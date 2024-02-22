package fr.karadimas.gpmsi.poi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import fr.karadimas.pmsixml.FszField;

/**
 * Classe utilitaire pour aider à la création de fichiers Excel au format xlsx (Excel 2007 et ultérieurs).
 * Cette classe utilise la librairie apache POI qui est puissante mais délicate à manier.
 * J'ai pris exemple sur du code utilisé dans Squirrel SQL
 * (squirrel-sql-git\sql12\core\src\net\sourceforge\squirrel_sql\fw\gui\action\fileexport\DataExportExcelWriter.java)
 * Exemple simple d'utilisation :
 * <pre>
 *     XlsxHelper classeur = new XlsxHelper("Classeur test");
 *     classeur.addCell("Nom");
 *     classeur.addCell("Date de naissance");
 *     classeur.newRow();
 *     classeur.addCell("OBAMA");
 *     Calendar ddn = Calendar.getInstance();
 *     ddn.set(1961, 07, 04); //4 aout 1961
 *     classeur.addCell(ddn); //s'affichera en 1961-08-04
 *     classeur.newRow();
 *     File destFile = new File("MonExcel.xlsx");
 *     classeur.setOutput(destFile);
 *     classeur.writeFileAndClose();
 * </pre>
 * Pour un exemple détaillé d'utilisation, se référer au test
 * XlsxHelperTests#testXlsxHelper()
 * 
 * @author hkaradimas
 *
 */
public class XlsxHelper {

  private SXSSFWorkbook _workbook;  //Le modèle du classeur XML xlsx qui n'est que partiellement gardé en mémoire
  private SXSSFSheet _sheet;
  private FileOutputStream fos;
  private HashMap<String, CellStyle> formatCache = new HashMap<String, CellStyle>();
  private int curRow = 0;
  private int curCol = 0;
  
  private Pattern escapedUtfPattern = Pattern.compile("_x[\\da-fA-F]{4}_");
  
  /**
   * Ajouter un commentaire de cellule
   * @param cell La cellule
   * @param commentText Le texte de commentaire à ajouter
   * @param author L'auteur du commentaire
   */
  public static void setComment(SXSSFCell cell, String commentText, String author) {
    Sheet sh = cell.getRow().getSheet();
    Workbook wb = sh.getWorkbook();
    int cur_row = cell.getRowIndex();
    int cur_col = cell.getColumnIndex();
    CreationHelper factory = wb.getCreationHelper();

    ClientAnchor anchor = factory.createClientAnchor();
    //On met la boite en bas à droite
    anchor.setCol1(cur_col); 
    anchor.setCol2(cur_col + 5); 
    anchor.setRow1(cur_row); 
    anchor.setRow2(cur_row + 8); 

    Drawing<?> drawing = sh.createDrawingPatriarch();
    Comment comment = drawing.createCellComment(anchor);
    //Mettre le texte du commentaire, et l'auteur
    comment.setString(factory.createRichTextString(commentText));
    comment.setAuthor(author);

    cell.setCellComment(comment);  
  }
  
  /**
   * Crée un nouveau classeur Excel Xlsx en mémoire / sur disque, avec un seul onglet
   * @param sheetName Nom de l'onglet
   */
  public XlsxHelper(String sheetName) {
    _workbook = new SXSSFWorkbook(1000);
    _sheet = _workbook.createSheet(sheetName);
  }

