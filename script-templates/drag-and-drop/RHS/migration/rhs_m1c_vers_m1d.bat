rem Migration d'un fichier RHS au format M1C en fichier au format M1D
rem Normalement il suffit de faire glisser le fichier sur le raccourci
rem Le fichier de sortie porte le meme nom avec _m1d a la fin

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rhs_m1c_vers_m1d.groovy

set INFILE=%1
set OUTFILE=%~dpn1_m1d%~x1
call %APP% -script %SCRIPT% -a:input "%INFILE%" -a:output "%OUTFILE%"
rem Mettre une pause pour voir les erreurs eventuelles
pause