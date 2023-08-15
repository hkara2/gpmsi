rem Conversion d'un fichier de RHS (groupes ou non), en fichier csv.
rem Le fichier doit finir par .txt car le script efface les 3 derniers caracteres et les remplace par csv
rem Exemple d'utilisation :
rem rhs-vers-csv.bat SSR_RHSN_DateRHS_20220412135017.txt
rem N.B. marche si on fait un "glisser-deposer" sur le fichier bat (attention echoue si le nom contient une virgule !)

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rhs_vers_csv.groovy

rem Fichier RHS a transformer (avec guillemets enleves)
set A=%~1
rem Fichier RHS resultant
set B=%~dpn1.csv
call "%APP%" -script "%SCRIPT%" -a:input "%A%" -a:output "%B%"
pause
