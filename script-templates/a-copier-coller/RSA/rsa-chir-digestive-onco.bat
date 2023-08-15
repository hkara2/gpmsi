rem Analyser le fichier RSA a la recherche de sejours qui repondent aux criteres 
rem INCA 2011 de chirurgie digestive oncologique
rem Ce fichier est a copier-coller a cote du fichier RSA,
rem puis a adapter

setlocal EnableExtensions

rem Adapter GPMSI_HOME dans la ligne ci-dessous si gpmsi est installe ailleurs
set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat

set SCRIPT=%GPMSI_HOME%\scripts\groovy\regles\rsa_chir_digestive_onco.groovy

rem Fichier RSA
set RSA=<mettre_ici_chemin_du_fichier_csv_en_entree>

rem Fichier de sortie avec les NRSA (par ex. NRSAs_chir_dig_onco.csv), ce sera 'b'
set B=<mettre_ici_chemin_du_fichier_csv_en_sortie>

"%APP%" -script "%SCRIPT%" -a:input "%RSA%" -a:output "%B%"
