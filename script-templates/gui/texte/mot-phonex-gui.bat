rem Conversion d'un fichier RPU en un fichier .csv

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\mot_phonex_gui.groovy

%APP% -script %SCRIPT%
