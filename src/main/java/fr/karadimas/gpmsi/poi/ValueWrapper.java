package fr.karadimas.gpmsi.poi;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * Encapsulation d'une valeur de cellule, pour pouvoir la transporter vers une autre cellule.
 * Couvre 99% de mes besoins courants.
 * @author hkaradimas
 */
public class ValueWrapper {

  private Object val;
  private CellType typ;
  private boolean formattedLikeADate;
  private LocalDateTime locDate;
  
  public ValueWrapper() {
  }

  public void setValueFromCell(Cell c, boolean keepFormulas) {
    if (c == null) {
      val = ""; typ = CellType.BLANK;
    }
    else {
      typ = c.getCellType();
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
        if (keepFormulas) val = c.getCellFormula();
        else val = c.getCachedFormulaResultType();
        break;
      case NUMERIC:
        val = c.getNumericCellValue();
        formattedLikeADate = DateUtil.isCellDateFormatted(c);
        if (formattedLikeADate) {
          locDate = c.getLocalDateTimeCellValue();
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
   * La valeur est-elle formattée comme une date ? (correspond à la valeur de retour de {@link DateUtil#isCellDateFormatted(Cell)})
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
  
}