  /**
   * D'après la doc Microsoft des caractères d'échappement sont utilisés pour les
   * caractères interdits dans XML.
   * https://en.wikipedia.org/wiki/Valid_characters_in_XML
   * En pratique dans POI si on n'échappe pas ça donne des caractères invalides.
   * @param c
   * @return true si le caractère est un caractère valide pour xml 1.0
   */
  private static final boolean isValidMs(char c) {
    if (c < 0x0020) return c == 0x0009 || c == 0x000A || c == 0x000D;
    //U+0020–U+D7FF, U+E000–U+FFFD: this excludes some (not all) non-characters in the BMP (all surrogates, U+FFFE and U+FFFF are forbidden);
    if (c <= 0x00FF) return true;
    if (c >= 0xE000 && c <= 0xFFFD) return true;
    //U+10000–U+10FFFF: this includes all code points in supplementary planes, including non-characters.
    //java char is only 16 bit, we don't support U+10000 and above here.
    return false;
  }
  /**
   * Microsoft stocke les caractères unicode de manière spéciale.
   *
   * https://stackoverflow.com/questions/48222502/xssfcell-in-apache-poi-encodes-certain-character-sequences-as-unicode-character
   *
   * https://learn.microsoft.com/en-us/dotnet/api/documentformat.openxml.varianttypes.vtbstring?view=openxml-2.8.1&amp;redirectedfrom=MSDN
   *
   * Malheureusement apache poi ne fait pas cette conversion ce qui fait
   * que les caractères accentués ne sont pas bien stockés si on ne fait pas cette
   * conversion avant.
   * Cette fonction fait la conversion.
   * Attention, il y a un traitement spécial avec _x005F_ qui est fait aussi ici (cf. liens ci-dessus).
   * @param rawString La chaîne de caractères non traitée
   * @return une string avec les caractères unicode bien traduits de façon "microsoftienne".
   */
  public String msUtfEncode(String rawString) {
    //Faire l'échappement des séquences qui peuvent représenter un échappement.
    String escaped1 = escapedUtfPattern.matcher(rawString).replaceAll("_x005F$0");
    //maintenant faire l'échappement des caractères "interdits" dans XML 1.0.
    StringBuilder sb = new StringBuilder();
    int len = escaped1.length();
    for (int i = 0; i < len; i++) {
      char c = escaped1.charAt(i);
      if (isValidMs(c)) sb.append(c);
      else {
        sb.append('_');
        sb.append('x');
        String.format("%04X", (int)c);
        sb.append('_');
      }
    }
    return escaped1;
  }
  
  /**
   * Donner le flux de destination des données xlsx.
   * @param pFos Le flux de destination
   */
  public void setOutput(FileOutputStream pFos) { fos = pFos; }
  
  /**
   * Donner le fichier excel de destination. Appelle {@link #setOutput(FileOutputStream)} pour
   * créer le flux de destination.
   * @param outFile le fichier de destination
   * @throws FileNotFoundException Si le fichier n'existe pas
   */
  public void setOutput(File outFile)
      throws FileNotFoundException 
  { 
    setOutput(new FileOutputStream(outFile)); 
  }
  
  /**
   * Renvoie l'onglet interne utilisé, pour manipulation directe si besoin.
   * Sheet donne accès à Workbook (getWorkbook()).
   * @return L'onglet utilisé dans cet objet
   */
  public Sheet getSheet() { return _sheet; }
  
  /**
   * Mettre une cellule texte dans Excel (si null, sera remplacée par "").
   * Une transformation via {@link #msUtfEncode(String)} est faite pour traiter les caractères
   * unicode de façon appropriée.
   * @param value La valeur
   * @param rowNr le numéro de rangée (commence à 0)
   * @param colNr le numéro de colonne (commence à 0)
   * @return La cellule qui a été créée
   */
  public SXSSFCell setCell(String value, int rowNr, int colNr) {
    SXSSFRow row = getOrMakeRow(rowNr);
    SXSSFCell cell = row.createCell(colNr);
    String v = value == null ? "" : value;
    v = msUtfEncode(v); //remplacer v par sa valeur transformée avec des séquences d'échappement pour les caractères non ASCII
    cell.setCellValue(v); //setCellValue surchargé pour : String boolean Calendar Date Double RichTextString
    return cell;
  }
  
  /**
   * Mettre une cellule booleenne dans Excel
   * @param value La valeur
   * @param rowNr le numéro de rangée (commence à 0)
   * @param colNr le numéro de colonne (commence à 0)
   * @return La cellule qui a été créée
   */
  public SXSSFCell setCell(boolean value, int rowNr, int colNr) {
    SXSSFRow row = getOrMakeRow(rowNr);
    SXSSFCell cell = row.createCell(colNr);
    cell.setCellValue(value); //setCellValue surchargé pour : String boolean Calendar Date Double RichTextString
    return cell;
  }
  
