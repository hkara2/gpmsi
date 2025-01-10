rem Pour un fichiers RPU créer un fichier dans lequel il n'y a que les rangees
rem ou le DP est vide. Peut fonctionner par drag and drop (glisser - deposer)
rem cf. "C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rpu_xlsx_dp_vide.groovy"
setlocal EnableExtensions
rem Adapter GPMSI_HOME dans la ligne ci-dessous si gpmsi est installe ailleurs
set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@
set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\rpu_xlsx_dp_vide.groovy
set A=%~1
set B=%~dpn1_dp_vide%~x1
call "%APP%" -script "%SCRIPT%" -a:input "%A%" -a:output "%B%"
if errorlevel 1 pause
