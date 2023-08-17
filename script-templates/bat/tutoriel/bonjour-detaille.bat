rem Appel de bonjour.groovy avec le drapeau (flag) 'details'

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\bonjour.groovy

call %APP% -script %SCRIPT% -f:details
pause