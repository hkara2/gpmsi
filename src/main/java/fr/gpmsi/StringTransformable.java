package fr.gpmsi;

import java.text.ParseException;

/**
 * Implémenté par les classes qui transforment un String en un autre String.
 * 
 * Exemple de définition en Groovy :
 * <pre>
 * def tonum = {s -&gt; "" + (s as int)} as StringTransformable //pour transformer le texte en numerique
 * </pre>
 * @author hkaradimas
 *
 */
public interface StringTransformable {
	/**
	 * Transformer le String et retourner la transformée.
	 * 
	 * @param input La chaîne à transformer
	 * @return la chaîne transformée
	 * @throws ParseException si il y a un problème avec l'analyse de l'entrée
	 */
  String transform(String input) throws ParseException;
}
