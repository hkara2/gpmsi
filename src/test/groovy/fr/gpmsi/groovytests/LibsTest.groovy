package fr.gpmsi.groovytests

import static org.junit.Assert.*;

import java.util.HashMap

import ca.uhn.hl7v2.DefaultHapiContext
import ca.uhn.hl7v2.HapiContext
import ca.uhn.hl7v2.parser.GenericParser

/**
 * Tester si les librairies que les scripts groovy doivent accéder sont bien accessibles après 
 * l'installation.
 * N.B. Pour l'instant gradle ne voit pas cette classe depuis java.
 * @author hkaradimas
 *
 */
public class LibsTest extends Script {

  /** 
   * Tester si la librairie groovy-ant est presente et accessible 
   */
  void testGroovyAntLib() {
    System.err.println('Test chargement librairie groovy-ant')
    def ant = new groovy.ant.AntBuilder()
    ant.echo('hello from Ant!')
    System.err.println('Chargement ok.')
  }

  /** 
   * Tester si la librairie groovy-xml est presente et accessible 
   */
  void testGroovyXmlLib() {
    System.err.println('Test chargement librairie groovy-xml')
    def xmlSlurper = new groovy.xml.XmlSlurper()
    System.err.println('Chargement ok.')
  }

  /** 
   * Tester si la librairie groovy-swing est presente et accessible 
   */
  void testGroovySwingLib() {
    System.err.println('Test chargement librairie groovy-swing')
    def xmlSlurper = new groovy.swing.SwingBuilder()
    System.err.println('Chargement ok.')
  }

  void testHapiLib() {
    System.err.println('Test utilisation librairie hapi')
    HapiContext ctx = new DefaultHapiContext()
    GenericParser gp = ctx.getGenericParser()
    gp.parse('MSH|^~\\&|||||20230214145150.73+0100||ADT^A04^ADT_A01|7201|T|2.5\r\n')
  }
  
  void runTest(String testName) {
    try {
      System.out.println("Execution test $testName")
      "$testName"()
    }
    catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
  }
  
  static void runAllTests() {
    LibsTest app = new LibsTest()
    ['testGroovyAntLib', 'testGroovyXmlLib', 'testGroovySwingLib', 'testHapiLib'].each { app.runTest(it) }
  }

  /**
   * Cette methode sera appelee si on appelle cette classe via fr.gpmsi.Groovy
   */
  @Override
  public Object run() {
    return runAllTests();
  }
  
}
