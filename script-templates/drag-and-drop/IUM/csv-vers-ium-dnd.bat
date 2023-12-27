rem Conversion d'un fichier csv vers un fichier IUM (infos unite medicale)
rem Exemple d'utilisation :
rem csv-vers-ium-dnd.bat infos-um.csv
rem N.B. marche si on fait un "glisser-deposer" sur le fichier bat (attention echoue si le nom contient une virgule !)

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\csv_vers_infoum1.groovy

rem Fichier csv a transformer (avec guillemets enleves)
set A=%~1
rem Fichier IUM resultant
set B=%~dpn1_infoum1.txt
rem Fichier de log de l'outil
set C=%~dp1\csv-vers-ium-dnd_log.txt
call "%APP%" -script "%SCRIPT%" -a:input "%A%" -a:output "%B%" >> "%C%" 2>&1
