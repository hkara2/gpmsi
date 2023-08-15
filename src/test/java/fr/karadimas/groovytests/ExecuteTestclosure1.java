package fr.karadimas.groovytests;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;

import groovy.lang.GroovyShell;

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
    sh.evaluate(new File("src\\fr\\karadimas\\groovytests\\testclosure1.groovy"));
  }

}
