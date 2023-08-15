package fr.karadimas.gpmsi;

/**
 * Petite classe utilitaire pour mesurer facilement le temps écoulé.
 * @author hkaradimas
 *
 */
public class Chrono {

  long ts; //timestamp, valeur de System.currentTimeMillis() au moment de la marque
  
  /**
   * Création d'un object Chrono. Au moment de la création, {@link #mark()} est
   * appelé, ce qui marque le temps de la création du Chrono.
   */
  public Chrono() {
    mark();
  }

  /**
   * Placer la marque du temps courant
   */
  public void mark() {
    ts = System.currentTimeMillis();
  }

  /**
   * Retourner le nombre de millisecondes écoulées entre la marque et maintenant.
   * @return nombre de millisecondes écoulées
   */
  public long elapsed() {
    return System.currentTimeMillis() - ts;
  }
  
}
