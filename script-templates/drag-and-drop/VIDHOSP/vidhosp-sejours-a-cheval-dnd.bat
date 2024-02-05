rem Dans un fichier VIDHOSP extraction des fichiers a cheval
rem Exemple d'utilisation :vidhosp-sejours-a-cheval-dnd.bat
rem vidhosp-sejours-a-cheval-dnd.bat "C:\Local\GROUPAGE\2019\M12\200117-pmsipilot\VH200117\VIDHOSP_MCO.txt"
rem N.B. marche si on fait un "glisser-deposer" sur le fichier bat

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat

rem Chemin du script.
set SCRIPT=%GPMSI_HOME%\scripts\groovy\vidhosp_sejours_a_cheval.groovy

rem Fichier VIDHOSP a analyser (sans les guillemets)
set A=%~1
rem Fichier qui contiendra les NADL des sejours a cheval
set B=%~dpn1_sej_a_cheval.csv
call %APP% -script %SCRIPT% -a:input "%A%" -a:output "%B%"
