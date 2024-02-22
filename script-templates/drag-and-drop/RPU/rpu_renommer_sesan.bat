rem Copie d'un fichier RPU avec un nom au format SESAN
rem cf. "C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rpu_renommer_sesan.groovy"
rem prend comme argument le nom du fichier
setlocal EnableExtensions
rem Adapter GPMSI_HOME dans la ligne ci-dessous si gpmsi est installe ailleurs
set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@
set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rpu_renommer_sesan.groovy
set A=%~1
"%APP%" -script "%SCRIPT%" -a:input "%A%"