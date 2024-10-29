:encoding=UTF-8:

Lien jdbc direct vers un fichier local (ici C:\t\BOXi\test.mv.db :

jdbc:h2://C:/t/BOXi/test

Connexion à la base :

import groovy.sql.Sql

gsql = Sql.newInstance(jdbcurl, jdbcuser, jdbcpassword, 'org.h2.Driver')

Exemple d'import direct depuis un fichier UTF-8 avec séparateur tabulation dans une table qui n'a qu'une seule
colonne : dte (de type date) :

gsql = Sql.newInstance(jdbcurl, jdbcuser, jdbcpassword, 'org.h2.Driver')
sql = "create table dates(dte date) as select dte from CSVREAD('$infileStr', null, 'charset=UTF-8 fieldSeparator=' || CHAR(9))"
gsql.execute(sql as String)

