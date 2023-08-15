rem :encoding=windows-1252: Conversion d'un fichier de RSS (groupes ou non), en fichier csv.
rem Le fichier doit finir par .txt car le script efface les 3 derniers caractères et les remplace par csv
rem Exemple d'utilisation :
rem rss-vers-csv.bat 012018_GRP018fg1617.txt
rem N.B. marche si on fait un "glisser-déposer" sur le fichier bat (attention echoue si le nom contient une virgule !)

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rss_vers_csv.groovy

rem Fichier RSS a transformer (avec guillemets enleves)
set A=%~1
rem Fichier RSS resultant
set B=%~dpn1.csv
call "%APP%" -script "%SCRIPT%" -a:input "%A%" -a:output "%B%"
pause
