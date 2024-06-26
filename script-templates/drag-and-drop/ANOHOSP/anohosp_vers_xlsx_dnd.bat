rem Conversion de fichier ANOHOSP en ficher xlsx
rem Exemple d'utilisation :
rem anohosp_vers_xlsx_dnd.bat "C:\Local\Controles-CDC\2024\910019447.2024.2.001.ano.txt"
rem N.B. marche si on fait un "glisser-deposer" sur le fichier bat

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat

rem Chemin du script.
set SCRIPT=%GPMSI_HOME%\scripts\groovy\anohosp_vers_xlsx.groovy

rem Fichier ANOHOSP a transformer
set A=%~1
rem Fichier XLSX resultant
set B=%~dpn1.xlsx
call "%APP%" -script "%SCRIPT%" -a:input "%A%" -a:output "%B%"
if errorlevel 1 pause
