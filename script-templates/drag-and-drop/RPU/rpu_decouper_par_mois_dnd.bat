rem Decouper un fichier RPU par mois.
rem Change aussi la date d'extraction en mettant le numero de mois dans les secondes
rem Normalement il suffit de faire glisser le fichier sur le raccourci

setlocal EnableExtensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rpu_filtrer.groovy

set /p EXTRACT=Entrez la date d extraction au format JJ/MM/AAAA HH:MM : 

set INFILE=%~1
set INFILE_PFX=%~dpn1

set LOGFILE=%INFILE_PFX%_decoupe_au_mois.log

echo Decoupage par mois du fichier %INFILE% >"%LOGFILE%" 2>&1
echo Date %date% %time%  >>"%LOGFILE%" 2>&1

rem 12 appels (1 par mois)
for /L %%M in (1,1,9)   do call %APP% -script %SCRIPT% -a:input "%INFILE%" -a:mois %%M -a:extract "%EXTRACT%:0%%M" -a:output "%INFILE_PFX%_0%%M.xml" >>"%LOGFILE%" 2>&1
for /L %%M in (10,1,12) do call %APP% -script %SCRIPT% -a:input "%INFILE%" -a:mois %%M -a:extract "%EXTRACT%:%%M" -a:output "%INFILE_PFX%_%%M.xml" >>"%LOGFILE%" 2>&1

echo Consultez les resultats dans le fichier de log "%LOGFILE%".
pause
