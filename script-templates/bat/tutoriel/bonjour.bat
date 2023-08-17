rem Le script le plus simple possible, il appelle juste bonjour.groovy

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\bonjour.groovy

call %APP% -script %SCRIPT%
pause