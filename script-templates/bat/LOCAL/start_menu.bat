rem Lancer le fichier html passe en parametre et dont les liens font
rem un menu de fichiers et répertoire locaux
rem N.B. on lance avec gpmsiw qui supprime la fenetre de terminal

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsiw.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\fr\gpmsi\local\start_menu.groovy

call %APP% -script %SCRIPT% -a:input %1
