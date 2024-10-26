rem Lancer le fichier %USERPROFILE%\.gpmsi\start_menu.html qui est un menu de fichier

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\fr\gpmsi\local\start_menu.groovy

call %APP% -script %SCRIPT% -a:input %USERPROFILE%\.gpmsi\start_menu.html
