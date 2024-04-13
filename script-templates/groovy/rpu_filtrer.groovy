/**:encoding=UTF-8:
 * Filtrer fichier RPU sur criteres.
 * Pour l'instant le seul critere est le mois a garder.
 * Il y a aussi une option pour remplacer la date d'extraction, ce qui est
 * utile pour les renvois a Sesan.
 *
 * Exemple :
 * cd C:\Local\Urgences-Smur\RPU\exports\2023\M01\240220_1807
 * c:\app\gpmsi\v1.3\gpmsi.bat ^
 * -script c:\app\gpmsi\v1.3\scripts\groovy\rpu_filtrer.groovy ^
 * -a:input RPU_3702_20240220180716.txt ^
 * -a:mois 12
 * -a:extract "27/02/2024 12:32:00"
 * -a:output RPU_3702_20240220180716_12.txt
 *
 * Test fait 240227 sur decoupage sur 12 mois, la somme du nombre des passages
 * correspond bien.
 */
import groovy.xml.XmlSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.YearMonth
import static fr.karadimas.gpmsi.StringUtils.isEmpty

isoDtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME
sfrDtf = DateTimeFormatter.ofPattern('dd/MM/yyyy') //short
frDtf = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm') //normal
frDtff = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm:ss') //full

def FileWriter outfw = new FileWriter(args.output)
def mb = new StreamingMarkupBuilder()

mois = args.mois as Integer

def testInclusionMoisEntree = {p->
    //pas de mois défini -> inclusion forcée
    if (mois == null) return true
    //extraire mois d'entrée et comparer
    def entreeDateTime = LocalDateTime.parse(p.ENTREE.text(), frDtf)
    return entreeDateTime.monthValue == mois
}

//Le test d'inclusion teste tous les sous-tests (1 seul sous-test pour l'instant)
def testInclusion = {p->
    testInclusionMoisEntree(p)
}

def oscour = new XmlSlurper().parse(new File(args.input))

def finess = oscour.ETABLISSEMENT.FINESS.text()
def ordre = oscour.ETABLISSEMENT.ORDRE.text()
def extract = oscour.ETABLISSEMENT.EXTRACT.text()
if (!isEmpty(args.extract)) extract = args.extract //remplacer si l'option a été définie
def datedebut = oscour.ETABLISSEMENT.DATEDEBUT.text()
def datefin = oscour.ETABLISSEMENT.DATEFIN.text()
//si "mois" est défini, changer les bornes
if (mois != null) {
    datedebutLocalDateTime = LocalDate.parse(datedebut, sfrDtf)
    def annee = datedebutLocalDateTime.year
    def nouvDatedebutLd = LocalDate.of(annee, mois, 1)
    def nouvDatefinLd = new YearMonth(annee, mois).atEndOfMonth()
    datedebut = nouvDatedebutLd.format(sfrDtf)
    datefin = nouvDatefinLd.format(sfrDtf)
}


def patientsAGarder = []

oscour.PASSAGES.PATIENT.each {p->
    if (testInclusion(p)) patientsAGarder << p
}

nbPassages = patientsAGarder.size()

def resultXml = mb.bind {
    OSCOUR {
        ETABLISSEMENT {
            FINESS(finess)
            ORDRE(ordre)
            EXTRACT(extract)
            DATEDEBUT(datedebut)
            DATEFIN(datefin)
        }
        PASSAGES {
            mkp.yield patientsAGarder
        }
    }
}

outfw.write(XmlUtil.serialize(resultXml))
outfw.close()

println "$nbPassages passages patient filtres pour mois $mois"
