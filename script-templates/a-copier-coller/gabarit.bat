rem Gabarit de commande gpmsi pour copier-coller et adapter
rem <ici mettre explications de la commande>

setlocal EnableExtensions

rem Adapter GPMSI_HOME dans la ligne ci-dessous si gpmsi est installe ailleurs
set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat

rem Mettre ici le chemin du script groovy a executer
set SCRIPT=%GPMSI_HOME%\scripts\groovy\bonjour.groovy

rem Fichier en entree, ce sera 'A'
set A=<mettre_ici_chemin_du_fichier_en_entree>

rem Fichier en sortie, ce sera 'B'
set B=<mettre_ici_chemin_du_fichier_en_sortie>

"%APP%" -script "%SCRIPT%" -a:input "%A%" -a:output "%B%"
