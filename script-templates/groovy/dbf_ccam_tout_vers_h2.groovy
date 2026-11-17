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
 * C:\Local\CCAM\v78\dbf>%GPMSI_BASE%\v@PROJECT_VERSION@\gpmsi -script %GPMSI_BASE%\v@PROJECT_VERSION@\scripts\groovy\dbf_ccam_tout_vers_h2.groovy  -a:h2url jdbc:h2:file:./ccam
 * C:\Local\CCAM\v78\dbf
 * DROP des tables
 * Creation des tables
 * Transfert des dbf vers les tables
 * 3898089 enregistrements
 * Transfert terminé
 * 3899416 enregistrements transférés en 567.051 secondes
 *
 * Ajout colonne R_ACTE(DT_FIN_EFFET) ...
 * ... en 27.64 secondes
 *
 * Ajout index sur R_ACTE(COD_ACTE) ...
 * ... en 11.969 secondes
 *
 * Ajout index sur R_ACTE(COD_ACTE, DT_EFFET) ...
 * ... en 0.795 secondes
 *
 * Ajout valeurs dans R_ACTE(DT_FIN_EFFET) ...
 * ... en 783.065 secondes
 *
 * Ajout valeurs de fin 2099-12-31 dans R_ACTE(DT_FIN_EFFET) ...
 * ... en 20.289 secondes
 *
 * Compactage de la base ...
 * ... en 93.277 secondes
 *
 * Temps total d'execution : 1504.247 secondes
 *
 * (25 minutes,  taille base ccam.mv.db  : 66 506 752
 *
 */
import fr.gpmsi.nx.ccam.Dbf2H2

//il faut ajouter DB_CLOSE_ON_EXIT=FALSE car on ferme nous-même la base
//en mode COMPACT, et le temps max de compactage est censé être 200 ms,
//on l'augmente pour le mettre à 30 mn (1 800 000 ms)
h2url = args.h2url + ";DB_CLOSE_ON_EXIT=FALSE;MAX_COMPACT_TIME=1800000" 
currentDir = new File(System.getProperty('user.dir'))
println currentDir
dbf2H2 = new Dbf2H2()
dbf2H2.dbfDir = currentDir
dbf2H2.connectH2(h2url, 'sa', '')

dbf2H2.destructiveCopy()
dbf2H2.gsql.close()
