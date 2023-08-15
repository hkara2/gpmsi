rem Conversion d'un fichier RPU accompagne de donnees extraites via BO (ou autre) en un fichier excel .xlsx

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rpu_et_bo_vers_xlsx_gui.groovy

%APP% -script %SCRIPT%
