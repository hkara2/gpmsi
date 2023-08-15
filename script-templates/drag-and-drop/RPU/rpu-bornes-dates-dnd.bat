rem Extraction des bornes de dates d'un fichier de RPU
rem Normalement il suffit de faire glisser le fichier sur le raccourci

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rpu_bornes_dates.groovy

set INFILE=%~1
call %APP% -script %SCRIPT% -a:input "%INFILE%" %2 %3 %4 %5 %6 %7 %8 %9
pause