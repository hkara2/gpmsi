rem Afficher des informations sur l'environnement de GPMSI

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=%GPMSI_BASE%\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\infos_environnement.groovy

%APP% -script %SCRIPT%
