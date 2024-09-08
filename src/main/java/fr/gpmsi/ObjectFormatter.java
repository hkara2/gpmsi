package fr.gpmsi;

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Formater un objet avec des préférences de formatage.
 * Par défaut le format est approprié à la France ce qui est ce qu'il faut pour le PMSI.
 * @author hkaradimas
 *
 */
public class ObjectFormatter {

  NumberFormat numberFormat;
  DateFormat dateFormat;
  DateFormat timeFormat;
  DateFormat dateTimeFormat;
  
  /** Le formateur par défaut. Attention à ne rien changer dans cette instance d'objet ! */
  public static final ObjectFormatter defaultFormatter = new ObjectFormatter();
  
  /** Constructeur par défaut */
  public ObjectFormatter() {
    numberFormat = NumberFormat.getInstance(Locale.FRANCE); //forcer le format Français par défaut
    numberFormat.setGroupingUsed(false); //mais ne pas séparer les chiffres entre eux
    numberFormat.setMaximumFractionDigits(40); //et on garde jusqu'à 40 décimales
    dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    timeFormat = new SimpleDateFormat("HH:mm:ss");
    dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  }

  /**
   * Essayer de convertir au mieux l'objet en String, avec les formats dont on dispose
   * @param obj L'objet à convertir
   * @return La string résultante
   */
  public String format(Object obj) {
    if (obj == null) return "";
    if (obj instanceof String) return (String)obj;
    if (obj instanceof Number) return numberFormat.format(obj);
    if (obj instanceof Time) return timeFormat.format(obj);
    if (obj instanceof Timestamp) return dateTimeFormat.format(obj);
    if (obj instanceof Date) return dateFormat.format(obj);
    return obj.toString(); //utiliser methode "toString()" par défaut
  }
  
  /**
   * Formater un objet dont on connaît le type "sql" équivalent.
   * Seuls marcheront les types chaines de caracteres, date, heure, date + heure, nombres.
   * Pour les types connus, il y aura l'appel au formatteur numberFormat, dateFormat, timeFormat, dateTimeFormat
   * fourni.
   * Les autres sont simplement convertis en appelant .toString(), ce qui peut avoir des résultats assez variables et inattendus
   * @param obj L'objet à formater en String
   * @param sqlType Le type SQL tel que défini dans {@link Types}
   * @return La String qui représente l'objet.
   */
  public String formatSql(Object obj, int sqlType) {
    if (obj == null) return "";
    switch (sqlType) {
    case Types.ARRAY: return obj.toString();
    case Types.BIGINT: return numberFormat.format(obj);
    case Types.BINARY: return obj.toString();
    case Types.BIT: return obj.toString();
    case Types.BLOB: return obj.toString();
    case Types.BOOLEAN: return obj.toString();
    case Types.CHAR: return obj.toString();
    case Types.CLOB: return obj.toString();
    case Types.DATALINK: return obj.toString();
    case Types.DATE: return dateFormat.format(obj);
    case Types.DECIMAL: return numberFormat.format(obj);
    case Types.DISTINCT: return obj.toString();
    case Types.DOUBLE: return numberFormat.format(obj);
    case Types.FLOAT: return numberFormat.format(obj);
    case Types.INTEGER: return numberFormat.format(obj);
    case Types.JAVA_OBJECT: return obj.toString();
    case Types.LONGNVARCHAR: return obj.toString();
    case Types.LONGVARBINARY: return obj.toString();
    case Types.LONGVARCHAR: return obj.toString();
    case Types.NCHAR: return obj.toString();
    case Types.NCLOB: return obj.toString();
    case Types.NULL: return obj.toString(); //ne devrait pas arriver
    case Types.NUMERIC: return numberFormat.format(obj);
    case Types.NVARCHAR: return obj.toString();
    case Types.OTHER: return obj.toString();
    case Types.REAL: return numberFormat.format(obj);
    case Types.REF: return obj.toString();
    case Types.REF_CURSOR: return obj.toString();
    case Types.ROWID: return obj.toString();
    case Types.SMALLINT: return numberFormat.format(obj);
    case Types.SQLXML: return obj.toString();
    case Types.STRUCT: return obj.toString();
    case Types.TIME: return timeFormat.format(obj);
    case Types.TIME_WITH_TIMEZONE: return timeFormat.format(obj);
    case Types.TIMESTAMP: return dateTimeFormat.format(obj);
    case Types.TIMESTAMP_WITH_TIMEZONE: return dateTimeFormat.format(obj);
    case Types.TINYINT: return numberFormat.format(obj);
    case Types.VARBINARY: return obj.toString();
    case Types.VARCHAR: return obj.toString();
    default: return obj.toString(); //par défaut convertir via "toString"
    }
  }
  
}
