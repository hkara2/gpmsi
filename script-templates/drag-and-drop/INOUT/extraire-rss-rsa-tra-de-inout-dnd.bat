rem Extraire les fichiers se terminant par .tra.txt , .rsa , .rss.ini.txt
rem des fichiers INOUT
rem Il suffit de donner en argument un fichier .in.zip ou .out.zip l'autre
rem fichier est automatiquement deduit (les deux doivent etre dans le meme repertoire)
rem
rem Exemple d'utilisation :
rem extraire-rss-rsa-tra-de-inout-dnd.bat infos-um.csv 910019447.2025.12.MCO.SEJOURS.SEJOURS.20260206003746.in.zip
rem N.B. marche si on fait un "glisser-deposer" sur le fichier bat

setlocal enableextensions

if not defined GPMSI_HOME set GPMSI_HOME=%GPMSI_BASE%\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat
set SCRIPT=%GPMSI_HOME%\scripts\groovy\inout_extraire_fichiers.groovy

rem Fichier archive zip
set A=%~1
"%APP%" -script "%SCRIPT%" -a:input "%A%" -a:suffixe .tra.txt -a:suffixe .rsa -a:suffixe .rss.ini.txt
