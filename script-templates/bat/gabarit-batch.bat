rem Fichier gabarit de batch lancement de script, a adapter. Ne pas utiliser d'accents.
rem
rem

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=%GPMSI_BASE%\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\nom_du_script_a_appeler.groovy

set INFILE=%~1
set MYARG=%~2
set OUTFILE=%~3
call "%APP%" -script "%SCRIPT%" -a:input "%INFILE%" -a:myarg "%MYARG%" -a:output "%OUTFILE%"
if errorlevel 1 pause
