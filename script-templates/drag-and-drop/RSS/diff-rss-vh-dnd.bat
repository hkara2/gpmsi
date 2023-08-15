rem Differences RSS - VIDHOSP via drag and drop (selectionner les deux fichiers puis les faire glisser sur ce fichier)

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set SCRIPT=%GPMSI_HOME%\scripts\groovy\diff_vidhosp_rss_dnd.groovy
call "%GPMSI_HOME%\gpmsi.bat" -script "%SCRIPT%" -a:in_a "%~1" -a:in_b "%~2"
pause
