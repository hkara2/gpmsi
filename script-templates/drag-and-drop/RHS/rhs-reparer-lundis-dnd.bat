rem Réparer les RHS pour lesquels la sortie s'est faite un lundi, et le SI n'a
rem rien envoyé.
rem Exemple d'utilisation :
rem rhs-reparer-lundis-dnd.bat SSR_RHSN_DateRHS_20220412135017.txt
rem N.B. marche si on fait un "glisser-deposer" sur le fichier bat (attention echoue si le nom contient une virgule !)
rem Attention pour l'instant ne marche pas sur les periodes a cheval
rem Attention demande interactivement la periode de fin !

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rhs_reparer_lundis.groovy

rem Fichier RHS a reparer (le ~ enleve les guillemets)
set A=%~1
call "%APP%" -script "%SCRIPT%" -a:input "%A%" -a:periode "?sEntrez le mois de fin de la periode (AAAAMM)"
pause
