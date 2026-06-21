package fr.gpmsi;

import java.time.temporal.ChronoUnit;

/**
 * Petite classe utilitaire pour mesurer facilement le temps écoulé.
 * On met une marque temporelle en appelant mark() (mark() est appelée automatiquement dans le constructeur)
 * Ensuite chaque appel à elapsed() retourne le nombre de millisecondes écoulées depuis l'appel à mark()
 * @author hkaradimas
 */
public class Chrono {
  
  long ts; //timestamp, valeur de System.currentTimeMillis() au moment de la marque
  
  /**
   * Création d'un object Chrono. Au moment de la création, {@link #mark()} est
   * appelé, ce qui met la marque temporelle au moment de la création du Chrono.
   */
  public Chrono() {
    mark();
  }

  /**
   * Placer la marque du temps courant (enregistre la valeur de  la valeur de System.currentTimeMillis())
   */
  public void mark() {
    ts = System.currentTimeMillis();
  }
  
  /**
   * Définir la valeur de la marque temporelle. En pratique n'est utile que pour les tests.
   * @param newTimestamp La nouvelle valeur de la marque temporelle.
   */
  public void setMark(long newTimestamp) {
    ts = newTimestamp;
  }
  
  /**
   * Synonyme de mark(), mais peut être plus clair lorsqu'on réutilise le même chrono, comme ça on voit que la marque
   * a été reposisionnée.
   */
  public void resetMark() {
    mark();
  }
  
  /**
   * Retourner la valeur de la marque temporelle
   * @return la valeur de la marque de temps, qui est la valeur de System.currentTimeMillis() au moment où mark() a été appelé
   */
  public long getMark() {
    return ts;
  }

  /**
   * Retourner le nombre de millisecondes écoulées entre la marque et maintenant.
   * @return nombre de millisecondes écoulées
   */
  public long elapsed() {
    return System.currentTimeMillis() - ts;
  }
  
  /**
   * Retourner le nombre de millisecondes écoulées entre la marque et maintenant.
   * Synonyme de "elapsed()" mais permet en Groovy d'écrire chrono.elapsed
   * @return nombre de millisecondes écoulées
   */
  public long getElapsed() {
    return System.currentTimeMillis() - ts;
  }
  
  /**
   * Emet le message en ajoutant prefix + le temps (en anglais, toujours au pluriel) dans l'unité voulue (avec séparateur point) + le suffixe.
   * @param prefix Le préfixe
   * @param unit L'unité voulue pour l'affichage (le nom est en anglais)
   * @param suffix Le suffixe
   * @return Le message, prêt pour l'affichage
   */
  public String messageEn(String prefix, ChronoUnit unit, String suffix) {    
    long elap = elapsed();
    long ums = unit.getDuration().toMillis();
    long div = elap / ums;
    long rem = elap % ums;
    String et = div + (rem == 0 ? "" : "." + rem) + " " + unit.toString();
    return prefix + et + suffix;
  }
  
  /**
   * Emet le message en ajoutant prefix + le temps (en français) dans l'unité voulue (avec séparateur virgule) + le suffixe.
   * Il est ajouté un "s" à la fin de l'unité si celle-ci n'est pas entre [1;2[
   * @param prefix Le préfixe
   * @param unit L'unité voulue pour l'affichage (le nom est en français)
   * @param suffix Le suffixe
   * @return Le message, prêt pour l'affichage
   */
  public String messageFr(String prefix, ChronoUnit unit, String suffix) {
    long elap = elapsed();
    long ums = unit.getDuration().toMillis();
    long div = elap / ums;
    long rem = elap % ums;
    String plur = div == 1 ? "" : "s";
    String et =  div + (rem == 0 ? "" : "," + rem) + " " + Durees.fr(unit) + plur;
    return prefix + et + suffix;    
  }
  
  /**
   * Appelle messageEn(prefix, unit, "")
   * @param prefix Le préfixe
   * @param unit L'unité voulue pour l'affichage (le nom est en anglais malheureusement)
   * @return Le message, prêt pour l'affichage
   */
  public String messageEn(String prefix, ChronoUnit unit) {
    return messageEn(prefix, unit, "");
  }
  
  /**
   * Appelle messageFr(prefix, unit, "")
   * @param prefix Le préfixe
   * @param unit L'unité voulue pour l'affichage (le nom est en anglais malheureusement)
   * @return Le message, prêt pour l'affichage
   */
  public String messageFr(String prefix, ChronoUnit unit) {
    return messageFr(prefix, unit, "");
  }
  
}
