package fr.gpmsi.poi;

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
  /** Le format numérique (null par défaut) */
  public String numberFormat = null;
  /** Le format de date (par défaut "dd/mm/yyyy") */
  public String dateFormat = "dd/mm/yyyy";
  /** Le format de temps (par défaut "hh:mm:ss") */
  public String timeFormat = "hh:mm:ss";
  /** Le format date+temps (par défaut "dd/mm/yyyy hh:mm:ss") */
  public String dateTimeFormat = "dd/mm/yyyy hh:mm:ss";

  /**
   * Préférences par défaut
   */
  public static final XlsxFormatPreferences defaultPreferences = new XlsxFormatPreferences();
  
  /** constructeur par défaut (ne rajoute rien par rapport aux valeurs par défaut) */
  public XlsxFormatPreferences() {
  }

}
