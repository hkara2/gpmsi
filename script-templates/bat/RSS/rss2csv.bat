rem Conversion d'un fichier texte/rss en fichier csv (le nom doit finir par .txt !)
rem Normalement il suffit de faire glisser le fichier sur le raccourci

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rss-vers-csv.groovy

set INFILE=%1
set OUTFILE=%INFILE:~0,-3%csv
call %APP% -script %SCRIPT% -a:input "%INFILE%" -a:output "%OUTFILE%"
