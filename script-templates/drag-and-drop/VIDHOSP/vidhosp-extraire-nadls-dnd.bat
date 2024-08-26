rem Extraction des NADLs d'un fichier VIDHOSP
rem Le fichier de sortie est le fichier d'entree avec _nadls.csv au lieu de .txt
rem Exemple d'utilisation :
rem vidhosp-extraire-nadls-dnd.bat "C:\Local\GROUPAGE\2019\M12\200117-pmsipilot\VH200117\VIDHOSP_MCO.txt"
rem N.B. marche si on fait un "glisser-deposer" sur le fichier bat

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat

rem Chemin du script.
set SCRIPT=%GPMSI_HOME%\scripts\groovy\vidhosp_extraire_nadls.groovy

rem Fichier VIDHOSP a analyser
set A=%~1
rem Fichier csv resultant avec les NADLs
set B=%~dpn1_nadls.csv
call %APP% -script %SCRIPT% -a:input %A% -a:output %B%
if errorlevel 1 pause
