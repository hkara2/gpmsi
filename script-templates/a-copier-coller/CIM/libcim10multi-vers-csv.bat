rem Conversion d'un fichier CIM 10 fourni dans le kit de nomenclature ATIH
rem en fichier .csv avec séparateur point-virgule, et des noms de colonne
rem utilisables avec fr.gpmsi.StringTable
rem Le fichier de destination est cree dans le meme repertoire que le fichier
rem d'entree, a comme nom 'cim10-p<date_debut_validite>-<date_fin_validite>.csv'
rem et comme encodage 'windows-1252'
rem Copier-coller puis adapter
setlocal enableextensions

if not exist GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@
set APP=%GPMSI_HOME%\gpmsi.bat
rem Chemin du script.
set SCRIPT=%GPMSI_HOME%\scripts\groovy\libcim10multi_vers_csv.groovy
rem Fichier CIM 10 d'entree 'input' issu de l'ATIH. Habituellement le nom
rem qui est mis ici est le bon. Adapter pour utiliser un autre nom.
set INP=LIBCIM10MULTI.TXT
rem Date de debut (inclue) de validite de cette nomenclature au format AAAAMMJJ
rem Comme ce fichier est habituellement cumulatif, on peut sauf besoin
rem contraire laisser 20040101
set DEBVAL=20040101
rem Date de fin (exclue) de validite de cette nomenclature au format AAAAMMJJ
set FINVAL=<date_au_format_aaaammjj>

call %APP% -script "%SCRIPT%" -a:input "%INP%" -a:debval "%DEBVAL%" -a:finval "%FINVAL%"
