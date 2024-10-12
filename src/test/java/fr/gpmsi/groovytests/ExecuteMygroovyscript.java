package fr.gpmsi.groovytests;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;

import groovy.lang.GroovyShell;

/**
 * Test d'exécution d'un script Groovy à partir du {@link GroovyShell}
 */
public class ExecuteMygroovyscript {

  /**
   * Execution tres simplifiee du script
   * @param args Arguments
   * @throws CompilationFailedException Si err compilation
   * @throws IOException Si erreur E/S
   */
  public static void main(String[] args)
      throws CompilationFailedException, IOException
  {
    GroovyShell sh = new GroovyShell();
    sh.evaluate(new File("src\\main\\groovy\\fr\\gpmsi\\groovytests\\mygroovyscript.groovy"));
  }

}
