rem Lancer le fichier html passe en parametre et dont les liens font
rem un menu de fichiers et répertoire locaux
rem N.B. on lance avec gpmsiw qui supprime la fenetre de terminal
rem On peut ajouter la taille en deuxieme parametre si la taille automatique ne
rem convient pas (ex : 800x600)

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsiw.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\fr\gpmsi\local\start_menu.groovy
if "_%2_"=="__" (
    call %APP% -script %SCRIPT% -a:input %1
) else (
    call %APP% -script %SCRIPT% -a:input %1 -a:taille %2
)

