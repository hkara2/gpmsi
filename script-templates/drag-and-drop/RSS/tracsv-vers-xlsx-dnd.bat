rem :encoding=windows-1252: Conversion d'un fichier de TRA au format csv (le format DRUIDES depuis 2023), en fichier Excel xlsx.
rem Exemple d'utilisation :
rem tracsv-vers-xlsx-dnd.bat 910019447.2025.0.tra.txt
rem N.B. marche si on fait un "glisser-déposer" sur le fichier bat (attention echoue si le nom contient une virgule !)

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\tracsv_vers_xlsx.groovy

rem Fichier TRA a transformer (avec guillemets enleves)
set A=%~1
rem Fichier Excel resultant
set B=%~dpn1.xlsx
call "%APP%" -script "%SCRIPT%" -a:input "%A%" -a:output "%B%"
@rem on met une pause pour voir le résultat (et s'il y a eu des erreurs)
pause
