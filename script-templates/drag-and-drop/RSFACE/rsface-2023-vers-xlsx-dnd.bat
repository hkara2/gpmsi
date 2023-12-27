rem :encoding=windows-1252: Conversion d'un fichier de RSF-ACE au format 2023
rem en fichier xlsx.
rem Exemple d'utilisation :
rem rsface-2023-vers-xlsx-dnd.bat RSF.txt
rem N.B. marche si on fait un "glisser-deposer" sur le fichier bat (attention echoue si le nom contient une virgule !)

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rsface-vers-xlsx.groovy

rem Fichier RSF-ACE a transformer (avec guillemets enleves)
set A=%~1
rem Fichier XLSX resultant, place a cote du fichier d'entree
set B=%~dpn1.xlsx
call "%APP%" -script "%SCRIPT%" -a:meta_hint 2023 -a:input "%A%" -a:output "%B%"
pause
