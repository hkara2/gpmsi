rem Migration d'un fichier RSS au format 122 en fichier au format 123
rem Normalement il suffit de faire glisser le fichier sur le raccourci
rem Le fichier de sortie porte le meme nom avec _123 a la fin

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=%GPMSI_BASE%\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rss_122_vers_123.groovy

set INFILE=%1
set OUTFILE=%~dpn1_123%~x1
call %APP% -script %SCRIPT% -a:input "%INFILE%" -a:output "%OUTFILE%"
rem Mettre une pause pour voir les erreurs eventuelles
pause