//:encoding=UTF-8:
//Exemple de script de maintenance de table
/**
 * Script de maintenance de la base de données.
 * à appeler avec les arguments jdbcurl, jdbcuser, jdbcpwd
 * Liens vers les tables (par ordre alphabétique) :
 * ##>AAA: table AAA
 *
 * (dans jEdit avec le plugin "Hyperlinks" il suffit d'appuyer sur la touche
 * CTRL et de cliquer, sinon, faire la recherche manuellement sur le nom suivi
 * de ':')
 */

 /*
  * Creation d'une table de test
  * create table AA(
  *   X varchar(),
  *   Y integer,
  *   Z date
  * )
  * 
  * Création d'une séquence de test :
  * create sequence AA_seq
  * 
  * Création d'un index de test :
  * create index AA_Y on AA(Y)
  *
  * Création d'une ligne :
  * insert into AA(X, Y, Z) values ('X1', 1, date '2021-12-01')
  */

import groovy.sql.Sql

/*
 * Ex. de code pour voir si une colonne existe
 *      def colNomtableNomcol = metadata.getColumns(null, null, "nomtable", "nomcol")
 *      if (!colNomtableNomcol.next()) { ... //code pour ajouter colonne
 */
/** fonction pour voir si une colonne existe */
def columnExists(Sql gsql, String tableName, String columnName) {
    def cols = gsql.connection.getMetaData().getColumns(null, null, tableName, columnName)
    if (cols.next()) return true else return false
}

/** fonction pour voir si une sequence existe */
def sequenceExists(Sql gsql, String sequenceName) {
    def r = gsql.firstRow '''
    SELECT COUNT(*) as seqcount 
    FROM information_schema.sequences 
    WHERE sequence_name=:sequenceName''', sequenceName: sequenceName
    return r.seqcount > 0
}

/** fonction pour voir si un index existe */
def indexExists(Sql gsql, String indexName) {
    def r = gsql.firstRow '''
    SELECT COUNT(*) as idxcount 
    FROM information_schema.indexes 
    WHERE index_name=:indexName''', indexName: indexName
    return r.idxcount > 0
}

def columnType(Sql gsql, String table, String col) {
    def r = gsql.firstRow '''
    select data_type from information_schema.columns
    where table_name = :tableName and column_name = :columnName
    ''', [tableName: table, columnName: col]
    return r.data_type
}

def rowExists(Sql gsql, String table, String whereClause) {
    def sq = 'select count(*) as rc from ' + table + ' where ' + whereClause
    def r = gsql.firstRow sq
    return r.rc > 0
}










def db = [driver:'org.h2.Driver',
          url: args.jdbcurl, user: args.jdbcuser, password: args.jdbcpwd]
gsql = Sql.newInstance(db.url, db.user, db.password, db.driver)
println "Connecte a H2"
def metadata = gsql.connection.getMetaData()
    
def tableAA = metadata.getTables(null, null, "AA", null)
if (!tableAA.next()) {
    println "Table AA n'existe pas"
}
else {
    println "Table AA existe"
}

println "Sequence AA_SEQ existe : " + (sequenceExists(gsql, 'AA_SEQ'))

println "Index AA_Y exists : " + (indexExists(gsql, 'AA_Y'))

println "Type of A.X : " + (columnType(gsql, 'AA', 'X'))

println "Type of A.Y : " + (columnType(gsql, 'AA', 'Y'))

println "Type of A.Z : " + (columnType(gsql, 'AA', 'Z'))

println "Row for X1 exists : " + (rowExists(gsql, 'AA', "X = 'X1' "))

println "Row for X2 exists : " + (rowExists(gsql, 'AA', "X = 'X2' "))


//--- gabarit pour copier-coller

    if (false) {
        
    //-- début pour copier coller
    
    //-- TEMPLATE description ...
    // Table ##:TEMPLATE: description ...
    def tableTEMPLATE = metadata.getTables(null, null, "template", null)
    if (!tableTEMPLATE.next()) {
        println "Creation de la table template"
        gsql.execute '''
        /* decrire la table ici */
        CREATE TABLE template (
          template_id BIGINT PRIMARY KEY, /* cle numero sequentiel unique */
        )
        '''
    }
    if (!sequenceExists(gsql, 'template_seq')) {
        println "Creation de la sequence template_seq"
        gsql.execute 'CREATE SEQUENCE template_seq'
    }
    
    //-- fin pour copier coller
    
    }


//--- fin gabarit pour copier-coller
    