  /**
   * Mettre une cellule de date formatée dans Excel, à partir d'un objet Calendar.
   * @param value Le calendrier qui contient la date
   * @param excelFormat Exemples "dd/mm/yyyy" (format à préférer pour la France), "m/d/yy", "m/d/yy h:mm", "h:mm", cf. fonction TEXT (version anglaise) de Excel, <a href="https://support.microsoft.com/en-us/office/text-function-20d5ac4d-7b94-49fd-bb38-93d29371225c">TEXT function</a>
   * @param rowNr le numéro de rangée (commence à 0)
   * @param colNr le numéro de colonne (commence à 0)
   * @return La cellule qui a été créée
   */
  public SXSSFCell setCell(Calendar value, String excelFormat, int rowNr, int colNr) {
    SXSSFRow row = getOrMakeRow(rowNr);
    SXSSFCell cell = row.createCell(colNr);
    if (value == null) {
      cell.setCellValue("");
    }
    else {
      cell.setCellValue(value); //setCellValue surchargé pour : String boolean Calendar Date Double RichTextString 
      adjustCellStyle(cell, excelFormat);
    }
    return cell;
  }
  
  /**
   * Mettre une cellule de date dans Excel, au format ISO
   * @param value La date (peut être null), le format utilisé sera "yyyy-mm-dd" si date non null
   * @param rowNr le numéro de rangée (commence à 0)
   * @param colNr le numéro de colonne (commence à 0)
   * @return La cellule qui a été créée
   */
  public SXSSFCell setCell(Calendar value, int rowNr, int colNr) { 
    return setCell(value, "yyyy-mm-dd", rowNr, colNr);
  }
  
  /**
   * Mettre une cellule de date dans Excel, au format donné.
   * @param value La date ou null
   * @param excelFormat Exemples "dd/mm/yyyy" (format à préférer pour la France), "m/d/yy", "m/d/yy h:mm", "h:mm", cf. fonction TEXT (version anglaise) de Excel, <a href="https://support.microsoft.com/en-us/office/text-function-20d5ac4d-7b94-49fd-bb38-93d29371225c">TEXT function</a>
   * @param rowNr le numéro de rangée (commence à 0)
   * @param colNr le numéro de colonne (commence à 0)
   * @return La cellule qui a été créée
   */
  public SXSSFCell setCell(Date value, String excelFormat, int rowNr, int colNr) {
    SXSSFRow row = getOrMakeRow(rowNr);
    SXSSFCell cell = row.createCell(colNr);
    if (value == null) {
      cell.setCellValue("");
    }
    else {
      cell.setCellValue(value); //setCellValue surchargé pour : String boolean Calendar Date Double RichTextString
      adjustCellStyle(cell, excelFormat);      
    }
    return cell;
  }
  
  /**
   * Mettre une cellule de date dans Excel, au format ISO
   * @param value La date, le format utilisé sera "yyyy-mm-dd"
   * @param rowNr le numéro de rangée (commence à 0)
   * @param colNr le numéro de colonne (commence à 0)
   * @return La cellule qui a été créée
   */
  public SXSSFCell setCell(Date value, int rowNr, int colNr) { 
    return setCell(value, "yyyy-mm-dd", rowNr, colNr);
  }
  
  /**
   * Mettre une cellule de nombre dans Excel, avec le format donné
   * @param value La valeur numérique
   * @param format Le format , non utilisé si null
   * @param rowNr le numéro de rangée (commence à 0)
   * @param colNr le numéro de colonne (commence à 0)
   * @return La cellule qui a été créée
   */
  public SXSSFCell setCell(double value, String format, int rowNr, int colNr) {
    SXSSFRow row = getOrMakeRow(rowNr);
    SXSSFCell cell = row.createCell(colNr);
    cell.setCellValue(value); //setCellValue surchargé pour : String boolean Calendar Date Double RichTextString
    if (format != null) adjustCellStyle(cell, format);
    return cell;
  }
  
