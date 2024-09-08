package fr.gpmsi.groovytests;

import groovy.lang.Closure;

public class Closure1Tester {

  String foo;
  String bar;
  Integer baz;
  
  public Closure1Tester() {
  }

  //test de l'appel de la Closure avec en argument les 3 attributs de la classe
  public String testClosure(Closure<String> cl) {
    return cl.call(foo, bar, baz);
  }
}
