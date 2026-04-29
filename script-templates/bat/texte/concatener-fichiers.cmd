rem Concatener les fichiers vers un fichier destination.
rem Le premier argument est le fichier de destination, les autres sont les fichiers texte à concatener, dans l'ordre.
rem Les lignes vides sont ignorées
rem Exemple :
rem C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\concatener_fichiers_texte.groovy -f:sansLignesVides -a:output "fabc.txt"  -a:input fa.txt -a:input "fb 2.txt" -a:input fc.txt
setlocal enableextensions
set OUTF=%~1
set ARGS=
:LOOP
shift
if "_%~1_" == "__" goto :DONE
set ARGS=%ARGS% -a:input %1
goto :LOOP
:DONE
%GPMSI_BASE%\v@PROJECT_VERSION@\gpmsi -script %GPMSI_BASE%\v@PROJECT_VERSION@\scripts\groovy\concatener_fichiers_texte.groovy -f:sansLignesVides -a:output "%OUTF%" %ARGS%
