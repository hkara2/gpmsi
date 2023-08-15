package fr.karadimas.gpmsi.poi;

/**
 * Objet simple pour stocker les préférences pour chaque type.
 * Si null, laisser le format par défaut pour Excel.
 * Par défaut laisse le format par défaut de Excel pour les nombres, mais pour les dates
 * met <code>"dd/mm/yyyy"</code>, pour les heures met <code>"hh:mm:ss"</code> (attention ces spécificateurs sont pour Excel
 * par pour java !).
 * <br>
 * Utilisé dans {@link XlsxHelper}
 * 
 * @author hkaradimas
 *
 */
public class XlsxFormatPreferences {
  public String numberFormat = null;
  public String dateFormat = "dd/mm/yyyy";
  public String timeFormat = "hh:mm:ss";
  public String dateTimeFormat = "dd/mm/yyyy hh:mm:ss";

  public static final XlsxFormatPreferences defaultPreferences = new XlsxFormatPreferences();
  
  public XlsxFormatPreferences() {
  }

}
