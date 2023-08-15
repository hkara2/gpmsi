rem Conversion de fichier RPU en ficher XLSX
rem N.B. marche si on fait un "glisser-deposer" sur le fichier bat

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
rem Chemin du script.
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rpu_vers_xlsx.groovy

rem Fichier RPU a transformer
set RPU=%~1
rem Fichier XLSX resultant
set CSV=%~dpn1%.xlsx
call %APP% -script "%SCRIPT%" -a:input "%RPU%" -a:output "%CSV%"
pause
