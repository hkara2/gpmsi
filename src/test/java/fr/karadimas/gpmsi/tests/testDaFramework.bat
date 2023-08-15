rem Lancement du script de test des operations DAO 
rem Utilise les table daotests_X
rem Adapter l'url ebudim05:5432/pmsi01, le jdbcuser en consequence

setlocal EnableExtensions
set APP=C:\app\gpmsi\v1.2\gpmsi.bat
set SCRIPT=C:\app\gpmsi\v1.2\src\fr\karadimas\gpmsi\tests\testDaFramework.groovy
rem Driver JDBC
set PMSIXML_XCP=C:\hkchse\dev\pmsixml\local\pmsi01db\jdbc\postgresql-42.1.4.jar
call %APP% Groovy -script %SCRIPT% -a:jdbcurl jdbc:postgresql://ebudim05:5432/pmsi01 -a:jdbcuser hk -a:jdbcpwd "?pEntrez le mot de passe pour hk" %*
