/**☺:encoding=UTF-8:
 * Analyser un fichier RPU à la recherche d'anomalies.
 * Anomalies recherchées :
 * - date d'entrée supérieure à la date de sortie
 *
 * TODO : continuer
 *
 * Exemple :
 * cd C:\Local\RPU\exports\2020\01-03
 * gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rpu_analyser.groovy ^
 * -a:input RPU_7002_20200318145530.xml
 *
 */
import groovy.xml.XmlSlurper
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.nio.file.Files

isoDtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME
longIsoDtf = DateTimeFormatter.ofPattern('yyyyMMddHHmmss')
frDtf = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm')
lfrDtf = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm:ss')


def oscour = new XmlSlurper().parse(new File(args.input))
def finess = oscour.ETABLISSEMENT.FINESS.text()
def ordre = oscour.ETABLISSEMENT.ORDRE.text()
def extract = oscour.ETABLISSEMENT.EXTRACT.text()



