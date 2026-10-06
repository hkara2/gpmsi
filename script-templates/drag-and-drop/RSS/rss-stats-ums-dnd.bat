rem :encoding=windows-1252: Fait les stats du nombre de RUMs par UM, en fichier csv.
rem Exemple d'utilisation :
rem rss-stats-ums-dnd.bat 012018_GRP018fg1617.txt
rem N.B. marche si on fait un "glisser-déposer" sur le fichier bat (attention echoue si le nom contient une virgule !)

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=%GPMSI_BASE%\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rss_stats_ums.groovy

rem Fichier RSS a analyser (avec guillemets enleves)
set A=%~1
rem Fichier csv resultant avec stats UMs
set B=%~dpn1_stats_ums.csv
call "%APP%" -script "%SCRIPT%" -a:input "%A%" -a:output "%B%"
pause
