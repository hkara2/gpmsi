package fr.gpmsi.groovytests;

import groovy.lang.Closure;

/**
 * Test d'un appel closure
 */
public class Closure1Tester {

  String foo;
  String bar;
  Integer baz;
  
  /**
   * Constructeur simple par défaut
   */
  public Closure1Tester() {
  }

  /**
   * test de l'appel de la Closure avec en argument les 3 attributs de la classe
   * @param cl La closure
   * @return Le résultat
   */
  public String testClosure(Closure<String> cl) {
    return cl.call(foo, bar, baz);
  }
}
