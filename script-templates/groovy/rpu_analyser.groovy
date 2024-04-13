/**☺:encoding=UTF-8:
 * Analyser un fichier RPU à la recherche d'anomalies.
 * Anomalies recherchées :
 * - date d'entrée supérieure à la date de sortie
 * - date de sortie manquante
 * - durée de séjour > 30 jours
 * - âge du patient à l'entrée > 120 ans
 *
 * TODO : continuer
 *
 * Exemple :
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi.bat -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rpu_analyser.groovy -a:input RPU_3700_20240222160347.txt
 *
 */
import groovy.xml.XmlSlurper
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.nio.file.Files
import static fr.karadimas.gpmsi.StringUtils.isTrimEmpty

isoDtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME
longIsoDtf = DateTimeFormatter.ofPattern('yyyyMMddHHmmss')
frDtf = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm')
lfrDtf = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm:ss')

/** Ramener assez d'info pour identifier le message : date de naissance, date d'entree */
def msgId(p) {
    def dateNaiss = p.NAISSANCE ? p.NAISSANCE.text() : ""
    def dateEntree = p.ENTREE.text()
    "$dateNaiss;$dateEntree"
}

/** mettre au pluriel */
def plur(nom, n) { n == 1 ? nom : nom + 's' }

def oscour = new XmlSlurper().parse(new File(args.input))
def finess = oscour.ETABLISSEMENT.FINESS.text()
def ordre = oscour.ETABLISSEMENT.ORDRE.text()
def extract = oscour.ETABLISSEMENT.EXTRACT.text()

nbAnalyses = 0
nbErreurs = 0
demin = null
demax = null
dsmin = null
dsmax = null

println "Analyse du fichier '${args.input}'"
println ""
println "NAISSANCE;ENTREE;champ;message"

oscour.PASSAGES.PATIENT.each {p->
    def entreeStr = p.ENTREE.text()
    def entreeDateTime = null
    def sortieStr = p.SORTIE.text()
    def sortieDateTime = null
    def msgInfoPrinted = false
    def mId = msgId(p)
    if (isTrimEmpty(sortieStr)) {
        println "$mId;SORTIE;champ vide"
        nbErreurs++
    }
    else {
        entreeDateTime = LocalDateTime.parse(entreeStr, frDtf)
        sortieDateTime = LocalDateTime.parse(sortieStr, frDtf)
        if (sortieDateTime.compareTo(entreeDateTime) < 0) {
            println "$mId;SORTIE;la sortie a lieu avant l entree"
            nbErreurs++
        }
        def dureeJours = ChronoUnit.DAYS.between(entreeDateTime, sortieDateTime) + 1
        if (dureeJours > 30) {
            println "$mId;SORTIE;duree du passage superieure a 30 jours"
            nbErreurs++

        }
    }
    if (demin == null || (entreeDateTime != null && entreeDateTime < demin)) demin = entreeDateTime
    if (demax == null || (entreeDateTime != null && entreeDateTime > demax)) demax = entreeDateTime
    if (dsmin == null || (sortieDateTime != null && sortieDateTime < dsmin)) dsmin = sortieDateTime
    if (dsmax == null || (sortieDateTime != null && sortieDateTime > dsmax)) dsmax = sortieDateTime

    nbAnalyses++
}//oscour.PASSAGES.PATIENT.each

println "Analyse terminee."
println "$nbAnalyses ${plur("passage", nbAnalyses)} ${plur("analyse", nbAnalyses)}."
println "$nbErreurs ${plur("erreur", nbErreurs)} ${plur("trouvee", nbErreurs)}."
deminStr = demin.format(frDtf)
demaxStr = demax.format(frDtf)
println "Dates entree : min : $deminStr, max : $demaxStr"
dsminStr = dsmin.format(frDtf)
dsmaxStr = dsmax.format(frDtf)
println "Dates sortie : min : $dsminStr, max : $dsmaxStr"