  /**
   * Mettre un champ PMSI en utilisant le type préféré du champ et le format de nombre pour avoir le nombre avec la bonne échelle.
   * Exemples :
   * <ul>
   * <li>000012500 (avec un format 6+3) -&gt; 12.5 en tant que nombre (double)
   * <li>31122001 (au format date) -&gt; 31/12/2001 en tant que date
   * </ul>
   * @param fld Le champ PMSI
   * @param rowNr Le numéro de rangée
   * @param colNr Le numéro de colonne
   * @return La cellule qui a été créée
   */
  public SXSSFCell setCell(FszField fld, int rowNr, int colNr) {
    SXSSFRow row = getOrMakeRow(rowNr);
    SXSSFCell cell = row.createCell(colNr);
    if (fld.representsEuropeanDate()) {
      try {
        cell.setCellValue(fld.getValueAsEuropeanDate());
        adjustCellStyle(cell, "dd/mm/yyyy");
      }
      catch (ParseException pex) {
        //si la date est invalide, mettre le texte brut à la place
        cell.setCellValue(fld.getValueAsText());
      }
    }
    else if (fld.representsNumber()) {
      BigDecimal cv = fld.getCorrectedValue();
      if (cv == null) cell.setCellValue("");
      else cell.setCellValue(cv.doubleValue());
    }
    else cell.setCellValue(fld.getValueAsText());
    return cell;
  }
  
  /**
   * Mettre une cellule de nombre dans Excel, le format par défaut
   * @param value La valeur numérique
   * @param rowNr le numéro de rangée (commence à 0)
   * @param colNr le numéro de colonne (commence à 0)
   * @return La cellule qui a été créée
   */
  public SXSSFCell setCell(double value, int rowNr, int colNr) {
    return setCell(value, null, rowNr, colNr);
  }
  
  /**
   * Mettre une cellule de texte "riche" dans Excel.
   * Cf. <a href="https://poi.apache.org/apidocs/dev/org/apache/poi/xssf/usermodel/XSSFRichTextString.html">XSSFRichTextString</a>
   * TODO voir comment se comportent les caractères Unicode. 
   * @param value La valeur numérique
   * @param rowNr le numéro de rangée (commence à 0)
   * @param colNr le numéro de colonne (commence à 0)
   * @return La cellule qui a été créée
   */
  public SXSSFCell setCell(XSSFRichTextString value, int rowNr, int colNr) {
    SXSSFRow row = getOrMakeRow(rowNr);
    SXSSFCell cell = row.createCell(colNr);
    if (value == null) {
      cell.setCellValue("");
    }
    else {
      cell.setCellValue(value); //setCellValue surchargé pour : String boolean Calendar Date Double RichTextString
    }
    return cell;
  }

  /**
   * Mettre une cellule avec une valeur contenue dans le ValueWrapper.
   * @param vw
   * @param rowNr
   * @param colNr
   * @return
   */
  public SXSSFCell setCell(ValueWrapper vw, int rowNr, int colNr) {
    SXSSFRow row = getOrMakeRow(rowNr);
    SXSSFCell cell = row.createCell(colNr);
    if (vw == null) {
      cell.setCellValue("");
    }
    else {
      vw.setValueInCell(cell, formatCache);
    }
    return cell;
    
  }
  
  /** Passer à la rangée suivante */
  public void newRow() { curRow++; curCol = 0; }
  
  /**
   * Ajouter une cellule de type String
   * @param value La valeur de String (si null, sera remplacée par "")
   * @return La cellule créée (on peut ajuster le style si besoin)
   */
  public SXSSFCell addCell(String value) { return setCell(value, curRow, curCol++); }
  
  /**
   * Ajouter une cellle de type Booléen
   * @param value Le booléen à ajouter
   * @return La cellule créée (on peut ajuster le style si besoin)
   */
  public SXSSFCell addCell(boolean value) { return setCell(value, curRow, curCol++); }

  /**
   * Ajouter une cellule de type Date
   * @param value Le calendrier qui donne la date à date à ajouter
   * @param format Le format (cf formats Excel) à utiliser
   * @return La cellule créée (on peut ajuster le style si besoin)
   */
  public SXSSFCell addCell(Calendar value, String format) { return setCell(value, format, curRow, curCol++); }
  
