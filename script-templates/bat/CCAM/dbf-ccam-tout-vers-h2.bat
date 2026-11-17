rem Appel au script GPMSI groovy dbf_ccam_tout_vers_h2.groovy
rem A copier-coller dans le repertoire qui contient les fichiers dbf
rem A la fin il y a une base "ccam" (user "sa", mot de passe vide)
rem dans le répertoire, avec le contenu des tables dbf

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=%GPMSI_BASE%\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\dbf_ccam_tout_vers_h2.groovy

set INFILE=%~1
set MYARG=%~2
set OUTFILE=%~3
call "%APP%" -script "%SCRIPT%" -a:h2url jdbc:h2:file:./ccam
if errorlevel 1 pause
