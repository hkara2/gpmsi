package fr.karadimas.gpmsi.poi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFCell;

/**
 * Encapsulation d'une valeur de cellule, pour pouvoir la transporter vers une autre cellule.
 * Contient le format de date utilisé de manière à pouvoir le transporter avec la valeur et à ainsi
 * le recopier également.
 * Couvre 99% de mes besoins courants (ce n'est pas grave si les styles ne sont pas copiés).
 * Pour l'instant j'ai encore des erreurs avec {@link CellUtil#copyCell(Cell, Cell, org.apache.poi.ss.usermodel.CellCopyPolicy, org.apache.poi.ss.usermodel.CellCopyContext)}
 * qui est en bêta. Lorsque cela fonctionnera, il n'y aura plus besoin de cette classe. 
 * @author hkaradimas
 */
public class ValueWrapper {

  private Object val;
  private CellType typ;
  private boolean formattedLikeADate;
  private LocalDateTime locDate;
  private String dataFormatString;
  
  /**
   * Constructeur vide, la valeur est une cellule de type BLANK.
   */
  public ValueWrapper() {
    val = ""; typ = CellType.BLANK;
  }
  
  /**
   * Constructeur qui prend une cellule pour mettre la valeur.
   * Appelle setValueFromCell(c, false)
   * @param c la cellule à utiliser.
   */
  public ValueWrapper(Cell c) {
    setValueFromCell(c, false);
  }
  
  /**
   * Constructeur qui prend une cellule pour mettre la valeur, et
   * contrôle si on doit garder ou non les formules (au lieu du résultat de leur exécution).
   * @param c la cellule à utiliser
   * @param keepFormulas la valeur (qui sera passée à {@link #setValueFromCell(Cell, boolean)})
   */
  public ValueWrapper(Cell c, boolean keepFormulas) {
    setValueFromCell(c, keepFormulas);
  }

  /**
   * Mettre la valeur en fonction de ce qu'il y a dans la cellule.
   * @param c La cellule. Si null, la valeur sera une String vide et un type BLANK
   * @param keepFormulas si true, la valeur sera une String avec la formule et le type FORMULA, 
   *        sinon la valeur sera la dernière valeur calculée et le type le dernier type mis en cache.
   */
  public void setValueFromCell(Cell c, boolean keepFormulas) {
    if (c == null) {
      val = ""; typ = CellType.BLANK;
    }
    else {
      typ = c.getCellType();
      //remplacer une formule par son résultat (sauf si on garde les formules telles quelles)
      if (typ == CellType.FORMULA && !keepFormulas) typ = c.getCachedFormulaResultType();
      switch (typ) {
      case _NONE:
      case BLANK:
        val = "";
        break;
      case BOOLEAN:
        val = c.getBooleanCellValue();
        break;
      case ERROR:
        val = c.getErrorCellValue();
        break;
      case FORMULA:
        val = c.getCellFormula();
        break;
      case NUMERIC:
        val = c.getNumericCellValue();
        formattedLikeADate = DateUtil.isCellDateFormatted(c);
        if (formattedLikeADate) {
          locDate = c.getLocalDateTimeCellValue();
          dataFormatString = c.getCellStyle().getDataFormatString();
        }
        break;
      case STRING:
        val = c.getStringCellValue();
        break;
      default:
        System.err.println("Type de cellule non pris en charge (la cellule destination sera vide) : " + typ);
        val = ""; typ = CellType.BLANK;
      }
      
    }
  }

  /**
   * Mettre la valeur de la cellule a ce qui correspond ici
   * @param c la cellule dans laquelle on veut écrire la valeur.
   * @param formatCache une Map qui contient les formats déjà créés pour éviter de recréer les formats plusieurs fois.
   */
  public void setValueInCell(Cell c, Map<String, CellStyle> formatCache) {
    if (c == null) return;
    switch (typ) {
    case _NONE:
    case BLANK:
      c.setBlank();
      break;
    case BOOLEAN:
      c.setCellValue((boolean)val);
      break;
    case ERROR:
      c.setCellErrorValue((byte)val);
      break;
    case FORMULA:
      c.setCellFormula((String)val);
      break;
    case NUMERIC:
      c.setCellValue((double)val);
      if (formattedLikeADate) {
        //essayer de remettre le même format de date que celui qui a été enregistré
        Workbook wb = c.getSheet().getWorkbook(); 
        if (c instanceof SXSSFCell) {
          PoiHelper.adjustCellStyle(wb, formatCache, (SXSSFCell)c, dataFormatString);
        }
      }
      break;
    case STRING:
      c.setCellValue((String)val);
      break;
    default:
      System.err.println("Type de valeur non supporte : " + typ);
      c.setCellValue(String.valueOf(val));
    }
  }
  
  /**
   * La valeur est-elle formattée comme une date ? (correspond à la valeur de retour de {@link DateUtil#isCellDateFormatted(Cell)})
   * si c'est le cas, le formatage de la date sera également dans dataFormatString.
   * @return true is la valeur semble formatée en tant que date
   */
  public boolean isFormattedLikeADate() {
    return formattedLikeADate;
  }
  
  /** 
   * Retourne la valeur en tant que LocalDateTime.
   * Attention ne marche que si getCellType() est {@link CellType#NUMERIC}, et {@link #isFormattedLikeADate()} est true
   * La valeur numérique en tant que Double est également disponible via {@link #getValue()}
   * @return la valeur en tant que {@link LocalDateTime} ou null is {@link #isFormattedLikeADate()} est false
   */
  public LocalDateTime getLocalDateTime() {
    return locDate;
  }
  
  public CellType getCellType() { return typ; }
  
  public Object getValue() { return val; }

  public String getDataFormatString() {
    return dataFormatString;
  }
  
}
