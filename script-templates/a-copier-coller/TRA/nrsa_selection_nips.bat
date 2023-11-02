rem Rattacher les champs NADL et IPP a un fichier qui contient une colonne NRSA,
rem en se servant du fichier TRA.
rem Ce fichier est a copier-coller a cote des fichiers a completer
rem puis a adapter

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat

set SCRIPT=%GPMSI_HOME%\scripts\groovy\nrsa_selection_nips.groovy

rem Fichier csv qui contient une colonne NRSA, ce sera 'A'
set A=mettre_ici_chemin_du_fichier_csv_en_entree

rem Fichier TRA
set TRA=mettre_ici_chemin_du_fichier_tra

rem Fichier VIDHOSP
set VH=mettre_ici_chemin_du_fichier_vidhosp

rem Fichier csv resultat, ce sera 'B'
set B=mettre_ici_chemin_du_fichier_csv_en_sortie

"%APP%" -script "%SCRIPT%" -a:input_csv "%A%" -a:input_tra "%TRA%" -a:input_vh "%VH%" -a:output "%B%"
