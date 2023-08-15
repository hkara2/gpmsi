package fr.karadimas.groovytests;

import groovy.lang.Script;

/**
 * Exemple d'un script base qui sera rappelé par un autre script (<i>callback</i>).
 * @author hkaradimas
 *
 */
public abstract class MyRecall extends Script {
	public void myCall(String arg) { System.out.println("called with "+arg); }
}
