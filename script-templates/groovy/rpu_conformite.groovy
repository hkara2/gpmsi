/**:encoding=UTF-8:
 * Corrige un RPU pour qu'il soit conforme à l'envoi vers SESAN :
 * - borne DATEDEBUT : date d'entrée la plus ancienne
 * - borne DATEFIN : date de sortie la plus récente
 * Exemple :
 * cd C:\Local\GROUPAGE\2022\M12\RPU\230127-Etampes-1er-semestre
 * c:\app\gpmsi\exec -script c:\app\gpmsi\v1.0\scripts\groovy\rpu_conformite.groovy -a:input RPU_3700_20230127102329.xml -a:output RPU_3700_20230127102329_corrige.xml
 *
 */
import java.text.SimpleDateFormat

import groovy.util.XmlParser
import groovy.util.XmlNodePrinter

import static fr.karadimas.gpmsi.StringUtils.isTrimEmpty


FINESS_ETABLISSEMENT = '910001973' //a adapter a l'établissement

inputFilePath = args.input
if (inputFilePath == null) throw new Exception("Erreur il manque l'argument -a:input")
outputFilePath = args.output
if (outputFilePath == null) throw new Exception("Erreur il manque l'argument -a:output")

xp = new XmlParser()

OSCOUR = xp.parse(new File(inputFilePath))

formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm")
formatDateEtablissement = new SimpleDateFormat("dd/MM/yyyy")
dateEntreeMin = null
dateSortieMax = null
OSCOUR.PASSAGES.PATIENT.each {patient ->
    def dateEntreeStr = patient.ENTREE.text()
    //println "ENTREE $dateEntreeStr"
    if (!isTrimEmpty(dateEntreeStr)) {
        def dateEntree = formatDate.parse(dateEntreeStr)
        if (dateEntreeMin == null) dateEntreeMin = dateEntree
        else if (dateEntree < dateEntreeMin) dateEntreeMin = dateEntree
    }
    def dateSortieStr = patient.SORTIE.text()
    //println "SORTIE $dateSortieStr"
    if (!isTrimEmpty(dateSortieStr)) {
        def dateSortie = formatDate.parse(dateSortieStr)
        if (dateSortieMax == null) dateSortieMax = dateSortie
        else if (dateSortie > dateSortieMax) dateSortieMax = dateSortie
    }
    //println "$patient"
}

println "date entree min : $dateEntreeMin"
println "date sortie max : $dateSortieMax"

ETABLISSEMENT = OSCOUR.ETABLISSEMENT.find { it.FINESS.text() == FINESS_ETABLISSEMENT }

//println "ETABLISSEMENT : $ETABLISSEMENT"

//ajuster les dates avec ce qui a été collecté
ETABLISSEMENT.DATEDEBUT[0].value = formatDateEtablissement.format(dateEntreeMin)
ETABLISSEMENT.DATEFIN[0].value = formatDateEtablissement.format(dateSortieMax)

println "ETABLISSEMENT : $ETABLISSEMENT"

//ecrire maintenant le resultat
new File(outputFilePath).withWriter {
    def printer = new XmlNodePrinter(new PrintWriter(it))
    printer.preserveWhitespace = true
    printer.print(OSCOUR)
}
