rem Fusion de deux fichiers RPU
rem cf. "C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rpu_fusion_fichiers.groovy"

setlocal EnableExtensions

rem Adapter GPMSI_HOME dans la ligne ci-dessous si gpmsi est installe ailleurs
set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat

set SCRIPT=%GPMSI_HOME%\scripts\groovy\rpu_fusion_fichiers.groovy

rem Fichier en entree A
set A=<mettre_ici_chemin_du_fichier_en_entree_A>

rem Fichier en entree B
set B=<mettre_ici_chemin_du_fichier_en_entree_B>

rem Fichier en sortie, ce sera 'C'
set C=<mettre_ici_chemin_du_fichier_en_sortie>

"%APP%" -script "%SCRIPT%" -a:input_a "%A%" -a:input_b "%B%" -a:output "%C%"
