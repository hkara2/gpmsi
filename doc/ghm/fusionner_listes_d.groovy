/**:encoding=UTF-8:
 * Rassembler tous les fichiers D-* dans un seul grand fichier "listesd.csv",
 * avec une ligne par liste, et sans les libelles.
 * Mettre ce script dans le répertoire qui contient les listes.
 * Exemple :
 * %GPMSI_BASE%\v2.1\gpmsi -script fusionner_listes_d.groovy
 */
import groovy.ant.FileNameFinder //ce n'est plus groovy.util !
import fr.gpmsi.StringUtils
import fr.gpmsi.StringTable
import fr.gpmsi.CsvDestination

codesByNomListe = [:]

//importer le fichier "listesd.csv" s'il existe
fListesd = new File("listesd.csv")
stListesd = new StringTable("listesd")
if (fListesd.exists()) stListesd.readFrom(fListesd)
else stListesd.declareColumnNames(['liste','codes'])

println "codes actuels"
stListesd.each {row->
    println "${row[0]} : ${row[1]}"
    def codes = row.codes
    codesByNomListe[row.liste] = codes.split(' ') as HashSet
}


def loadListFile(f, nomListe) {
    stList = new StringTable('codes', f)
    def codes = [] as HashSet
    stList.each {row->
        codes.add row.code
    }
    return codes
    //println "liste : $nomListe, codes : $codes"
}

def mergeList(nomListe, codes) {
    def codesActuels = codesByNomListe.get(nomListe)
    if (codesActuels == null) codesActuels = [] as HashSet
    codesActuels.addAll(codes)
    codesByNomListe.put(nomListe, codesActuels)
}

//trouve tous les fichiers D-*.txt du répertoire courant
def dFiles = new FileNameFinder().getFileNames('.', 'D-*.txt' /* includes */)

dFiles.each {fp->
    def f = new File(fp)
    def nomListe = StringUtils.removeExtension(f.name)
    //println nomListe
    mergeList(nomListe, loadListFile(f, nomListe))
}
//println dFiles

nouvelleStListesd = new StringTable("listesd")
nouvelleStListesd.declareColumnNames(['liste','codes'])

codesByNomListe.each {k, v->
    nouvelleStListesd.addRow(k, v.join(" "))
}

nouvelleStListesd.writeTo(new CsvDestination(fListesd))

