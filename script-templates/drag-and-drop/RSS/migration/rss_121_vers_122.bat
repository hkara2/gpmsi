rem Migration d'un fichier RSS au format 121 en fichier au format 122
rem Normalement il suffit de faire glisser le fichier sur le raccourci
rem Le fichier de sortie porte le meme nom avec _122 a la fin

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rss_121_vers_122.groovy

set INFILE=%1
set OUTFILE=%~dpn1_122%~x1
call %APP% -script %SCRIPT% -a:input "%INFILE%" -a:output "%OUTFILE%"
rem Mettre une pause pour voir les erreurs eventuelles
pause