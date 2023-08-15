package fr.karadimas.gpmsi;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Quelques utilitaire JDBC utiles lorsque la base de données est simple (un seul catalogue,
 * un seul schéma). 
 * @author hkaradimas
 *
 */
public class JdbcUtils {

  public JdbcUtils() {
  }

  /**
   * Fonction utilitaire pour trouver si une table existe dans la base de données dont la
   * connexion est donnée.
   * @param cxn La connexion de base de données
   * @param name Le nom de la table
   * @param caseSensitive True si la casse doit être identique, False sinon.
   * @return True si la table existe dans la base, False sinon 
   * @throws SQLException Si erreur de bdd
   */
  public static final boolean tableExists(Connection cxn, String name, boolean caseSensitive)
      throws SQLException 
  {
    DatabaseMetaData md = cxn.getMetaData();
    ResultSet rs = md.getTables(null, null, name, null);
    if (rs.next()) {
      String dbTableName = rs.getString(3); //correspond a la colonne 'TABLE_NAME' (cf. doc)
      rs.close();
      if (caseSensitive) return dbTableName.equals(name);
      else return dbTableName.equalsIgnoreCase(name);
    }
    else {
      rs.close();
      return false;
    }
  }
  
  public static final boolean columnExists(Connection cxn, String tableName, String columnName, boolean caseSensitive)
      throws SQLException 
  {
    DatabaseMetaData md = cxn.getMetaData();
    ResultSet rs = md.getColumns(null, null, tableName, columnName);
    if (rs.next()) {
      String dbColumnName = rs.getString(4); //correspond a la colonne 'COLUMN_NAME' (cf. doc)
      rs.close();
      if (caseSensitive) return dbColumnName.equals(columnName);
      else return dbColumnName.equalsIgnoreCase(columnName);
    }
    else {
      rs.close();
      return false;
    }
  }

  /* 
   * getColumns(String catalog,
                     String schemaPattern,
                     String tableNamePattern,
                     String columnNamePattern)
   */
  
  /**
   * Fonction pour tester si un index existe.
   * Marche bien pour une base H2, à tester plus en profondeur pour d'autres
   * types de bases.
   * @param cxn la connexion
   * @param tableName Le nom de la table
   * @param indexName Le nom de l'index
   * @param caseSensitive True si la recherche doit être sensible à la casse
   * @return true si index existe
   * @throws SQLException si erreur bdd
   */
  public static final boolean indexExists(Connection cxn, String tableName, String indexName, boolean caseSensitive)
      throws SQLException 
  {
    DatabaseMetaData md = cxn.getMetaData();
    ResultSet rs = md.getIndexInfo(null, null, tableName, false, true); //recupere tous les indexs de la table
    while (rs.next()) {
      String dbIndexName = rs.getString(6); //correspond a la colonne 'INDEX_NAME' (cf. doc)
      if (caseSensitive) {
        if (dbIndexName.equals(indexName)) {
          rs.close();
          return true;
        }
      }
      else {
        if (dbIndexName.equalsIgnoreCase(indexName)) {
          rs.close();
          return true;
        }
      }
    }//while
    //pas trouvé
    rs.close();
    return false;
  }
  
  /**
   * Créer un timestamp avec la date / heure courante 
   * @return Le timestamp
   */
  public static Timestamp now() { return new Timestamp(System.currentTimeMillis()); }
  
}
