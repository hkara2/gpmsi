rem Lancement conversion texte vers phonex, essayer par exemple avec -a:text TEAUTAU

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\phonex.groovy

call %APP% -script %SCRIPT% %*
