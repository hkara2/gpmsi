/**☺:encoding=UTF-8:
 * Pour un argument de fichier inout (fichier zip) qui doit finir
 * par .in.zip ou .out.zip, extrait les fichiers dont les suffixes correspondent.
 * Si on passe un .in.zip, le .out.zip est aussi inclus, et vice-versa.
 *
 * Gabarit pour un nouveau script. A modifier selon les besoins.
 * Contient des exemples pour l'utilisation de input, output, un argument, un flag.
 * Arguments :
 * -a:input chemin_d_un_fichier_zip
 * -a:suffixe suffixe_desire          designe un suffixe qu'on veut extraire (il peut y en avoir plusieurs)
 *
 * Exemples :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\inout_extraire_fichiers.groovy -a:input 910019447.2025.00.MCO.SEJOURS.SEJOURS.20260121182027.in.zip -a:suffixe .tra.txt -a:suffixe .rsa -a:suffixe .rss.ini.txt
 */
import java.nio.file.Path
import java.nio.file.Files
import java.nio.file.FileSystems
import java.nio.file.FileSystem
import java.nio.file.StandardCopyOption

def makeFileSystem(URI srcZipUri) {
    def zipUri = URI.create('jar:'+srcZipUri) //doit etre 'jar:', pas 'zip:'
    //println "Utilisation de $srcZipUri"
    FileSystems.newFileSystem(zipUri, Collections.emptyMap())
}

def extractAll(FileSystem fs, Path toDirectory, String[] suffixes)
throws IOException 
{
    fs.getRootDirectories().each {root -> {
        //println "root : $root"
        Files.walk(root).each {path ->
          suffixes.each {suffix ->
              //println "suffix : '$suffix', path : '$path'"
              if (path.toString().endsWith(suffix)) {
                  //println "Copie de $path -> $toDirectory"
                  Files.copy(path, 
                             new File(toDirectory.toFile(), path.getFileName().toString()).toPath(), 
                             StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES)
              }
          }//each suffix
        }//each path
      }//each root
    }
}

inZipPath = ""
outZipPath = ""

if (args.input.endsWith ".in.zip") {
    inZipPath = args.input
    outZipPath = inZipPath.replace(".in.zip", ".out.zip")
}

if (args.input.endsWith ".out.zip") {
    outZipPath = args.input
    inZipPath = outZipPath.replace(".out.zip", ".in.zip")
}

if (inZipPath.length() == 0) throw new Exception('Erreur pas de fichier donne en entree')

//println "inZipPath : $inZipPath"
//println "outZipPath : $outZipPath"

suffixes = args.suffixe as String[]
//println "suffixes : $suffixes"

inZipFile = new File(inZipPath)
inZipFileUri = inZipFile.toURI()
inZipFs = makeFileSystem(inZipFileUri)
destDirPath = inZipFile.canonicalFile.parentFile.toPath()
extractAll(inZipFs, destDirPath, suffixes)

outZipFile = new File(outZipPath)
outZipFileUri = outZipFile.toURI()
outZipFs = makeFileSystem(outZipFileUri)
destDirPath = outZipFile.canonicalFile.parentFile.toPath()
extractAll(outZipFs, destDirPath, suffixes)
