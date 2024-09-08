package fr.gpmsi;

/**
 * Classe utilitaire pour faciliter la lecture des arguments passés en ligne
 * de commande des scripts groovy
 * @author hkaradimas
 *
 */
public class Args {
  
  /** arguments */
  String[] arguments;
  
  /** pointeur */
  int p;
  
  /**
   * constructeur
   * @param argsp Les arguments
   */
  public Args(String[] argsp) { this.arguments = argsp; }
  
  /**
   * retourner courant sans bouger le pointeur
   * @return L'argument courant
   */
  public String current() { return arguments[p]; }
  
  /** 
   * retourner courant et avancer le pointeur sur le suivant
   * @return l'argument courant 
   */
  public String next() { return arguments[p++]; }
  
  /** 
   * retourner le pointeur 
   * @return le pointeur
   */
  public int index() { return p; }

  /**
   * est-ce qu'il y a encore au moins un argument à traiter
   * @return true s'il y a encore au moins un argument à traiter
   */
  public boolean hasNext() { return p < arguments.length; }
}
