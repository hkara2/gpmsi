
rem Fusion des RHS de A et B par ajout de B dans A (par ex. B:Sillage A:DxCare,
rem ou bien A: DxCare avec dates de fin des suites SSR B: DxCare avec dates RHS )

rem Fonctionne via drag and drop (selectionner les deux fichiers puis les faire glisser sur ce fichier)

rem Attention le bon ordre dans DxCare pour que cela fonctionne est que pour input_a ce soit dates SSR et input_b dates RHS
rem C'est pour cela que l'ordre des arguments est inversé ici !
rem Sinon on a les RHS dans le mauvais ordre et des erreur de groupage R807 !

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set SCRIPT=%GPMSI_HOME%\scripts\groovy\rhs_fusion_fichiers.groovy

set OUTF=%~dpn2__%~n1.txt
set LOGF=%~dp1\rhs-fusion-fichiers-dxcare.log

echo. >>"%LOGF%" 2>&1
echo Date d execution %DATE% %TIME% >>"%LOGF%" 2>&1

call "%GPMSI_HOME%\gpmsi.bat" -script "%SCRIPT%" -a:input_a "%~2" -a:input_b "%~1" -a:output "%OUTF%"  >>"%LOGF%" 2>&1
pause
