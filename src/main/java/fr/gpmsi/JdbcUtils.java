package fr.gpmsi;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Quelques utilitaire JDBC utiles lorsque la base de données est simple (un seul catalogue,
 * un seul schéma). 
 * @author hkaradimas
 *
 */
public class JdbcUtils {
  public static Logger lg = Logger.getLogger(JdbcUtils.class); 
  
  private static final int NO_CONVERTER = 0;       //pas de convertisseur nécessaire / disponible
  private static final int NUMBER_CONVERTER = 1;   //convertisseur de nombre
  private static final int DATETIME_CONVERTER = 2; //convertisseur de date + time
  private static final int DATE_CONVERTER = 3;     //convertisseur de date
  private static final int TIME_CONVERTER = 4;     //convertisseur de time
  private static HashMap<String, Integer> jdbcTypesByName = new HashMap<>();
  private static HashMap<Integer, Integer> converterTypeByTypeNumber = new HashMap<>();
  
  static {
    jdbcTypesByName.put("ARRAY", Types.ARRAY);
    jdbcTypesByName.put("BIGINT", Types.BIGINT);
    jdbcTypesByName.put("BINARY", Types.BINARY);
    jdbcTypesByName.put("BIT", Types.BIT);
    jdbcTypesByName.put("BLOB", Types.BLOB);
    jdbcTypesByName.put("BOOLEAN", Types.BOOLEAN);
    jdbcTypesByName.put("CHAR", Types.CHAR);
    jdbcTypesByName.put("CLOB", Types.CLOB);
    jdbcTypesByName.put("DATALINK", Types.DATALINK);
    jdbcTypesByName.put("DATE", Types.DATE);
    jdbcTypesByName.put("DECIMAL", Types.DECIMAL);
    jdbcTypesByName.put("DISTINCT", Types.DISTINCT);
    jdbcTypesByName.put("DOUBLE", Types.DOUBLE);
    jdbcTypesByName.put("FLOAT", Types.FLOAT);
    jdbcTypesByName.put("INTEGER", Types.INTEGER);
    jdbcTypesByName.put("JAVA_OBJECT", Types.JAVA_OBJECT);
    jdbcTypesByName.put("LONGNVARCHAR", Types.LONGNVARCHAR);
    jdbcTypesByName.put("LONGVARBINARY", Types.LONGVARBINARY);
    jdbcTypesByName.put("LONGVARCHAR", Types.LONGVARCHAR);
    jdbcTypesByName.put("NCHAR", Types.NCHAR);
    jdbcTypesByName.put("NCLOB", Types.NCLOB);
    jdbcTypesByName.put("NULL", Types.NULL);
    jdbcTypesByName.put("NUMERIC", Types.NUMERIC);
    jdbcTypesByName.put("NVARCHAR", Types.NVARCHAR);
    jdbcTypesByName.put("OTHER", Types.OTHER);
    jdbcTypesByName.put("REAL", Types.REAL);
    jdbcTypesByName.put("REF", Types.REF);
    jdbcTypesByName.put("REF_CURSOR", Types.REF_CURSOR);
    jdbcTypesByName.put("ROWID", Types.ROWID);
    jdbcTypesByName.put("SMALLINT", Types.SMALLINT);
    jdbcTypesByName.put("SQLXML", Types.SQLXML);
    jdbcTypesByName.put("STRUCT", Types.STRUCT);
    jdbcTypesByName.put("TIME", Types.TIME);
    jdbcTypesByName.put("TIME_WITH_TIMEZONE", Types.TIME_WITH_TIMEZONE);
    jdbcTypesByName.put("TIMESTAMP", Types.TIMESTAMP);
    jdbcTypesByName.put("TIMESTAMP_WITH_TIMEZONE", Types.TIMESTAMP_WITH_TIMEZONE);
    jdbcTypesByName.put("TINYINT", Types.TINYINT);
    jdbcTypesByName.put("VARBINARY", Types.VARBINARY);
    jdbcTypesByName.put("VARCHAR", Types.VARCHAR);
    //déclarer les types de convertisseurs nécessaires par type de données
    converterTypeByTypeNumber.put(Types.ARRAY, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.BIGINT, NUMBER_CONVERTER);
    converterTypeByTypeNumber.put(Types.BINARY, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.BIT, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.BLOB, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.BOOLEAN, NUMBER_CONVERTER);
    converterTypeByTypeNumber.put(Types.CHAR, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.CLOB, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.DATALINK, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.DATE, DATE_CONVERTER);
    converterTypeByTypeNumber.put(Types.DECIMAL, NUMBER_CONVERTER);
    converterTypeByTypeNumber.put(Types.DISTINCT, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.DOUBLE, NUMBER_CONVERTER);
    converterTypeByTypeNumber.put(Types.FLOAT, NUMBER_CONVERTER);
    converterTypeByTypeNumber.put(Types.INTEGER, NUMBER_CONVERTER);
    converterTypeByTypeNumber.put(Types.JAVA_OBJECT, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.LONGNVARCHAR, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.LONGVARBINARY, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.LONGVARCHAR, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.NCHAR, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.NCLOB, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.NULL, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.NUMERIC, NUMBER_CONVERTER);
    converterTypeByTypeNumber.put(Types.NVARCHAR, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.OTHER, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.REAL, NUMBER_CONVERTER);
    converterTypeByTypeNumber.put(Types.REF, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.REF_CURSOR, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.ROWID, NUMBER_CONVERTER);
    converterTypeByTypeNumber.put(Types.SMALLINT, NUMBER_CONVERTER);
    converterTypeByTypeNumber.put(Types.SQLXML, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.STRUCT, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.TIME, TIME_CONVERTER);
    converterTypeByTypeNumber.put(Types.TIME_WITH_TIMEZONE, TIME_CONVERTER);
    converterTypeByTypeNumber.put(Types.TIMESTAMP, DATETIME_CONVERTER);
    converterTypeByTypeNumber.put(Types.TIMESTAMP_WITH_TIMEZONE, DATETIME_CONVERTER);
    converterTypeByTypeNumber.put(Types.TINYINT, NUMBER_CONVERTER);
    converterTypeByTypeNumber.put(Types.VARBINARY, NO_CONVERTER);
    converterTypeByTypeNumber.put(Types.VARCHAR, NO_CONVERTER);
  }

