rem Renommages multiples, pour chaque fichier de l'ensemble glisse,
rem appelle rpu_renommer_sesan.bat
setlocal EnableExtensions
rem Adapter GPMSI_HOME dans la ligne ci-dessous si gpmsi est installe ailleurs
set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@
set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rpu_renommer_sesan.groovy
:boucle
set A=%~1
call "%APP%" -script "%SCRIPT%" -a:input "%A%"
@echo %1
shift
if not "%~1"=="" goto boucle
pause
