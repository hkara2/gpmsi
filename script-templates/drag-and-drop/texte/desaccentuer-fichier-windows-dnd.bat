rem Enlever les accents de ce fichier texte windows (encodage windows-1252)

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set SCRIPT=%GPMSI_HOME%\scripts\groovy\lignes_enlever_accents.groovy
call "%GPMSI_HOME%\gpmsi.bat" -script "%SCRIPT%" -a:input "%~1" -a:output "%~dpn1_sans_accents%~x1"
pause
