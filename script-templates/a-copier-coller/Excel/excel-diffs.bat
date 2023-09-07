rem Gabarit de commande gpmsi excel-diffs pour copier-coller et adapter
rem Cf. scripts\groovy\excel_diffs.groovy

setlocal EnableExtensions

rem Adapter GPMSI_HOME dans la ligne ci-dessous si gpmsi est installe ailleurs
set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat

set SCRIPT=%GPMSI_HOME%\scripts\groovy\excel_diffs.groovy

rem Ancien fichier excel, ce sera 'A'
set A=<mettre_ici_chemin_du_fichier_en_entree>

rem Nouveau fichier excel, ce sera 'B'
set B=<mettre_ici_chemin_du_fichier_en_entree>

call "%APP%" -script "%SCRIPT%" -a:input_a "%A%" -a:input_b "%B%"

@for %%G in (%B%) do @echo Les nouveaux enregistrements sont dans "%%~nG_new%%~xG"

