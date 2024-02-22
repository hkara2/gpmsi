rem Fusion de deux fichiers RPU
rem cf. "C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rpu_fusion_fichiers.groovy"
rem prend 3 arguments : fichier A, fichier B, fichier résultat

setlocal EnableExtensions

rem Adapter GPMSI_HOME dans la ligne ci-dessous si gpmsi est installe ailleurs
set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat

set SCRIPT=%GPMSI_HOME%\scripts\groovy\rpu_fusion_fichiers.groovy

set A=%~1
set B=%~2
set C=%~3

"%APP%" -script "%SCRIPT%" -a:input_a "%A%" -a:input_b "%B%" -a:output "%C%"
