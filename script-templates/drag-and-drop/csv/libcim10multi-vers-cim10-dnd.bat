rem appelle le script libcim10multi_vers_csv.groovy en passant le premier
rem argument comme chemin vers le fichier LIBCIM10MULTI.TXT
rem Deux fenêtres surgissantes apparaissent pour périodes de début et de fin
rem Fonctionne en drag-and-drop

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\libcim10multi_vers_csv.groovy

set INFILE=%~1
call %APP% -script %SCRIPT% -a:input "%INFILE%" -a:debval "?sDate debut validite (aaaammjj)" -a:finval "?sDate fin validite (aaaammjj)"
if errorlevel 1 pause
