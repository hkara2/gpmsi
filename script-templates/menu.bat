rem Lancer le fichier menu.html qui est un menu pour acceder aux commandes
rem et aux repertoires

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsiw.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\fr\gpmsi\local\start_menu.groovy

call %APP% -script %SCRIPT% -a:input menu.html
