rem Produire un FICHCOMP DMI a partir d'un fichier Excel xlsx (cf. script scripts\groovy\xlsx_vers_fichcompdmi.groovy)
rem Utilisation : %GPMSI_BASE%\v@PROJECT_VERSION@\scripts\bat\FICHCOMP\xslx-vers-fichcompdmi.cmd chemin_vers_fichcomp_dmi
rem Marche aussi en drag-and-drop
rem le fichier de destination est le fichier de depart avec .xlsx remplace par .txt
%GPMSI_BASE%\v@PROJECT_VERSION@\gpmsi -script %GPMSI_BASE%\v@PROJECT_VERSION@\scripts\groovy\xlsx_vers_fichcompdmi.groovy -a:input %1 -a:output "%~dpn1.txt"
