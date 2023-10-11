/**☺:encoding=UTF-8:
 * Ameliorer le rapport des erreurs GENRHA afin qu'il soit plus exploitable.
 * Arguments :
 * legerrdet : le fichier de GENRHA qui correspond à leg.errDet.txt . Optionnel.
 *     Si l'argument n'est pas donné, prend le fichier leg.errDet.txt du
 *     répertoire de sortie de GENRHA
 * legerr :  le fichier de GENRHA qui correspond à leg.err.txt . Optionnel.
 *     Si l'argument n'est pas donné, prend le fichier leg.err.txt du
 *     répertoire de sortie de GENRHA
 * rhs : le fichier des RHS groupés qui a servi à GENRHA. Optionnel. 
 *     Si l'argument n'est pas donné, analyse le fichier "Rapport.xml" du
 *     répertoire de sortie de GENRHA pour en extraire le chemin du fichier
 *     qui a été donné en entrée à GENRHA
 * output : le fichier de sortie qui contiendra les erreurs en clair avec les
 *     numéros de dossier. Optionnel. Si l'argument n'est pas donné, produit
 *     dans le répertoire de sortie de GENRHA un fichier leg.err.decode.csv
 * #210413 v.2 Harry KARADIMAS
 */
import fr.karadimas.gpmsi.StringTable
import fr.karadimas.gpmsi.CsvDestination
import static groovy.io.FileType.*
import groovy.xml.XmlSlurper

appdataDirPath = System.getenv()['APPDATA']
genrhaDirPath = appdataDirPath + '\\ATIH\\GENRHA\\log'
genrhaDir = new File(genrhaDirPath)

//TYPERR;CODERR;LIBELLE;EFFERR;NBRHS CONCERNES
legerrdetPath = args['legerrdet']

if (legerrdetPath == null || legerrdetPath == '') {
    def paths = []
    genrhaDir.eachFileMatch FILES, ~/.*leg\.errDet\.txt/, { paths << it.path }
    println paths
    if (paths.size() > 1) {
        throw new Exception("Erreur : il y a plus d'un fichier .leg.errDet.txt dans le repertoire $genrhaDirPath")
    }
    if (paths.size() < 1) {
        throw new Exception("Erreur : il n'y a pas de fichier .leg.errDet.txt dans le repertoire $genrhaDirPath")
    }
    legerrdetPath = paths[0]
    println "Utilisation pour .leg.errDet.txt de '$legerrdetPath'"
}

//[FINESS] ;[PERIODE];[ANNEE] ;[N° DE SEJOUR SSR] ;[N° SEQUENCIEL DU RHS];[NB ERREUR] ;[LISTE DES ERREURS]
//0         1         2        3                   4                      5            6 
legerrPath = args['legerr']

if (legerrPath == null || legerrPath == '') {
    def paths = []
    genrhaDir.eachFileMatch FILES, ~/.*leg\.err\.txt/, { paths << it.path }
    println paths
    if (paths.size() > 1) {
        throw new Exception("Erreur : il y a plus d'un fichier .leg.err.txt dans le repertoire $genrhaDirPath")
    }
    if (paths.size() < 1) {
        throw new Exception("Erreur : il n'y a pas de fichier .leg.err.txt dans le repertoire $genrhaDirPath")
    }
    legerrPath = paths[0]
    println "Utilisation pour .leg.err.txt de '$legerrPath'"
}


legerrdetTable = new StringTable('LEGERRDET')
legerrdetFile = new File(legerrdetPath)
if (legerrdetFile.length() > 0) legerrdetTable.readFrom(legerrdetFile)
println "legerrdet : ${legerrdetTable.rowCount} rangees lues"

legerrTable = new StringTable('LEGERR')
legerrFile = new File(legerrPath)
if (legerrFile.length() > 0) legerrTable.readFrom(legerrFile)
println "legerr : ${legerrTable.rowCount} rangees lues"

rhsTupleListsByNssr = [:]

rhsPath = args['rhs']
if (rhsPath == null || rhsPath == '') {
    def rapportFile = new File(genrhaDir, 'Rapport.xml')
    def rapport = new XmlSlurper().parse(rapportFile)
    //println "Rapport : $rapport"
    println "Rapport ..."
    def cont = rapport.LOG[0].CONT.text()
    //println cont
    def inputPaths = []
    def prefix = '  Fichier d\'entr\u00e9e  : ' //le fichier d'entrée commence par ce préfixe
    cont.eachLine { 
        if (it.startsWith(prefix)) inputPaths << it.substring(prefix.length()) 
    }
    println inputPaths
    if (inputPaths.size() > 1) throw new Exception("Erreur plus d'un fichier d'entree trouvé dans Rapport.xml !!")
    if (inputPaths.size() < 1) throw new Exception("Erreur fichier d'entree non trouvé dans Rapport.xml !!")
    rhsPath = inputPaths[0]
}

outputPath = args['output']
if (outputPath == null || outputPath == '') {
    //il n'y a pas d'argument donné pour le fichier de sortie
    //la sortie va se faire dans un fichier qui portera le meme nom que le
    //leg.err.txt, sauf qu'il finira par leg.err.decode.txt
    outputPath = legerrPath[0..-4] + 'decode.csv'
    println "Utilisation pour outputPath de: '$outputPath'"
}

rhs {
    input rhsPath
    output outputPath

    onInit() {
    }

    onItem() { item ->
        def rhs = item.rhs
        def nssr = rhs.txtNSSR
        def nadl = rhs.txtNADL
        def nsem = rhs.txtNSEM
        def tuple = [nssr, nadl, nsem]
        def tupleList = rhsTupleListsByNssr[nssr]
        if (tupleList == null) {
            //println "Creating new list for '$nssr'"
            tupleList = []
            rhsTupleListsByNssr.put(nssr, tupleList)
        }
        tupleList << tuple
        //println "nssr:'$nssr',tuple:$tuple, tupleList:$tupleList"
    }

    onEnd() {
        d = new CsvDestination(new File(outputFilePath), "Cp1252")
        d.f('nsejssr')
        d.f('nadl')
        d.f('nseq')
        d.f('nsem')
        d.f('nberr')
        d.f('numerr')
        d.f('erreur')
        d.f('libelle')
        d.endRow()
        def nrows = legerrTable.rowCount
        for (int i = 0; i < nrows; i++) {
            def nssr = legerrTable.getValue(i, 3).trim()
            def nseq = legerrTable.getValue(i, 4).trim() as int
            def nberr = legerrTable.getValue(i, 5).trim() as int
            //println "nssr:'$nssr'"
            def ssrTupleList = rhsTupleListsByNssr[nssr]
            if (ssrTupleList == null) throw new Exception("Enregistrements pour '$nssr' non trouves")
            //println "ssrTupleList:$ssrTupleList"
            def nadl = ssrTupleList[0][1]
            def nsem = ssrTupleList[nseq-1][2]
            for (int j = 1; j <= nberr; j++) {
                def codeErr = legerrTable.getValue(i, 5 + j)
                def nomErr = legerrdetTable.find('CODERR', codeErr, 'LIBELLE')
                d.f(nssr.trim())
                d.f(nadl) //nadl
                d.f(nseq) //nseq
                d.f(nsem) //nsem
                d.f(nberr) //nberr
                d.f(j) //numerr
                d.f(codeErr) //le code erreur, commence a 6 dans legerrTable
                d.f(nomErr)
                d.endRow()
            }
        }
        d.close()
    }

}

