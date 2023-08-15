rem Exemple de lancement simple d'un script local, a adapter

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\scripts\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\..\scripts-locaux\groovy\monscript.groovy

call %APP% -script %SCRIPT% -a:input fichier_entree -a:output fichier_sortie (adapter les parametres)
