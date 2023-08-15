/**
 * https://docs.oracle.com/javase/8/docs/technotes/guides/io/fsp/zipfilesystemprovider.html
 *
 * Exemple :
 * C:\Local\e-pmsi\fichiers-rss-mco\2023\M03\pmsi01db>C:\app\gpmsi\v1.1\gpmsi.bat -script C:\app\gpmsi\v1.1\scripts\groovy\fr\karadimas\gpmsi\druides\extraire_sejours.groovy -a:input C:\Users\hkaradimas\AppData\Roaming\ATIH\DRUIDES\sauvegarde\910019447\910019447.2023.3.SEJOURS.SEJOURS.56f12aae-708b-4f0c-bd7a-f03495e7bff3.zip
 */
package fr.karadimas.gpmsi.druides

//import java.util.zip
import java.nio.file.FileSystems
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path
import java.net.URI



def list(Path p, String indent) {
    Files.list(p).each {child->
        println("$indent child:'$child'")
        if (Files.isDirectory(child)) {
            list(child, indent + "  ")
        }
        if (child.toString().endsWith('.zip')) {
            subUriStr = child.toUri().toString()
            println("It's a ZIP !! ($subUriStr)")
            FileSystem subfs = FileSystems.newFileSystem(new URI(subUriStr), null);
            list(subfs.getPath('/'), indent + "!!")
        }
    }
}

entree = args['input']
println("Extraction fichier $entree")

Map<String, String> env = new HashMap<>(); 
env.put("create", "false"); //cf. https://docs.oracle.com/javase/8/docs/technotes/guides/io/fsp/zipfilesystemproviderprops.html
env.put("encoding", "UTF-8");
Path zipfile = Paths.get(entree);
println("zipfile:$zipfile")
zipfileUri = zipfile.toUri()
println("zipfileUri:$zipfileUri")

FileSystem fs = FileSystems.newFileSystem(new URI("jar:"+zipfileUri), env, null);

rootDirs = fs.rootDirectories

println("Roots $rootDirs") // c'est juste '/' et rien d'autre

Path rootPath = fs.getPath("/")

list(rootPath, "")

