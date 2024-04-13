/**:encoding=UTF-8:
 * Fusionner deux fichiers A et B.
 * Enregistrer le resultat avec le nom de A + "_fusion_" + le nom de B
 *
 * Exemple :
 * cd C:\Local\Urgences-Smur\RPU\exports\2023\M01\240220_1807
 * c:\app\gpmsi\v1.3\gpmsi.bat ^
 * -script c:\app\gpmsi\v1.3\scripts\groovy\rpu_fusion_fichiers.groovy ^
 * -a:input_a RPU_3702_20240220180716.txt ^
 * -a:input_b RPU_3700_20240220180715.txt ^
 * -a:output RPU_3700_3702.xml
 *
 */
import groovy.xml.XmlSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.time.LocalDate
import static fr.karadimas.gpmsi.StringUtils.isEmpty

isoDtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME
frDtsf = DateTimeFormatter.ofPattern('dd/MM/yyyy') //short
frDtf = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm') //normal
frDtff = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm:ss') //full

/**
 * Transformer une liste d'éléments enfants en une chaîne de caractères.
 * Utilisé pour avoir la liste des actes et la liste des da.
 */
def toList(parentElement, childName) {
    def sb = "" << ""
    sb << parentElement[childName].join(', ')
    sb.toString()
}

def parseDate(str) {
    if (str == null || str.trim() == '') return null
    return LocalDateTime.parse(str, frDtf)
}

def maxDate(LocalDateTime a, LocalDateTime b) { a.isAfter(b) ? a : b }

def minDate(LocalDateTime a, LocalDateTime b) { a.isBefore(b) ? a : b }

def maxDate(LocalDate a, LocalDate b) { a.isAfter(b) ? a : b }

def minDate(LocalDate a, LocalDate b) { a.isBefore(b) ? a : b }

//verifier que les arguments sont présents
if (isEmpty(args.input_a)) throw new Exception("argument -a:input_a manquant")
if (isEmpty(args.input_b)) throw new Exception("argument -a:input_b manquant")
if (isEmpty(args.output)) throw new Exception("argument -a:output manquant")
    
//def outfw = new BufferedWriter(new FileWriter(args.output))
//envoi de l'en-tête
//outfw << "finess;cp;commune;naissance;sexe;entree;mode_entree;provenance;transport;transport_pec;motif;gravite;dp;liste_da;liste_actes;sortie;mode_sortie;destination;orient\r\n"

// deminStr = args['demin']
// if (deminStr == null) {
    // throw new Exception("demin (date d'entree minimum) manquant")
// }

demin = null

// println "demin : $demin"
// 
// demaxStr = args['demax']
// if (demaxStr == null) {
    // throw new Exception("demax (date de d'entree maximum) manquant")
// }

demax = null

dsmin = null
dsmax = null

//println "demax : $demax"

/*
La structure d'un RPU est très simple :
<OSCOUR>
  <ETABLISSEMENT>
    ...
  </ETABLISSEMENT>
  <PASSAGES>
    ...
  </PASSAGES>
</OSCOUR>
*/

def FileWriter outfw = new FileWriter(args.output)
def mb = new StreamingMarkupBuilder()

def oscourA = new XmlSlurper().parse(new File(args.input_a))
/* Exemple de Etablissement :
<ETABLISSEMENT>
    <FINESS>910001973</FINESS>
    <ORDRE>0</ORDRE>
    <EXTRACT>20/02/2024 18:07:15</EXTRACT>
    <DATEDEBUT>01/01/2023</DATEDEBUT>
    <DATEFIN>31/01/2023</DATEFIN>
  </ETABLISSEMENT>
*/
def finessA = oscourA.ETABLISSEMENT.FINESS.text()
def ordreA = oscourA.ETABLISSEMENT.ORDRE.text()
def extractA = oscourA.ETABLISSEMENT.EXTRACT.text()
def datedebutA = oscourA.ETABLISSEMENT.DATEDEBUT.text()
def datefinA = oscourA.ETABLISSEMENT.DATEFIN.text()

