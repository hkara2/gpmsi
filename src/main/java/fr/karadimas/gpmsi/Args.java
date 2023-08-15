package fr.karadimas.gpmsi;

/**
 * Classe utilitaire pour faciliter la lecture des arguments passés en ligne
 * de commande des scripts groovy
 * @author hkaradimas
 *
 */
public class Args {
  String[] arguments;
  int p;
  public Args(String[] argsp) { this.arguments = argsp; }
  public String current() { return arguments[p]; }
  public String next() { return arguments[p++]; }
  public int index() { return p; }
  public boolean hasNext() { return p < arguments.length; }
}
