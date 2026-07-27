/**
 * Pour tous les fichiers .dbf ccam du répertoire courant, les transférer vers une base h2,
 * en appelant fr.gpmsi.nx.ccam.Dbf2H2
 * Exemple :
 * C:\Users\Harry\Downloads\classement\PMSI\Ameli\NX\CCAM\CCAM07500_DBF_PART3
 * %GPMSI_BASE%\v@PROJECT_VERSION@\gpmsi -script %GPMSI_BASE%\v@PROJECT_VERSION@\scripts\groovy\dbf_tout_vers_xml.groovy
 */
import fr.gpmsi.nx.ccam.Dbf2H2

currentDir = new File(System.getProperty('user.dir'))
println currentDir
dbf2H2 = new Dbf2H2()
dbf2H2.dbfDir = currentDir
dbf2H2.connectH2('jdbc:h2:file:C:/t/ccam', 'sa', '')

dbf2H2.destructiveCopy()
dbf2H2.gsql.close()
