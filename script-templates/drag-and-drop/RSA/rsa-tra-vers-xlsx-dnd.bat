rem :encoding=windows-1252: transformation des deux fichiers RSA + TRA en
rem un fichier xlsx
rem Exemple d'utilisation :
rem rsa-tra-vers-xlsx-dnd.bat 910019447.2019.12.csv 910019447.2019.12.tra.txt
rem N.B. marche si on fait un "glisser-déposer" des deux fichiers sur le fichier bat (attention echoue si le nom contient une virgule !)

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=%GPMSI_BASE%\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rsa_tra_vers_xlsx.groovy

if "%~x1" == ".rsa" goto :RSA1
goto :GO

rem Premier fichier est TRA, avec guillemets enleves s'il y en avait
set B=%~1
rem Deuxieme fichier est RSA, avec guillemets enleves s'il y en avait
set A=%~2
set OUTP=%~dpn2_rsa.xlsx

:RSA1
rem Premier fichier est RSA, avec guillemets enleves s'il y en avait
set A=%~1
rem Deuxieme fichier est TRA, avec guillemets enleves s'il y en avait
set B=%~2
set OUTP=%~dpn1_rsa.xlsx

:GO
call "%APP%" -script "%SCRIPT%" -a:input_rsa "%A%" -a:input_tra "%B%" -a:output "%OUTP%"
pause
