/**:encoding=UTF-8:
 * Chercher dans le répertoire passé en argument "input" un fichier qui se termine par ".in.zip" et
 * en extraire le fichier qui se termine par ".rss.ini.txt"
 * Chercher dans le répertoire passé en argument "input" un fichier qui se termine par ".out.zip" et
 * en extraire les fichiers qui se termine par ".rsa" ou ".tra.txt"
 *
 * Exemple :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2023\M03\pmsi01db
 * C:\app\gpmsi\v1.3\gpmsi.bat -script C:\app\gpmsi\v1.3\scripts\groovy\fr\karadimas\gpmsi\druides\extraire_druides_in_out.groovy -a:input ..\RSS+VH\DRUIDES
 */
package fr.gpmsi.druides

import java.nio.file.FileSystems
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path
import java.net.URI
import java.util.zip.ZipInputStream
import java.util.zip.ZipEntry
import java.util.zip.GZIPInputStream

/** Dezipper en vrac tout ce qu'il y a dans ce ZipInputStream, recursivement. */
def dezipVrac(ZipInputStream zis, String indent) {
    while ((ze = zis.getNextEntry()) != null) {
        println("$indent Traitement de ${ze.name}")
        if (ze.name.toUpperCase().endsWith('.ZIP')) {
            bytes = readAllStream(zis).toByteArray()
            println("$indent  (lecture interne de ${bytes.length} octets)")
            subZis = new ZipInputStream(new ByteArrayInputStream(bytes))
            dezipVrac(subZis , indent + '  ')
        }
        else {
            bytes = readAllStream(zis).toByteArray()
            File outFile = new File(ze.name) //ecriture dans le repertoire courant
            File outFileUniq = renUniq(outFile) //renommage au cas ou plusieurs fichiers avec le meme nom
            println("$indent  Ecriture de ${bytes.length} octets dans $outFileUniq")
            outFileUniq.bytes = bytes
            //si c'est un .gzip l'extraire par commodité
            if (ze.name.toUpperCase().endsWith('.GZIP')) {
                File uncompressedOutFile = new File(ze.name[0..-6]) //suppression des 5 derniers caractères (la fin commence à -1)
                File uncompressedOutFileUniq = renUniq(uncompressedOutFile)
                println("$indent  Ecriture de ${bytes.length} octets dans $uncompressedOutFileUniq")
                GZIPInputStream gzin = new GZIPInputStream(new ByteArrayInputStream(bytes))
                expandedOutBytes = readAllStream(gzin).toByteArray()
                uncompressedOutFileUniq.bytes = expandedOutBytes
            }
        }
        zis.closeEntry()
    }
    zis.close()
}

/** Lister recursivement ce ZipInputStream, utile pour verifications */
def listZip(ZipInputStream zis, String indent) {
    while ((ze = zis.getNextEntry()) != null) {
        println("$indent/ze:${ze.name}")
        if (ze.name.toUpperCase().endsWith('.ZIP')) {
            bytes = readAllStream(zis).toByteArray()
            println("Extrait ${bytes.length} bytes")
            subZis = new ZipInputStream(new ByteArrayInputStream(bytes))
            listZip(subZis , indent + '!!')
        }
        zis.closeEntry()
    }
    zis.close()
}

/** lit tout le flux et retourne son contenu dans un ByteArrayOutputStream. Marche pour Zip et Gzip. */
def readAllStream(FilterInputStream fis) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream()
    byte[] buf = new byte[8096] //buffer intermediaire
    //lire tout dans le contenu et le transférer dans le ByteArrayOutputStream
    while ((nbytesread = fis.read(buf, 0, buf.length)) != -1) {
        bos.write(buf, 0, nbytesread)
    }
    return bos
}

/** changer le nom de f si f existe déjà, pour qu'il soit unique, en lui rajoutant _2 ou _3, etc.
 * Echoue au delà de 9999
 * Retourne le nouveau nom de fichier
 */
def renUniq(File f) {
    int n = 1
    if (!f.exists()) return f;
    while (n < 10000) {
        File f2 = new File((File)f.parent, f.name + '_' + n)
        if (!f2.exists()) return f2
        n++
    }
    throw new Exception("N'arrive pas a renommer de facon unique $f")
}

entree = args['input']
println("Extraction fichier $entree")

ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(entree)))

//listZip(zis, '') //utiliser cette ligne à la place de la suivante pour des recherches en cas de fichier à problème
//dezipper tous le fichiers rencontrés, en vrac, dans le répertoire courant.
dezipVrac(zis, '')
