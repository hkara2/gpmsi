rem :encoding=windows-1252: Conversion d'un fichier de RSS (groupes ou non), en fichier Excel xlsx.
rem On desactive la generation des libelles pour gagner de la place
rem Exemple d'utilisation :
rem rss-vers-xlsx-dnd.bat 012018_GRP018fg1617.txt
rem N.B. marche si on fait un "glisser-déposer" sur le fichier bat (attention echoue si le nom contient une virgule !)

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rss_vers_xlsx.groovy

rem Fichier RSS a transformer (avec guillemets enleves)
set A=%1
rem Fichier Excel resultant
set B=%~dpn1.xlsx
call "%APP%" -script "%SCRIPT%" -a:input "%A%" -a:output "%B%" -f:paslibccam -f:paslibcim
@rem on met une pause pour voir le résultat (et s'il y a eu des erreurs)
pause
