rem Appel de l'interface graphique de rss vers csv

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\scripts\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rss_vers_csv_gui.groovy

call %APP% -script %SCRIPT%
