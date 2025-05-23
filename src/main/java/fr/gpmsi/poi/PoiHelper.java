package fr.gpmsi.poi;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyContext;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFCell;

import fr.gpmsi.StringTable;
import fr.gpmsi.StringUtils;

import static fr.gpmsi.StringUtils.isEmpty;

/**
 * Classe avec méthodes utilitaires pour l'utilisation de fonctions Apache POI pour Excel.
 * Les classes statiques sont utilisables directement, les autres nécessitent de créer un objet qui crée un DataFormatter
 * (ce qui permet un cache des valeurs de formats) et/ou le champ newJavaTimeUsed.
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
   * Ajuste le format de la cellule a l'aide du texte de format fourni.
   * Crée si besoin le style et le place dans le cache des styles.
   * @param wb Le classeur (Workbook)
   * @param formatCache La Map qui permet de stocker les {@link CellStyle} par nom
   * @param cell La cellule dont le style doit être renseigné
   * @param format Le texte qui représente le format, exemples "m/d/yy", "m/d/yy h:mm", "h:mm"
   */
  public static void adjustCellStyle(Workbook wb, Map<String, CellStyle> formatCache, SXSSFCell cell, String format) {
    CreationHelper creationHelper = wb.getCreationHelper();
    CellStyle cellStyle = formatCache.get(format);
    if (cellStyle == null) {
      cellStyle = wb.createCellStyle();
      cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
      formatCache.put(format, cellStyle);
    }
    cell.setCellStyle(cellStyle);
  }
  
  /**
   * Cloner les styles d'une cellule à l'autre.
   * Attention c'est un vrai clonage, il vaut mieux utiliser la méthode {@link #copyStyles(Cell, Cell, CellCopyContext)}
   * qui gère les multiples occurences de styles.
   * @param from cellule dont on veut cloner le style
   * @param to cellule vers laquelle écrire les styles
   */
  public static void cloneStyles(Cell from, Cell to) {
    CellStyle s = from.getCellStyle();
    Map<String, Object> props = getFormatProperties(s);
    CellUtil.setCellStyleProperties(to, props);
  }

  /**
   * Copier le style de srcCell vers destCell. Le {@link CellCopyContext} fourni sert
   * à garder trace des styles qui ont déjà été clonés, pour pouvoir les réutiliser et ainsi
   * éviter qu'ils ne soient recréés inutilement.
   * Cette méthode est faite à partir de contenu extrait de {@link CellUtil#copyCell(Cell, Cell, org.apache.poi.ss.usermodel.CellCopyPolicy, CellCopyContext)}
   * qui pour l'instant est encore en beta.
   * Si la source ou la destination sont nulles, il n'y a pas d'exception lancée, mais un avertissement est
   * envoyé sur le log.
   * @param srcCell cellule source
   * @param destCell cellule destination
   * @param context contexte de copie
   */
  public static void copyStyles(Cell srcCell, Cell destCell,  CellCopyContext context) {
    if (srcCell == null) {
      lg.warn("srcCell est null, copie de styles abandonnee");
      return;
    }
    if (destCell == null) {
      lg.warn("destCell est null, copie de styles abandonnee");
      return;
    }
    if (srcCell.getSheet() != null && destCell.getSheet() != null &&
        destCell.getSheet().getWorkbook() == srcCell.getSheet().getWorkbook()) {
        destCell.setCellStyle(srcCell.getCellStyle());
    } else {
        CellStyle srcStyle = srcCell.getCellStyle();
        CellStyle destStyle = context == null ? null : context.getMappedStyle(srcStyle);
        if (destStyle == null) {
            destStyle = destCell.getSheet().getWorkbook().createCellStyle();
            destStyle.cloneStyleFrom(srcStyle);
            if (context != null) context.putMappedStyle(srcStyle, destStyle);
        }
        destCell.setCellStyle(destStyle);
    }
  }
  
  /**
   * Prendre la valeur de la cellule, garantit qu'aucune valeur null ne sera retournée.
   * Si la cellule est de type Date, utilise le dateFormat fourni.
   * Si la cellule est de type Number, utiliser le NumberFormat fourni.
   * @param c La cellule à lire, peut être null.
   * @param df Le format de date/heure à utiliser, s'il est null, le format par défaut de java sera utilisé.
   * @param nf Le format de nombre à utiliser, s'il est null, le format par défaut de java sera utilisé.
   * @return La valeur textuelle de la cellule. Au lieu de null si c'est le cas, retourne la chaîne vide "".
   */
  public static final String getCellValueAsString(Cell c, DateFormat df, NumberFormat nf) {
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
   * Mettre l'objet (issu de {@link #getCellValueAsObject(Cell)} ) dans la cellule.
   * @param c la cellule dont on veut changer la valeur. Ne peut pas être null.
   * @param obj l'objet à mettre, peut être null
   * @throws NullPointerException si c est null
   */
  public static void setCellValueFromObject(Cell c, Object obj)
  throws NullPointerException
  {
    if (c == null) throw new NullPointerException();
    if (obj == null) {
      c.setBlank();
    }
    else if (obj.equals(CellType._NONE)) {
      c.setBlank();
    }
    else if (obj.equals(CellType.BLANK)) {
      c.setBlank();
    }
    else if (obj instanceof Number) {
      Number numObj = (Number) obj;
      c.setCellValue(numObj.doubleValue());
    }
    else if (obj instanceof Date) {
      c.setCellValue((Date)obj);
    }
    else if (obj instanceof LocalDate) {
      c.setCellValue((LocalDate)obj);
    }
  }
  
  /**
   * La cellule est-elle de type BLANK
   * @param c la cellule à tester
   * @return true si la cellule est de type BLANK
   */
  public static boolean isCellBlank(Cell c) {
    return c != null && c.getCellType() == CellType.BLANK;
  }
  
  /**
   * Est la recopie quasi-exacte de {@link CellUtil#getFormatProperties(CellStyle)}
   * qui est private et pas public.
   * @param style le {@link CellStyle} dont on veut avoir les propriétés
   * @return une Map avec les propriétés par leur nom dans {@link CellUtil}.
   */
  public static  Map<String, Object> getFormatProperties(CellStyle style) {
    Map<String, Object> properties = new HashMap<>();
    properties.put(CellUtil.ALIGNMENT, style.getAlignment());
    properties.put(CellUtil.VERTICAL_ALIGNMENT, style.getVerticalAlignment());
    properties.put(CellUtil.BORDER_BOTTOM, style.getBorderBottom());
    properties.put(CellUtil.BORDER_LEFT, style.getBorderLeft());
    properties.put(CellUtil.BORDER_RIGHT, style.getBorderRight());
    properties.put(CellUtil.BORDER_TOP, style.getBorderTop());
    properties.put(CellUtil.BOTTOM_BORDER_COLOR, style.getBottomBorderColor());
    properties.put(CellUtil.DATA_FORMAT, style.getDataFormat());
    properties.put(CellUtil.FILL_PATTERN, style.getFillPattern());
    
    properties.put(CellUtil.FILL_FOREGROUND_COLOR, style.getFillForegroundColor());
    properties.put(CellUtil.FILL_BACKGROUND_COLOR, style.getFillBackgroundColor());
    properties.put(CellUtil.FILL_FOREGROUND_COLOR_COLOR, style.getFillForegroundColorColor());
    properties.put(CellUtil.FILL_BACKGROUND_COLOR_COLOR, style.getFillBackgroundColorColor());

    properties.put(CellUtil.FONT, style.getFontIndex());
    properties.put(CellUtil.HIDDEN, style.getHidden());
    properties.put(CellUtil.INDENTION, style.getIndention());
    properties.put(CellUtil.LEFT_BORDER_COLOR, style.getLeftBorderColor());
    properties.put(CellUtil.LOCKED, style.getLocked());
    properties.put(CellUtil.RIGHT_BORDER_COLOR, style.getRightBorderColor());
    properties.put(CellUtil.ROTATION, style.getRotation());
    properties.put(CellUtil.TOP_BORDER_COLOR, style.getTopBorderColor());
    properties.put(CellUtil.WRAP_TEXT, style.getWrapText());
    properties.put(CellUtil.SHRINK_TO_FIT, style.getShrinkToFit());
    properties.put(CellUtil.QUOTE_PREFIXED, style.getQuotePrefixed());
    return properties;
  }
  
  /**
   * Calculer le numéro de colonne à partir de la colonne en lettres.
   * Exemple :
   * <pre>
   * getColumnNumber("A"); //retourne 1
   * getColumnNumber("Z"); //retourne 26
   * getColumnNumber("AA"); //retourne 27
   * getColumnNumber("AAA"); //retourne 703 (1 + 26 + 26*26
   * </pre>
   * (Si on a un doute sur un numéro, taper dans Excel la formule <code>=COLONNE()</code> on aura le numéro de colonne Excel)
   * @param columnLetters La colonne en lettres.
   * @return Le numéro de colonne pour Poi (part de 0, c'est celui de Excel - 1)
   */
  public static int getColumnNumber(String columnLetters) {
    int pow = 1; //les puissances de 26
    int acc = 0; //accumulation des valeurs
    int n = columnLetters.length();
    for (int i = n-1 ; i >= 0; i--) {
      char letter = Character.toUpperCase(columnLetters.charAt(i));
      if (letter < 'A' || 'Z' < letter) return -1; //erreur dans les coordonnées la colonne doit être entre A et Z
      acc += (letter - 'A' + 1) * pow; //A=1, Z=26
      pow = pow * 26;
    }
    return acc-1;
  }
  
  /**
   * Prendre la valeur de la cellule, garantit qu'aucune valeur null ne sera retournée.
   * A noter que ici les valeurs apparaîtront telles qu'elles sont visible dans Excel.
   * Pour les cellules qui contiennent des formules, c'est la dernière valeur qui a été stockée
   * par Excel qui sera retournée.
   * Attention les cellules vierges de type BLANK renvoient le texte "BLANK" !
   * @param c La cellule à lire, peut être null.
   * @return La valeur textuelle de la cellule. N'est jamais null.
   */
  public String getCellValueAsString(Cell c) {
    return formatter.formatCellValue(c);
  }
  
  /**
   * Prendre la valeur de la cellule en tant qu'objet.
   * Ici cette méthode n'est pas statique car elle dépend de {@link #newJavaTimeUsed}.
   * Fait la différence entre les nombres et les dates.
   * Les objets qui peuvent être retournés :
   * <ul>
   * <li>CellType._NONE
   * <li>CellType.BLANK
   * <li>Boolean
   * <li>Date (java.util) (si {@link #newJavaTimeUsed} est false
   * <li>LocalDateTime (si  {@link #newJavaTimeUsed} est true
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
    
  /**
   * Lire un onglet en tant que table dans un objet StringTable.
   * La définition de "table" est assez restrictive ici.
   * La table commence en ligne 0 et colonne 0 (A1).
   * La première ligne (ligne 0) contient les noms de colonne.
   * La première colonne qui est vide marque la limite de la table au niveau des colonnes
   * La première ligne qui est vide marque la limite de la table au niveau des colonnes
   * Les valeurs sont lues à l'aide de {@link #getCellValueAsString(Cell)} si df et nf sont tous
   * deux null, sinon les valeurs sont lues à l'aide de {@link #getCellValueAsString(Cell, DateFormat, NumberFormat)}.
   * Les noms de colonnes sont passés à trim(), les valeurs sont passés à {@link StringUtils#endTrim(String)} avant
   * d'être enregistrés.
   * @param sh L'onglet à convertir en table
   * @param df Format de date/heure java 
   * @param nf Format de nombre java
   * @return un objet {@link StringTable}
   */
  public StringTable sheetToStringTable(Sheet sh, DateFormat df, NumberFormat nf) {
    ArrayList<String> columnNames = new ArrayList<>();
    ArrayList<String> columnValues = new ArrayList<>();
    StringTable stbl = new StringTable();
    int rowNum = 0;
    int ncols = 0;
    boolean rowEmpty = false;
    Row curRow = sh.getRow(rowNum);
    while (!rowEmpty) {
      rowEmpty = true;
      if (rowNum == 0) {
        //lecture titres
        int colNum = 0;
        String colName = getCellValueAsString(curRow.getCell(colNum)).trim();
        while (!isEmpty(colName)) {
          columnNames.add(colName);
          rowEmpty = false;
          colNum++;
          colName = getCellValueAsString(curRow.getCell(colNum)).trim();
        }
        stbl.addTitlesRow(columnNames);
        ncols = colNum;
      }
      else {
        columnValues.clear();
        for (int ci = 0; ci < ncols; ci++) {
          String val = null;
          if (curRow != null) {
            if (df == null && nf == null) val = StringUtils.endTrim(getCellValueAsString(curRow.getCell(ci)));
            else val = StringUtils.endTrim(getCellValueAsString(curRow.getCell(ci), df, nf));            
          }
          if (!isEmpty(val)) rowEmpty = false; 
          columnValues.add(val);
        }
        if (!rowEmpty) stbl.addRow(columnValues);
      }
      rowNum++;
      curRow = sh.getRow(rowNum);
    }
    return stbl;
  }
}
