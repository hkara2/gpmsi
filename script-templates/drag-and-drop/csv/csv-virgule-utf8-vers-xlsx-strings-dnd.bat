rem conversion du fichier csv passe en argument en fichier .xlsx
rem avec toutes les colonnes en format texte (string)
rem ici on force l'encodage a UTF-8 et le separateur a la virgule.
rem C'est un format qu'on rencontre frequemment sur les bases INSERM.

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\csv_vers_xlsx_strings.groovy

set INFILE=%~1
set OUTFILE=%~dpn1.xlsx
call %APP% -script %SCRIPT% -a:sep 2C -a:enc UTF-8 -a:input "%INFILE%" -a:output "%OUTFILE%"
if errorlevel 1 pause
