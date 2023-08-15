rem Conversion de fichier VIDHOSP en ficher CSV
rem Exemple d'utilisation :
rem vidhosp-vers-csv.bat "C:\Local\GROUPAGE\2019\M12\200117-pmsipilot\VH200117\VIDHOSP_MCO.txt"
rem N.B. marche si on fait un "glisser-déposer" sur le fichier bat

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat

rem Chemin du script. Mettre repertoire de dev C:\hkchse\dev\pmsixml ou bien C:\app\pmsixml\1.8
set SCRIPT=%GPMSI_HOME%\scripts\groovy\mono_vers_xlsx.groovy

rem Fichier VIDHOSP a transformer
set A=%~1
rem Fichier XLSX resultant
set B=%~dpn1.xlsx
call %APP% -script %SCRIPT% -a:input %A% -a:meta vidhospV013 -a:output %B%
