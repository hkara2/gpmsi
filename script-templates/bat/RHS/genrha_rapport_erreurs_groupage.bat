rem Lancement de genrha_rapport_erreurs_groupage.groovy avec les options par defaut

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\genrha_rapport_erreurs_groupage.groovy

rem Appelle le script sans aucun argument, agit sur le repertoire de sortie par defaut
call %APP% -script %SCRIPT%

rem Faire une pause pour voir les erreurs eventuelles

pause
