rem :encoding=windows-1252: Conversion d'un fichier de FICHCOMP DMI, en fichier Excel xlsx.
rem Exemple d'utilisation :
rem dmi-vers-xlsx-dnd.bat FichCompDmi.txt
rem N.B. marche si on fait un "glisser-d�poser" sur le fichier bat (attention echoue si le nom contient une virgule !)

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\mono_vers_xlsx.groovy

rem Fichier DMI a transformer (avec guillemets enleves)
set A=%~1
rem Fichier Excel resultant
set B=%~dpn1.xlsx
call "%APP%" -script "%SCRIPT%" -a:meta fichcompdmi2020 -a:input "%A%" -a:output "%B%"
@rem on met une pause pour voir le r�sultat (et s'il y a eu des erreurs)
pause
