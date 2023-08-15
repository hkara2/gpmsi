rem Conversion d'un fichier texte/rss en fichier csv (le nom doit finir par .txt !)
rem Normalement il suffit de faire glisser le fichier sur le raccourci

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\scripts\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rss_vers_triple_csv.groovy

set INFILE=%1
set OUTPREFIX=%INFILE:~0,-4%
call %APP% -script %SCRIPT% -a:input "%INFILE%" -a:outprefix "%OUTPREFIX%" %2 %3 %4 %5 %6 %7 %8 %9
