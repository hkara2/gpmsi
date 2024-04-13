rem Comparaison entre deux fichiers de RUM
rem Ce fichier est a copier-coller a cote des fichiers de RUM/RSS a controler
rem puis a adapter

setlocal EnableExtensions

rem Adapter GPMSI_HOME dans la ligne ci-dessous si gpmsi est installe ailleurs
set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@

set APP=%GPMSI_HOME%\gpmsi.bat

set SCRIPT=%GPMSI_HOME%\scripts\groovy\comparer_rums_v2.groovy

rem Fichier RUMS/RSS avant, ce sera 'a'
set A=mettre_ici_chemin_du_fichier_des_RUMSRSS
rem Fichier RUMS/RSS apres, ce sera 'b'
set B=mettre_ici_chemin_du_fichier_des_RUMSRSS
rem Il faut faire un appel local avec arguments car sinon on ne peut pas utiliser ~n pour extraire le nom
call :launch %A% %B%
goto :eof

rem Produire le fichier des differences a/b.
rem On peut ajouter a la fin -comparercmd si on veut comparer sur la base des
rem ghms et cmds resultant du groupage
:launch
set O=a_%~n1__b_%~n2.csv
call "%APP%" -script "%SCRIPT%" -a:input_a "%A%" -a:input_b "%B%" -a:output "%O%"
