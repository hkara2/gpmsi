rem selection des lignes du fichier csv (1er argument) selon les NADLs qui
rem sont dans le fichier (2eme argument)
rem Le resultat est dans le fichier donne en 3ème argument

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\csv_selectionner_sur_nadls.groovy

set INFILE=%~1
set NADLS=%~2
set OUTFILE=%~3
call %APP% -script %SCRIPT% -a:input "%INFILE%" -a:nadls "%NADLS%" -a:output "%OUTFILE%"
