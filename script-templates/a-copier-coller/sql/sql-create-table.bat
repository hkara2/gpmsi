rem Appel du script sql_create_table.groovy
rem Copier-coller puis adapter
setlocal enableextensions

if not exist GPMSI_HOME set GPMSI_HOME=C:\app\gpmsi\v@PROJECT_VERSION@
set APP=%GPMSI_HOME%\gpmsi.bat
rem Chemin du script.
set SCRIPT=%GPMSI_HOME%\scripts\groovy\sql_create_table.groovy
rem Nom de la table a creer
set TABLE=nom_table
rem Chemin vers le fichier de definition de colonnes
set COLMD=sql_metadonnees_colonnes_nom_table.csv
rem Pilote JDBC
set JDBCDRIVER=org.h2.Driver
rem Url JDBC vers la base
set JDBCURL=jdbc:h2:./bdd
rem User JDBC
set JDBCUSER=sa
rem Mot de passe JDBC
set JDBCPWD=

call %APP% -script "%SCRIPT%" -a:table "%TABLE%" -a:colmd "%COLMD%" -a:jdbcurl "%JDBCURL%" -a:jdbcuser "%JDBCUSER%" -a:jdbcpwd "%JDBCPWD%" -a:jdbcdriver "%JDBCDRIVER%"
