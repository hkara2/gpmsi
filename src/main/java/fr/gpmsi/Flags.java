package fr.gpmsi;

import java.util.HashSet;

/**
 * Hashset spécialisé pour contenir les drapeaux ("flags") utilisés dans les scripts.
 * La seule différence c'est que "getAt" est surchargé pour retourner TRUE ou FALSE selon
 * que le drapeau est contenu ou non.
 * @author hkaradimas
 *
 */
public class Flags
    extends HashSet<String> 
{
  static final long serialVersionUID = 0xF00001;
  
  /**
   * Constructeur par défaut
   */
  Flags() { }
  
  /**
   * Spécialise getAt de manière à ce qu'on puisse écrire flags['mondrapeau'] pour tester si mondrapeau est contenu
   * ou non dans les drapeaux.
   * @param flagName Nom du drapeau à rechercher
   * @return Boolean.TRUE si contient le drapeau, Boolean.FALSE sinon
   */
  public Object getAt(String flagName) {
    if (contains(flagName)) return Boolean.TRUE; else return Boolean.FALSE;
  }

}
