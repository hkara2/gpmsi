rem Sortir les enregistrements dmi dont le NADL est dans la liste fournie
rem Cf le script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\dmi_selection_sur_nadls.groovy

setlocal EnableExtensions

rem Adapter GPMSI_HOME dans la ligne ci-dessous si gpmsi est installe ailleurs
set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat

rem Mettre ici le chemin du script groovy a executer
set SCRIPT=%GPMSI_HOME%\scripts\groovy\dmi_selection_sur_nadls.groovy

rem Fichier en entree, ce sera 'A'
set A=<mettre_ici_chemin_du_fichier_en_entree>

rem Fichier en sortie, ce sera 'B'
set B=<mettre_ici_chemin_du_fichier_en_sortie>

rem Fichier contenant la liste des NADLs 'C'
set B=<mettre_ici_chemin_du_fichier_des_nadls>

"%APP%" -script "%SCRIPT%" -a:input "%A%" -a:meta fichcompdmi2020 -a:nadls "%C%" -a:output "%B%"