def oscourB = new XmlSlurper().parse(new File(args.input_b))
/* Exemple de Etablissement :
<ETABLISSEMENT>
    <FINESS>910001973</FINESS>
    <ORDRE>0</ORDRE>
    <EXTRACT>20/02/2024 18:07:15</EXTRACT>
    <DATEDEBUT>01/01/2023</DATEDEBUT>
    <DATEFIN>31/01/2023</DATEFIN>
  </ETABLISSEMENT>
*/
def finessB = oscourB.ETABLISSEMENT.FINESS.text()
def ordreB = oscourB.ETABLISSEMENT.ORDRE.text()
def extractB = oscourB.ETABLISSEMENT.EXTRACT.text()
def datedebutB = oscourB.ETABLISSEMENT.DATEDEBUT.text()
def datefinB = oscourB.ETABLISSEMENT.DATEFIN.text()

if (finessA != finessB) throw new Exception("Les finess sont differents ($finessA vs $finessB)")
if (ordreA != ordreB) throw new Exception("Les ordres sont differents ($ordreA vs $ordreB)")
LocalDateTime extractA_date = LocalDateTime.parse(extractA, frDtff)
LocalDateTime extractB_date = LocalDateTime.parse(extractB, frDtff)
LocalDateTime extractO_date = maxDate(extractA_date, extractB_date)
String extractO_str = frDtff.format(extractO_date)

LocalDate datedebutA_date = LocalDate.parse(datedebutA, frDtsf)
LocalDate datedebutB_date = LocalDate.parse(datedebutB, frDtsf)
LocalDate datedebutO_date = minDate(datedebutA_date, datedebutB_date)
String datedebutO_str = frDtsf.format(datedebutO_date)

