/**:encoding=UTF-8:
 * Décoder le fichier volume2cmd.zip qui contient par CMD les listes d'actes et
 * de diagnostics.
 * En sortie, il y a trois fichiers : listes d'actes , listes de diagnostics et
 * diagnostics d'entrée dans une CMD.
 * Les fichiers sont écrits dans le répertoire courant et ont les noms fixes
 * listesA.csv , listesD.csv et diagsEntreeCmd.csv.
 *
 * Arguments :
 * -a:input chemin_du_fichier_volume2cmd.zip
 * Exemple :
 * C:\Users\hkaradimas\Downloads\PMSI\ATIH\ghm\manuel_des_ghm\2023>c:\app\gpmsi\v1.3\gpmsi.bat -script c:\app\gpmsi\v1.3\scripts\groovy\ghm_decoder_listes_vol2.groovy -a:input volume2cmd.zip
 */
import java.util.zip.ZipInputStream
import java.util.zip.ZipEntry
import java.util.zip.GZIPInputStream
import java.nio.charset.Charset
import java.util.regex.Matcher
import fr.karadimas.gpmsi.CsvDestination
import fr.karadimas.gpmsi.StringTable

/** lit tout le flux et retourne son contenu dans un ByteArrayOutputStream. Marche pour Zip et Gzip. */
ByteArrayOutputStream readAllStream(FilterInputStream fis) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream()
    byte[] buf = new byte[8096] //buffer intermediaire
    //lire tout dans le contenu et le transférer dans le ByteArrayOutputStream
    while ((nbytesread = fis.read(buf, 0, buf.length)) != -1) bos.write(buf, 0, nbytesread)
    return bos
}

/** Lire l'entrée zip courante dans une StringTable */
StringTable readTable(FilterInputStream fis) {
    ByteArrayOutputStream bos = readAllStream(fis)
    StringTable tbl = new StringTable("TBL")
    tbl.readFrom(new InputStreamReader(new ByteArrayInputStream(bos.toByteArray())), ';' as char) //on laisse le charse par défaut ici, on se fiche des accents
    return tbl
}

def writeToDest(csvDest, nCmd, nListe, tbl) {
    tbl.each {rowValues ->
        csvDest.f(nCmd)
        csvDest.f(nListe)
        csvDest.f(rowValues[0])
        csvDest.endRow()
    }
}

def writeEntreesToDest(csvDest, nCmd, tbl) {
    tbl.each {rowValues ->
        csvDest.f(nCmd)
        csvDest.f(rowValues[0])
        csvDest.endRow()
    }
}

File inFile = new File(args.input)
File outFileA = new File("listesA.csv")
CsvDestination da = new CsvDestination(outFileA)
da.f('cmd')
da.f('liste')
da.f('code')
da.endRow()

File outFileD = new File("listesD.csv")
CsvDestination dd = new CsvDestination(outFileD)
dd.f('cmd')
dd.f('liste')
dd.f('code')
dd.endRow()

File outFileEnt = new File("diagsEntreeCmd.csv")
CsvDestination de = new CsvDestination(outFileEnt)
de.f('cmd')
de.f('code')
de.endRow()

ZipInputStream zis = new ZipInputStream(new FileInputStream(inFile), Charset.forName("Cp850")) //pour la France Cp850 c'est bien
def patternListe = ~/CMD_\d+\/(\d+)_Liste ([AD])-(\d+)_/
def patternDent = ~/Diagnostics d entr.e dans la CMD n°(\d+)/

while ((ze = zis.getNextEntry()) != null) {
    println("Traitement de ${ze.name}")
    //exemple d'entrée à traiter : CMD_28/28_Liste A-314_RCMI et techniques spéciales.csv
    Matcher mListe = ze.name =~ patternListe
    //println("Matcher : $mListe")
    if (mListe) {
        //on est dans une entrée de liste
        String cmd = mListe.group(1)
        String type = mListe.group(2)
        String nListe = mListe.group(3)
        println("$type Cmd $cmd liste $nListe")
        tbl = readTable(zis)
        writeToDest((type == 'A' ? da : dd), cmd, nListe, tbl)
    } else {
        //tester si liste de diagnostics d'entrée
        Matcher mDent = ze.name =~ patternDent
        if (mDent) {
            String cmd = mDent.group(1)
            println("Entree dans cmd $cmd")
            tbl = readTable(zis)
            writeEntreesToDest(de, cmd, tbl)
        }
        else {
            //on ne traite pas les autres entrées
            println("Entree ignoree.")
        }
    }
}//while
da.close()
dd.close()
de.close()

