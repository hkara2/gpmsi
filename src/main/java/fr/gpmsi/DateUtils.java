package fr.gpmsi;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Methodes utilitaires de dates.
 * Attention aux types, il y a des {@link Date} (ancien type) et des {@link LocalDate} (nouveau type).
 * @author hkaradimas
 *
 */
public class DateUtils {

//Quelques liens vers des calculs ISO pour les semaines
//a tester
// public static int getWeekOfYear(long packedDateTime) {
// LocalDateTime date = asLocalDateTime(packedDateTime);
// TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
// return date.get(woy);
//}

//int week = myZonedDateTime.get( IsoFields.WEEK_OF_WEEK_BASED_YEAR ) ;
//int weekBasedYear = myZonedDateTime.get( IsoFields.WEEK_BASED_YEAR ) ;

//https://stackoverflow.com/questions/34722997/java-calendar-week-of-year-not-iso-8601compliant

//https://www.threeten.org/threeten-extra/


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
  
  /**
   * Retourner la date du premier lundi de l'année ISO donnée (utile pour le début de l'année SSR/SMR)
   * @param isoYear l'année
   * @return la date du premier lundi de l'année ISO
   */
  public static LocalDate getIsoWeekStartDate(int isoYear) {
      LocalDate startDate;
      LocalDate jan1 = LocalDate.of(isoYear, 1, 1);
      int jan1day = jan1.getDayOfWeek().getValue();
      //1:0
      //2:-1
      //3:-2
      //4:-3
      //5:+3
      //6:+2
      //7:+1
      if (jan1day < 5) startDate = jan1.minusDays(jan1day - 1);
      else startDate = jan1.plusDays(8 - jan1day);
      return startDate;
  }
  
  /**
   * Retourner la date du dernier dimanche du mois donné.
   * La semaine appartient au mois si le jeudi est dans le mois.
   * @param isoYear l'année
   * @param month le mois (1 à 12)
   * @return date du dimanche de la dernière semaine du mois
   */
  public static LocalDate getIsoWeekEndDate(int isoYear, int month) {
    LocalDate em = YearMonth.of(isoYear, month).atEndOfMonth();
    int lastDom = em.getDayOfWeek().getValue();
    LocalDate endDate;
    //1:-1
    //2:-2
    //3:-3
    //4:+3
    //5:+2
    //6:+1
    //7:0
    if (lastDom < 4) endDate = em.minusDays(lastDom);
    else endDate = em.plusDays(7 - lastDom);
    return endDate;
  }

}
