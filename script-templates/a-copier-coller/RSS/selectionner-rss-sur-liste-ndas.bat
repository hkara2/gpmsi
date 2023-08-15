rem Selection de RUMs a partir d'une colonne NADL d'un fichier csv
rem Ce fichier est a copier-coller
rem puis a adapter

setlocal EnableExtensions

rem Adapter GPMSI_HOME dans la ligne ci-dessous si gpmsi est installe ailleurs
if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat

set SCRIPT=%GPMSI_HOME%\scripts\groovy\selection_de_rss.groovy

rem Fichier RUMS/RSS, ce sera 'A'
set A=<mettre_ici_chemin_du_fichier_des_RUMSRSS>
rem Fichier csv contenant une colonne NADL, ce sera 'B'
set B=<mettre_ici_chemin_du_fichier_csv_avec_colonne_NADL>
rem Fichier RUMS/RSS selectionnes, ce sera 'C'
set C=<mettre_ici_chemin_du_fichier_des_RUMSRSS_selectionnes>

%APP% -script "%SCRIPT%" -a:input "%A%" -a:nadlincl "%B%" -a:output "%C%
