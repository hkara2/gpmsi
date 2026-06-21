package fr.gpmsi;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;

/**
 * Descriptions des durées en français
 * @author hkaradimas
 *
 */
public class Durees {
  
  static private HashMap<ChronoUnit, String> frenchNames = null;

  static {
    frenchNames = new HashMap<>();
    frenchNames.put(ChronoUnit.NANOS, "nanoseconde");
    frenchNames.put(ChronoUnit.MILLIS, "milliseconde");
    frenchNames.put(ChronoUnit.SECONDS, "seconde");
    frenchNames.put(ChronoUnit.MINUTES, "minute");
    frenchNames.put(ChronoUnit.HOURS, "heure");
    frenchNames.put(ChronoUnit.DAYS, "jour");
    frenchNames.put(ChronoUnit.WEEKS, "semaine");
  }
  
  /** Constructeur privé, il n'y a que des méthodes statiques */
  private Durees() {}

  /**
   * Retourner un nom en français pour l'unité
   * @param u l'unité (  {@link ChronoUnit} ), si null la méthode retourne ""
   * @return Le nom de l'unité
   */
  public static String fr(ChronoUnit u) {
    if (u == null) return "";
    String frenchName = frenchNames.get(u);
    if (frenchName == null) return u.toString();
    else return frenchName;
  }
}
