/**:encoding=UTF-8:
 * Pour tous les fichiers .dbf ccam du répertoire courant, les transférer vers une base h2,
 * en appelant fr.gpmsi.nx.ccam.Dbf2H2.
 * Attention les tables sont à chaque fois détruites, puis recréés !
 *
 * Parametres :
 * - a:h2url &lt;url_base_h2%gt;
 *
 * Exemple 1 :
 * C:\Users\Harry\Downloads\classement\PMSI\Ameli\NX\CCAM\CCAM07500_DBF_PART3
 * %GPMSI_BASE%\v@PROJECT_VERSION@\gpmsi -script %GPMSI_BASE%\v@PROJECT_VERSION@\scripts\groovy\dbf_ccam_tout_vers_h2.groovy -a:h2url jdbc:h2:file:./ccam
 * Exemple 2 :
 * C:\Local\CCAM\v78\dbf>%GPMSI_BASE%\v@PROJECT_VERSION@\gpmsi -script %GPMSI_BASE%\v@PROJECT_VERSION@\scripts\groovy\dbf_ccam_tout_vers_h2.groovy  -a:h2uri jdbc:h2:file:./ccam
 * C:\Local\CCAM\v78\dbf
 * DROP des tables
 * Creation des tables
 * Transfert des dbf vers les tables
 * 3898146
 * Transfert terminé
 * 3899416 enregistrements transférés en 575.891 secondes
 *
 * (10 minutes, taille base ccam.mv.db  : 159 248 384 octets)
 */
import fr.gpmsi.nx.ccam.Dbf2H2

h2url = args.h2uri + ";DB_CLOSE_ON_EXIT=FALSE" //il faut ajouter DB_CLOSE_ON_EXIT=FALSE car on ferme nous-même la base en mode COMPACT
currentDir = new File(System.getProperty('user.dir'))
println currentDir
dbf2H2 = new Dbf2H2()
dbf2H2.dbfDir = currentDir
dbf2H2.connectH2(h2url, 'sa', '')

dbf2H2.destructiveCopy()
dbf2H2.gsql.close()