  /**
   * Constructeur par défaut
   */
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
  
  /**
   * Est-ce que la colonne existe ?
   * @param cxn La connexion Jdbc
   * @param tableName Le nom de la table
   * @param columnName Le nom de la colonne
   * @param caseSensitive Est-ce que la comparaison doit être sensible à la casse ?
   * @return true si la colonne existe
   * @throws SQLException -
   */
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
  
  /**
   * Analyser les principaux formats de donnée pour convertir un texte en objet utilisable dans une instruction jdbc.
   * Pour cela on doit fournir en plus du texte à analyser le nom du type sql visé, et éventuellement le format qu'on désire,
   * si ce n'est pas le type par défaut.
   * Exemple d'utilisation :
   * <pre>
   * </pre>
   * @see DateTimeFormatter
   * @param text Le texte à convertir
   * @param sqlType Le type sql qui est désiré
   * @param dataFormat Le format spécial de donnée qui doit être utilisé (peut être null, auquel cas un format par défaut est choisi,
   * DecimalFormat.getInstance() ou DateTimeFormatter.ISO_OFFSET_DATE_TIME ou DateTimeFormatter.ISO_OFFSET_DATE ou DateTimeFormatter.ISO_OFFSET_TIME)
   * @param columnName Le nom de colonne (sert au stockage du format analysé dans le cache des formats)
   * @param formatsCache Une Map qui sert à stocker les formats déjà analysés, pour ne pas les réanalyser à chaque appel
   * @param loc la Locale à utiliser, si null on utilise la Locale du système. Parfois on veut changer de Locale, par exemple lorsque les nombres sont au format américain.
   * @return l'objet qui correspond à la conversion ou null si il y a eu une erreur lors de la conversion. L'erreur est envoyée avec le niveau DEBUG dans le log
   * @throws ParseException si il y a une erreur d'analyse de nombre
   */
  public static Object parseText(String text, String sqlType, String dataFormat, String columnName, Map<String, Object> formatsCache, Locale loc)
      throws ParseException 
  {
    int sqlTypeNr = getTypeNumber(sqlType);
    if (sqlTypeNr == Integer.MIN_VALUE) {
      lg.debug("Type java sql inconnu : '"+sqlType+"'");
      return null;
    }
    int convType = converterTypeByTypeNumber.get(sqlTypeNr);
    switch (convType) {
    case NO_CONVERTER:
      //pas de conversion, retour tel quel
      return text;
    case NUMBER_CONVERTER:
      //conversion de nombre
      NumberFormat nf = (DecimalFormat) formatsCache.get(columnName);
      if (nf == null) {
        if (dataFormat == null) {
          if (loc == null) nf = DecimalFormat.getInstance(); 
          else nf = DecimalFormat.getInstance(loc);
        }
        else {
          nf = new DecimalFormat(dataFormat);
        }
        formatsCache.put(columnName, nf);
      }
      return nf.parseObject(text);
    case DATETIME_CONVERTER:
      //cas commun pour DATE, TIME et DATETIME
    case DATE_CONVERTER:
      //cas commun pour DATE, TIME et DATETIME
    case TIME_CONVERTER:
      DateTimeFormatter dtf = (DateTimeFormatter) formatsCache.get(columnName);
      if (dtf == null) {
        if (dataFormat == null) {
          if (convType == DATETIME_CONVERTER) dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
          else if (convType == DATE_CONVERTER) dtf = DateTimeFormatter.ISO_LOCAL_DATE;
          else if (convType == TIME_CONVERTER) dtf = DateTimeFormatter.ISO_LOCAL_TIME;
          else lg.error("Erreur interne : convType a la valeur "+convType);
        }
        else {
          if (loc == null) dtf = DateTimeFormatter.ofPattern(dataFormat);
          else dtf = DateTimeFormatter.ofPattern(dataFormat, loc);
        }
        formatsCache.put(columnName, dtf);
      }
      return dtf.parse(text);
    }
    lg.error("Erreur interne : on ne devrait jamais arriver après le switch dans la méthode JdbcUtils.parseText");
    return null;
  }
  
  /**
   * Retourner le numéro de type de données JDBC pour le nom de type donné. Le nom doit être en majuscules.
   * @see Types
   * @param typeName Le nom de type sql. Doit être impérativement en majuscules.
   * @return Le numéro de type java.sql.Types ou Integer.MIN_VALUE si le nom ne correspond à aucun des types prédéfinis dans cette classe.
   */
  public static int getTypeNumber(String typeName) {
    Integer typeNr = jdbcTypesByName.get(typeName);
    if (typeNr == null) return Integer.MIN_VALUE;
    else return typeNr;
  }
  
}
