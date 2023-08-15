rem Rattacher une colonne IPP a un fichier qui contient des NADL, a l'aide du VIDHOSP
rem Ce fichier est a copier-coller a cote des fichiers a completer
rem puis a adapter

setlocal EnableExtensions

rem Adapter GPMSI_HOME dans la ligne ci-dessous si gpmsi est installe ailleurs
set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat

set SCRIPT=%GPMSI_HOME%\scripts\groovy\vh_attacher_ipp.groovy

rem Fichier csv qui contient une colonne NADL, ce sera 'a'
set A=<mettre_ici_chemin_du_fichier_csv_en_entree>

rem Fichier VIDHOSP
set VH=<mettre_ici_chemin_du_fichier_vidhosp>

rem Fichier csv apres, ce sera 'b'
set B=<mettre_ici_chemin_du_fichier_csv_en_sortie>

"%APP%" -script "%SCRIPT%" -a:input_csv "%A%" -a:input_vh "%VH%" -a:output "%B%"
