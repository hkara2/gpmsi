package fr.gpmsi.groovytests;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;

import groovy.lang.GroovyShell;

/**
 * Tester une closure à l'intérieur d'un script Groovy appelé par {@link GroovyShell}
 */
public class ExecuteTestclosure1 {

  /**
   * Execution tres simplifiee du script
   * @param args Arguments
   * @throws CompilationFailedException Si erreur compilation
   * @throws IOException Si erreur E/S
   */
  public static void main(String[] args)
      throws CompilationFailedException, IOException
  {
    GroovyShell sh = new GroovyShell();
    sh.evaluate(new File("src\\fr\\gpmsi\\groovytests\\testclosure1.groovy"));
  }

}
