rem :encoding=windows-1252: rattachement d'une colonne numéro de dossier (NDOSS) à 
rem a un fichier .csv qui contient une colonne NRSA,
rem à l'aide du fichier TRA. Le fichier TRA doit finir par .tra.txt et l'autre
rem fichier qui contient la colonne NRSA doit être au format .csv
rem Exemple d'utilisation :
rem nrsa-rattacher-ndoss-dnd.bat 910019447.2019.12.csv 910019447.2019.12.tra.txt
rem N.B. marche si on fait un "glisser-déposer" des deux fichiers sur le fichier bat (attention echoue si le nom contient une virgule !)

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\nrsa_attacher_nadl_dnd.groovy

rem Premier fichier, avec guillemets enleves s'il y en avait
set A=%~1
rem Deuxieme fichier, avec guillemets enleves s'il y en avait
set B=%~2
call "%APP%" -script "%SCRIPT%" -a:in_a "%A%" -a:in_b "%B%"
pause
