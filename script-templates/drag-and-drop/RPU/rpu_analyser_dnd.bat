rem Analyser un fichier RPU a la recherche d'erreurs.
rem Normalement il suffit de faire glisser le fichier sur le raccourci

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rpu_analyser.groovy

set INFILE=%~1
call %APP% -script %SCRIPT% -a:input "%INFILE%"
pause