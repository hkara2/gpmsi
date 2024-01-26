rem :encoding=windows-1252: Pour un fichier de RSS (groupes ou non), 
rem detecter les lignes en doublon et les couples RSS+RUM en doublon.
rem Exemple d'utilisation :
rem rss-doublons-lignes-dnd.bat 012018_GRP018fg1617.txt
rem N.B. marche si on fait un "glisser-déposer" sur le fichier bat (attention echoue si le nom contient une virgule !)

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rss_doublons_lignes.groovy

rem Fichier RSS a analyser (avec guillemets enleves)
set A=%~1
call "%APP%" -script "%SCRIPT%" -a:input "%A%"
pause