LocalDate datefinA_date = LocalDate.parse(datefinA, frDtsf)
LocalDate datefinB_date = LocalDate.parse(datefinB, frDtsf)
LocalDate datefinO_date = minDate(datefinA_date, datefinB_date)
String datefinO_str = frDtsf.format(datefinO_date)

    /* Exemple de RPU :
      <CP>91150</CP> <!-- code postal de résidence. Format : 5 caractères -->
      <COMMUNE>ETAMPES</COMMUNE> <!-- nom de la commune de résidence. Format : caractères -->
      <NAISSANCE>02/03/1945</NAISSANCE> <!-- date de naissance. Format : JJ/MM/AAAA. vide correspond à incertain -->
      <SEXE>M</SEXE> <!-- sexe. Format : 1 caractère. Codes : M - masculin. F - féminin. I - inconnu -->
      <ENTREE>20/03/2020 14:44:00</ENTREE> <!-- date et heure d’entrée. Format : JJ/MM/AAAA hh:mm --> 
      <MODE_ENTREE></MODE_ENTREE> <!-- Mode d’entrée PMSI. Format : 1 caractère. Codes : 6 - mutation. 7 - transfert. 8 - domicile -->
      <PROVENANCE></PROVENANCE> <!-- Provenance PMSI. Format : 1 caractère. Codes : 1 - mutation ou transfert du MCO. 2 - mutation ou transfert du SSR. 3 - mutation ou transfert du SLD. 4 - mutation ou transfert du PSY. 5 - PE autre qu’organisationnelle. 6 - hospitalisation à domicile 2. . 2. Les codes 6 et 7 correspondent à des codes du PMSI et ont été ajoutés à la définition du RPU le 16/09/13.. InVS/DCAR/SSI SYSTÈME D’INFORMATION SUR L’ACTIVITÉ DES SERVICES D’URGENCES HOSPITALIERS. . 9. 7 - structure d’hébergement médicosociale. 8 - PE organisationnelle -->
      <TRANSPORT>PERSO</TRANSPORT> <!-- Mode de transport. Format : 5 caractères. Codes : PERSO - moyen personnel. AMBU - ambulance publique ou privée. VSAB - véhicule de secours et d’aide aux blessés. SMUR - véhicule de Service Mobile d’Urgence et de Réanimation. HELI - hélicoptère. FO - force de l’ordre -->
      <TRANSPORT_PEC>AUCUN</TRANSPORT_PEC> <!-- Mode de prise en charge pendant le transport. Format : 7 caractères. Codes : MED - médicalisée. PARAMED - paramédicalisée. AUCUN - sans prise en charge -->
      <MOTIF>plaie superficielle index droit</MOTIF> <!--motif de recours aux urgences. Format : alphanumérique. Codes : thésaurus SFMU -->
      <GRAVITE>1</GRAVITE> <!-- classification CCMU modifiée. Format : 1 caractère. Codes : 1 - état lésionnel ou pronostic fonctionnel jugé stable après le premier examen clinique éventuellement complété d’actes diagnostiques réalisés et interprétés au lit du malade, abstention d’actes complémentaires ou de thérapeutique. P - idem CCMU 1 avec problème dominant psychiatrique ou psychologique isolé ou associé à une pathologie somatique jugée stable. 2 - état lésionnel ou pronostic jugé stable, réalisation d’actes complémentaires aux urgences en dehors des actes diagnostiques éventuellement réalisés et interprétés au lit du malade et / ou d’actes thérapeutiques. 3 - état lésionnel ou pronostic fonctionnel jugé susceptible de s’aggraver aux urgences sans mettre en jeu le pronostic vital. 4 - situation pathologique engageant le pronostic vital aux urgences sans manœuvre de réanimation initiée ou poursuivie dès l’entrée aux urgences. 5 - situation pathologique engageant le pronostic vital aux urgences avec initiation ou poursuite de manœuvres de réanimation dès l’entrée aux urgences. D - patient décédé à l’entrée aux urgences sans avoir pu bénéficier d’initiation ou poursuite de manœuvres de réanimation aux urgences -->
      <DP></DP> <!-- diagnostic principal. Format : CIM 10  -->
      <LISTE_DA>
        <DA>I10</DA> <!-- Diagnostic associé. Format : CIM 10 -->
        <DA>E440</DA> <!-- InVS/DCAR/SSI SYSTÈME D’INFORMATION SUR L’ACTIVITÉ DES SERVICES D’URGENCES HOSPITALIERS...10. Diagnostic associé. Format : CIM 10  -->
      </LISTE_DA>
      <LISTE_ACTES >
        <ACTE>DEQP003</ACTE> <!-- Acte réalisé aux urgences. Format : CCAM  -->
      </LISTE_ACTES>
      <SORTIE>20/03/2020 14:45:00</SORTIE> <!-- Date et heure de sortie. Format : JJ/MM/AAAA hh :mm -->
      <MODE_SORTIE>8</MODE_SORTIE> <!-- Mode de sortie PMSI. Format : 1 caractère. Codes : 6 – mutation. 7 – transfert. 8 – domicile. 9 – décès -->
      <DESTINATION></DESTINATION> <!-- Destination PMSI. Format : 1 caractère. Codes : 1 – hospitalisation MCO. 2 – hospitalisation SSR. 3 – hospitalisation SLD. 4 – hospitalisation PSY. 6 – hospitalisation à domicile. 7 – structure d’hébergement médicosociale -->
      <ORIENT></ORIENT>
      */


def patientsA = []

oscourA.PASSAGES.PATIENT.each {p->
    patientsA << p
}

def patientsB = []

oscourB.PASSAGES.PATIENT.each {p->
    patientsB << p
}

def resultXml = mb.bind {
    OSCOUR {
        ETABLISSEMENT {
            FINESS(finessA)
            ORDRE(ordreA)
            EXTRACT(extractO_str)
            DATEDEBUT(datedebutO_str)
            DATEFIN(datefinO_str)
        }
        PASSAGES {
            mkp.yield patientsA
            mkp.yield patientsB
        }
    }
}

outfw.write(XmlUtil.serialize(resultXml))
outfw.close()

