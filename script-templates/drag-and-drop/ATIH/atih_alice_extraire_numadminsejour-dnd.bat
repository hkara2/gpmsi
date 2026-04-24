rem A partir d'un fichier .csv produit par l'outil "Alice", extrait un fichier
rem qui ne contient que les "NumAdminSejour", sans les espaces.
rem Le nom du fichier de sortie est celui d'entree, mais sans l'extension, et
rem avec "_NumAdminSejour.txt" à la fin.
rem
rem Exemple d'utilisation :
rem atih_alice_extraire_numadminsejour-dnd.bat 910000280.2025.12.ca.MCO.20260414115130.csv
rem N.B. marche si on fait un "glisser-deposer" sur le fichier bat

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=%GPMSI_BASE%\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\atih_alice_extraire_numadminsejour.groovy

rem Fichier Alice a utiliser
set A=%~1
"%APP%" -script "%SCRIPT%" -a:input "%A%"
