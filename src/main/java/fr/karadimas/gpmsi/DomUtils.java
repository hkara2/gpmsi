package fr.karadimas.gpmsi;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Fonctions utilitaires pour manipuler le DOM.
 * @author hkaradimas
 *
 */
public class DomUtils {
	
  /**
   * Convertir une liste {@link NodeList} en {@link Element} en prenant son premier élément.
   * N'est plus utilisé mais peut encore être utile dans des scripts
   * @param nl La liste qui contient normalement 0 ou 1 noeud
   * @return Le premier élément de la liste s'il est présent
   */
  public static Element toElement(NodeList nl) {
	  if (nl == null) return null;
	  int len = nl.getLength();
	  if (len == 0) return null;
	  Node nd = nl.item(0);
	  if (nd instanceof Element) return (Element)nd;
	  else return null;
  }
  
}