  /**
   * Ajouter une cellule de type Date, au format iso "yyyy-mm-dd"
   * @param value Le calendrier qui donne la date à date à ajouter
   * @return La cellule créée (on peut ajuster le style si besoin)
   */
  public SXSSFCell addCell(Calendar value) { return setCell(value, curRow, curCol++); }
  
  /**
   * Ajouter une cellule de type Date
   * @param value La date à ajouter
   * @param format Le format (cf formats Excel) à utiliser
   * @return La cellule créée (on peut ajuster le style si besoin)
   */
  public SXSSFCell addCell(Date value, String format) { return setCell(value, format, curRow, curCol++); }
  
  /**
   * Ajouter une cellule de type Date, au format iso "yyyy-mm-dd"
   * @param value La date à ajouter
   * @return La cellule créée (on peut ajuster le style si besoin)
   */
  public SXSSFCell addCell(Date value) { return setCell(value, curRow, curCol++); }
  
  /**
   * Ajouter une cellule de type nombre
   * @param value Le nombre
   * @param format Le format (cf formats Excel) à utiliser
   * @return La cellule créée (on peut ajuster le style si besoin)
   */
  public SXSSFCell addCell(double value, String format) { return setCell(value, format, curRow, curCol++); }
  
  /**
   * Ajouter une cellule de type nombre, sans format particulier
   * @param value Le nombre
   * @return La cellule créée (on peut ajuster le style si besoin)
   */
  public SXSSFCell addCell(double value) { return setCell(value, curRow, curCol++); }
  
  /**
   * Ajouter une cellule de type texte riche (cf. docs apache)
   * @param value Le text
   * @return La cellule créée (on peut ajuster le style si besoin)
   */
  public SXSSFCell addCell(XSSFRichTextString value) { return setCell(value, curRow, curCol++); }
  
  /**
   * Ajouter une cellule en fonction du contenu du champ PMSI
   * @param fld Le champ
   * @return La cellule créée (le style a été créé à l'aide du type du champ)
   */
  public SXSSFCell addCell(FszField fld) { return setCell(fld, curRow, curCol++); }
  
  /**
   * Ajouter une cellule avec la valeur du ValueWrapper
   * @param vw un ValueWrapper qui est une représentation fidèle du contenu d'une cellule
   * @return la nouvelle cellule créée
   */
  public SXSSFCell addCell(ValueWrapper vw) { return setCell(vw, curRow, curCol++); }
  
  /**
   * Ajuste le format de la cellule a l'aide du texte de format fourni.
   * Crée si besoin le style et le place dans le cache des styles.
   * @param cell La cellule dont le style doit être renseigné
   * @param format Le texte qui représente le format, exemples "m/d/yy", "m/d/yy h:mm", "h:mm"
   */
  public void adjustCellStyle(SXSSFCell cell, String format) {
    CreationHelper creationHelper = _workbook.getCreationHelper();
    CellStyle cellStyle = formatCache.get(format);
    if (cellStyle == null) {
      cellStyle = _workbook.createCellStyle();
      cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
      formatCache.put(format, cellStyle);
    }
    cell.setCellStyle(cellStyle);
  }
  
  /**
   * Créer ou retourner une rangée
   * @param rowNr Le numéro de rangée (commence à 0)
   * @return La rangée
   */
  private SXSSFRow getOrMakeRow(int rowNr) {
    SXSSFRow row = _sheet.getRow(rowNr);
    if (row == null) { row = _sheet.createRow(rowNr); }
    return row;
  }
  
  /**
   * Finalise l'envoi. Après cet appel, l'objet XlsxHelper ne peut plus être utilisé.
   * @throws IOException si erreur entrée/sortie
   */
  public void writeFileAndClose()
      throws IOException 
  {
    _workbook.write(fos);
    fos.close();
    _workbook.dispose(); //cet appel est important, car il efface les fichiers temporaires qui ont été créés sur le disque
    _workbook = null; //rendre _workbook inutilisable
    _sheet = null; //rendre _sheet inutilisable
  }
  
