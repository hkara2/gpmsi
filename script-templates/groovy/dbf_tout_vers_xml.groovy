/**
 * Pour tous les fichiers .dbf du répertoire courant, produire un fichier .xml
 * équivalent, en appelant dbf2xml.groovy
 * Exemple :
 * C:\Users\Harry\Downloads\classement\PMSI\Ameli\NX\CCAM\CCAM07500_DBF_PART3
 * %GPMSI_BASE%\v@PROJECT_VERSION@\gpmsi -script %GPMSI_BASE%\v@PROJECT_VERSION@\scripts\groovy\dbf_tout_vers_xml.groovy
 */
currentDir = new File(System.getProperty('user.dir'))
println currentDir
currentDir.eachFile {f->
    if (f.name.endsWith ".dbf") {
        println f
        def outputFile = new File(f.parent, f.name.replace(".dbf", "_dbf.xml"))
        def argsp = ["-run", "fr.gpmsi.dbf2xml", "-a:input", f.toString(), "-a:output", outputFile.toString()] as String[]
        println "Appel de Groovy avec $argsp"
        fr.gpmsi.Groovy.main(argsp)
    }
}