package fr.gpmsi.groovytests;

import groovy.lang.Script;

/**
 * Exemple d'un script base qui sera rappelé par un autre script (<i>callback</i>).
 * @author hkaradimas
 *
 */
public abstract class MyRecall
extends Script 
{
	/**
	 * Méthode qui sera appelée par l'autre script
	 * @param arg L'argument à l'appel
	 */
	public void myCall(String arg) { System.out.println("called with "+arg); }
}