  /**
   * Ajouter des rangées depuis le ResultSet
   * @param rs Le ResultSet qui donne les rangées
   * @param fp Les préférences de format à utiliser
   * @throws SQLException -
   */
  public void addFrom(ResultSet rs, XlsxFormatPreferences fp)
      throws SQLException 
  {
    ResultSetMetaData md = rs.getMetaData();
    int cc = md.getColumnCount();
    String[] titles = new String[cc];
    int[] types = new int[cc]; 
    for (int i = 1; i <= cc; i++) {
      String colLabel = md.getColumnLabel(i);
      titles[i-1] = colLabel;
      types[i-1] = md.getColumnType(i);
    }
    for (String title : titles) addCell(title);
    newRow();
    while (rs.next()) {
      for (int i = 1; i <= cc; i++) {
        addCellFromDatabase(rs.getObject(i), types[i-1], fp);
      }
      newRow();
    }//while
  }
  
  /**
   * Ajouter des rangées depuis un ResultSet jdbc
   * @param rs Le {@link ResultSet} à partir duquel ajouter
   * @throws SQLException -
   */
  public void addFrom(ResultSet rs)
      throws SQLException 
  {
    addFrom(rs, XlsxFormatPreferences.defaultPreferences);
  }
  
  private void addCellFromDatabase(Object obj, int sqlType, XlsxFormatPreferences fp) {
    switch (sqlType) {
    case Types.ARRAY: addCell(obj.toString());
    case Types.BINARY: addCell(obj.toString());
    case Types.BIT: addCell(obj.toString());
    case Types.BLOB: addCell(obj.toString());
    case Types.BOOLEAN: addCell(obj.toString());
    case Types.CHAR: addCell(obj.toString());
    case Types.CLOB: addCell(obj.toString());
    case Types.DATALINK: addCell(obj.toString());
    case Types.DISTINCT: addCell(obj.toString());
    case Types.JAVA_OBJECT: addCell(obj.toString());
    case Types.LONGNVARCHAR: addCell(obj.toString());
    case Types.LONGVARBINARY: addCell(obj.toString());
    case Types.LONGVARCHAR: addCell(obj.toString());
    case Types.NCHAR: addCell(obj.toString());
    case Types.NCLOB: addCell(obj.toString());
    case Types.NULL: addCell(obj.toString()); //ne devrait pas arriver
    case Types.NVARCHAR: addCell(obj.toString());
    case Types.OTHER: addCell(obj.toString());
    case Types.REF: addCell(obj.toString());
    case Types.REF_CURSOR: addCell(obj.toString());
    case Types.ROWID: addCell(obj.toString());
    case Types.SQLXML: addCell(obj.toString());
    case Types.STRUCT: addCell(obj.toString());
    
    case Types.DATE:
      if (fp.dateFormat == null) addCell((Date)obj);
      else addCell((Date)obj, fp.dateFormat);
      return;
    
    case Types.TIME: //fall through 
    case Types.TIME_WITH_TIMEZONE: 
      if (fp.timeFormat == null) addCell((Date)obj);
      else addCell((Date)obj, fp.timeFormat);
      return;
    
    case Types.TIMESTAMP: //fall through 
    case Types.TIMESTAMP_WITH_TIMEZONE: 
      if (fp.dateTimeFormat == null) addCell((Date)obj);
      else addCell((Date)obj, fp.dateTimeFormat);
      return;
          
    case Types.REAL: //fall through
    case Types.SMALLINT: //fall through
    case Types.DECIMAL: //fall through
    case Types.BIGINT: //fall through
    case Types.NUMERIC: //fall through
    case Types.DOUBLE: //fall through
    case Types.FLOAT: //fall through
    case Types.INTEGER: //fall through
    case Types.TINYINT: 
      addCell(Double.valueOf(String.valueOf(obj)));
      return;
      
    case Types.VARBINARY: //fall through
    case Types.VARCHAR:
      addCell(obj.toString());
      return;
    default: addCell(obj.toString()); //par défaut convertir via "toString"
    }//switch
  }//addCellFromDatabase
    
}
