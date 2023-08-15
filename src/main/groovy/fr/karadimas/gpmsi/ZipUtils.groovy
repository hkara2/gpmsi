package fr.karadimas.gpmsi
import java.net.URI
import java.util.Collections
import java.nio.file.FileSystems
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.Path

/**
 * Quelques utilitaires pour faciliter le travail avec des fichiers Zip.
 * @author hkaradimas
 *
 */
class ZipUtils {
    
    static Object openZip(String path) {
        def zipFile = new File(path)
        def zipFileUri = zipFile.toURI()
        def zipUri = URI.create('jar:'+zipFileUri) //doit etre 'jar:', pas 'zip:'
        //println "zipUri:$zipUri"
        def fs = FileSystems.newFileSystem(zipUri, Collections.emptyMap())
        return fs
    }
    
    /**
     * Extraire le premier fichier qui verifie le pattern (ex: 'glob:**.txt')
     * et le copie dans un fichier temporaire dont le Path est retourne
     */
    static Path getAsTempFile(FileSystem zfs, String pattern) {
		def rootDirs = zfs.getRootDirectories()
		def rd = null
		rootDirs.each {d->
		    //println "d:$d"
		    rd = d
		}
		def pm = zfs.getPathMatcher(pattern)
		Path tmpPath = null
		Files.walk(rd).each {p->
		    if (tmpPath != null) return //court circuit si fichier a deja ete trouve
		    //println p
		    //println p.class //class com.sun.nio.zipfs.ZipPath
		    if (pm.matches(p)) {
		        //println 'Match !'
		        tmpPath = Files.createTempFile('ZipUtils','.txt')
		        Files.copy(p, tmpPath, StandardCopyOption.REPLACE_EXISTING)
		        //tmpPath.toFile().deleteOnExit()
		    }
		}
		return tmpPath
    }
    
}
