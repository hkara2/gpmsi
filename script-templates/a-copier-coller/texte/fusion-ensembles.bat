rem Fusion des ensembles A et B
setlocal enableextensions

if not exist GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@
set APP=%GPMSI_HOME%\gpmsi.bat
rem Chemin du script.
set SCRIPT=%GPMSI_HOME%\scripts\groovy\lignes_fusionner_ensembles.groovy
rem Fichier a fusionner A
set A=<chemin_du_fichier_a>
rem Fichier a fusionner B
set B=<chemin_du_fichier_b>
rem Fichier de sortie
set C=<chemin_du_fichier_de_sortie>
call "%APP%" -script "%SCRIPT%" -a:input_a "%A%" -a:input_b "%B%" -a:output "%C%"
