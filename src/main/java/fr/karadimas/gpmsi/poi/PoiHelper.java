package fr.karadimas.gpmsi.poi;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * Classe avec méthodes utilitaires pour l'utilisation de fonctions Apache POI pour Excel.
 * @author hkaradimas
 *
 */
public class PoiHelper {
  
  static Logger lg = LogManager.getLogger(PoiHelper.class);
  
  DataFormatter formatter = new DataFormatter();
  
  boolean newJavaTimeUsed = false;
  
  /**
   * Constructeur
   */
  public PoiHelper() {
    formatter.setUseCachedValuesForFormulaCells(true);
  }

  /**
   * Prendre la valeur de la cellule, garantit qu'aucune valeur null ne sera retournée.
   * A noter que ici les valeurs apparaîtront telles qu'elles sont visible dans Excel.
   * Pour les cellules qui contiennent des formules, c'est la dernière valeur qui a été stockée
   * par Excel qui sera retournée.
   * @param c La cellule à lire, peut être null.
   * @return La valeur textuelle de la cellule. N'est jamais null.
   */
  public String getCellValueAsString(Cell c) {
    return formatter.formatCellValue(c);
  }
  
  /**
   * Prendre la valeur de la cellule, garantit qu'aucune valeur null ne sera retournée.
   * Si la cellule est de type Date, utilise le dateFormat fourni.
   * Si la cellule est de type Number, utiliser le NumberFormat fourni.
   * @param c La cellule à lire, peut être null.
   * @param df Le format de date à utiliser, s'il est null, le format par défaut de java sera utilisé.
   * @param nf Le format de nombre à utiliser, s'il est null, le format par défaut de java sera utilisé.
   * @return La valeur textuelle de la cellule. Au lieu de null si c'est le cas, retourne la chaîne vide "".
   */
  public final String getCellValueAsString(Cell c, DateFormat df, NumberFormat nf) {
    if (c == null) return "";
    CellType ct = c.getCellType();
    String str = null;
    if (df == null) df = DateFormat.getDateInstance();
    if (nf == null) nf = NumberFormat.getNumberInstance(); 
    //Types contenus dans l'énumération : 
    // NONE
    // NUMERIC
    // STRING
    // FORMULA
    // BLANK
    // BOOLEAN
    // ERROR
    //si c'est une formule, on va utiliser le type et les valeurs mises en cache et on ne fait pas une ré-évaluation
    if (ct == CellType.FORMULA) ct = c.getCachedFormulaResultType();
    switch (ct) {
    case _NONE:
    case BLANK: //idem que _NONE pour nous
      str = "";
      break;
    case BOOLEAN:
      str = c.getBooleanCellValue() ? "1" : "0"; //pour un booléen met 1 pour vrai et 0 pour faux
      break;
    case NUMERIC: //nombres et dates
      if (DateUtil.isCellDateFormatted(c)) {
        //c'est bien une date
        str = df.format(c.getDateCellValue());
      }
      else {
        //c'est un nombre
        str = nf.format(c.getNumericCellValue());
      }
      break;
    case STRING: 
    case ERROR:
    default: //concerne ERROR, STRING
      str = c.getStringCellValue(); //a voir si cela met bien une erreur dans le cas de ERROR
    }
    if (str == null) return "";
    else return str;
  }

  /**
   * Prendre la valeur de la cellule en tant qu'objet.
   * Fait la différence entre les nombres et les dates.
   * Les objets qui peuvent être retournés :
   * <ul>
   * <li>CellType._NONE
   * <li>CellType.BLANK
   * <li>Boolean
   * <li>Date (java.util)
   * <li>Double
   * <li>String (en cas de type non géré, renvoie c.toString() mais c'est un bug si cela arrive)
   * <li>Byte (en cas de cellule de type erreur)
   * <li>null
   * </ul>
   * A noter que si la cellule est de type FORMULA (une formule Excel) c'est la dernière valeur
   * évaluée au moment de la sauvegarde qui est retournée. Les formules ne sont pas réévaluées ici.
   * @param c La cellule à lire, peut être null.
   * @return La valeur objet de la cellule. Peut retourner null. 
   */
  public final Object getCellValueAsObject(Cell c) {
    if (c == null) return null;
    CellType ct = c.getCellType();
    Object obj = null;
    //si c'est une formule, on va utiliser le type et les valeurs mises en cache et on ne fait pas une ré-évaluation
    if (ct == CellType.FORMULA) ct = c.getCachedFormulaResultType();
    switch (ct) {
    case _NONE:
      obj = CellType._NONE;
    case BLANK:
      obj = CellType.BLANK;
      break;
    case BOOLEAN:
      obj = c.getBooleanCellValue();
      break;
    case NUMERIC: //nombres et dates
      if (DateUtil.isCellDateFormatted(c)) {
        if (newJavaTimeUsed) {
          obj = c.getLocalDateTimeCellValue();
        }
        else {
          obj = c.getDateCellValue();
        }
      }
      else {
        //c'est un nombre
        obj = c.getNumericCellValue();
      }
      break;
    case STRING:
      obj = c.getStringCellValue();
      break;
    case ERROR:
      obj = c.getErrorCellValue();
      break;
    default: //ne devrait pas arriver !
      lg.error("Type de cellule apache POI non gere : " + ct); //on lance une erreur dans le log pour bien avertir de ce cas inhabituel
      obj = c.toString(); //cas non prévu, on convertit juste en String
    }
    return obj;
  }

  /**
   * Retourner le DataFormatter qui est utilisé par cet objet.
   * Utile pour éviter d'avoir à en recréer plusieurs.
   * Attention ne pas changer cet objet.
   * @return Le DataFormatter
   */
  public DataFormatter getDataFormatter() { return formatter; }

  /**
   * Si true, les objets Date sont les nouveaux objets {@link LocalDateTime} de java. Sinon,
   * des objets {@link Date} traditionnels sont utilisés.
   * false par défaut.
   * @return true si les nouveaux objets {@link LocalDateTime} de java sont utilisés. Défaut : false
   */
  public boolean isNewJavaTimeUsed() {
    return newJavaTimeUsed;
  }

  /**
   * Définit si on utilise la nouvelle api java.time
   * @param newJavaTimeUsed true si la nouvelle api est à utiliser
   */
  public void setNewJavaTimeUsed(boolean newJavaTimeUsed) {
    this.newJavaTimeUsed = newJavaTimeUsed;
  }
  
}
