rem :encoding=windows-1252: Conversion d'un fichier de FICHCOMP MED (Molecules Onereuses), en fichier Excel xlsx.
rem Exemple d'utilisation :
rem med-vers-xlsx-dnd.bat FichCompDmi.txt
rem N.B. marche si on fait un "glisser-déposer" sur le fichier bat (attention echoue si le nom contient une virgule !)

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\mono_vers_xlsx.groovy

rem Fichier MED a transformer (avec guillemets enleves)
set A=%~1
rem Fichier Excel resultant
set B=%~dpn1.xlsx
call "%APP%" -script "%SCRIPT%" -a:meta fichcompmed2020 -a:input "%A%" -a:output "%B%"
@rem on met une pause pour voir le résultat (et s'il y a eu des erreurs)
pause
