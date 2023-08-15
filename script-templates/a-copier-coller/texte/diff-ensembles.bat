rem Comparaison des ensembles A et B, envoi des elements communs AB et des differences a et b
rem Copier-coller puis adapter
setlocal enableextensions

if not exist GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@
set APP=%GPMSI_HOME%\gpmsi.bat
rem Chemin du script.
set SCRIPT=%GPMSI_HOME%\scripts\groovy\lignes_comparer_ensembles.groovy
rem Fichier a comparer A
set A=<chemin_local_du_fichier_a>
rem Fichier a comparer B
set B=<chemin_local_du_fichier_b>
rem Prefixe des fichiers de sortie
set C=resultat_ab
call %APP% -script "%SCRIPT%" -a:input_a "%A%" -a:input_b "%B%" -a:output_a "%C%_a_seul.csv" -a:output_b "%C%_b_seul.csv" -a:output_ab "%C%_ab_commun.csv"
