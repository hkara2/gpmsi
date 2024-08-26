rem conversion du fichier csv passe en argument en fichier .xlsx
rem avec toutes les colonnes en format texte (string)

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\csv_vers_xlsx_strings.groovy

set INFILE=%~1
set OUTFILE=%~dpn1.xlsx
call %APP% -script %SCRIPT% -a:input "%INFILE%" -a:output "%OUTFILE%"
if errorlevel 1 pause
