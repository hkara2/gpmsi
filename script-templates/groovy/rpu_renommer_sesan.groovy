/**☺:encoding=UTF-8:
 * Produire une copie du fichier mais avec la convention de nommage SESAN :
 * Lettre O
 * N° FINESS Géographique
 * '_'
 * Contenu de la balise ORDRE (chiffre 0 en général)
 * '_'
 * Contenu de la balise EXTRACT au format AAAAMMJJHHmmSS
 * '.xml'
 *
 Analyser le fichier des RPU et donner date entreee min, date entree max,
 * date sortie min, date sortie max.
 *
 * Exemple :
 * cd C:\Local\RPU\exports\2020\01-03
 * gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rpu_renommer_sesan.groovy ^
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
LocalDateTime extract_dt = LocalDateTime.parse(extract, lfrDtf)
String extract_str = extract_dt.format(longIsoDtf)
def nouveauNom = 'O' + finess + '_' + ordre + '_' + extract_str + '.xml'

println "Nouveau nom : '$nouveauNom'"

File inFile = new File(args.input)
File outFile = new File(inFile.canonicalFile.parent, nouveauNom)
Files.copy(inFile.toPath(), outFile.toPath())


