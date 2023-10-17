package fr.karadimas.gpmsi;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Methodes utilitaires de dates.
 * @author hkaradimas
 *
 */
public class DateUtils {

  /**
   * Constructeur par défaut
   */
  public DateUtils() {
  }
  
  private static SimpleDateFormat fdf = new SimpleDateFormat("dd/MM/yyyy");
  
  private static DateTimeFormatter lfdf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  /**
   * Utilitaire pour formater une date au format francais, ex : 31/12/2019
   * @param d la date (peut etre null)
   * @return la date formatée ou une chaîne vide si d est null
   */
  public static String formatAsFrenchDate(Date d) {
    if (d == null) return "";
    synchronized (fdf) { return fdf.format(d); }
  }
  
  /**
   * Utilitaire pour formater une localdate au format francais, ex : 31/12/2019
   * @param d la localdate (peut etre null)
   * @return la date formatée ou une chaîne vide si d est null
   */
  public static String formatAsFrenchDate(LocalDate d) {
    if (d == null) return ""; else return d.format(lfdf);
  }
  
  /**
   * Transformer une LocalDate en Date, en utilisant la time Zone par défaut du système.
   * Utilise la formule <code>Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant())</code>
   * @param ld la localdate (ne doit pas etre null)
   * @return la date
   */
  public static Date toDate(LocalDate ld) {
    return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  /**
   * Transformer une Date en LocalDate, en utilisant la time Zone par défaut du système.
   * Utilise la formule <code>d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()</code>
   * @param d la date (ne doit pas etre null)
   * @return la localdate
   */
  public static LocalDate toLocalDate(Date d) {
    return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }
  
  /**
   * Calcule l'age entre la date de naissance et la date presente (inclue)
   * @param birthDate La date de naissance
   * @param presentDate La date presente
   * @return Un objet Period que l'on peut utiliser avec getMonths() et getYears()
   */
  public static Period calcAge(Date birthDate, Date presentDate) {
    if (birthDate == null) return null;
    if (presentDate == null) return null;
    LocalDate birthLocalDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate presentLocalDate = presentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    return Period.between(birthLocalDate, presentLocalDate);
  }
  
}
