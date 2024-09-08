rem Conversion MLLP vers XML. Prend le parametre %1 pour le fichier d'entree
rem et le fichier de sortie, donc fonctionne en glisser deposer
setlocal enableextensions
if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set INP=%~1
set OUTP=%~dpn1_hl7.xml

call %GPMSI_HOME%\gpmsi.bat -run fr.gpmsi.hapi.MllpToXml -a:input "%INP%" -a:output "%OUTP%"

if errorlevel 1 pause
