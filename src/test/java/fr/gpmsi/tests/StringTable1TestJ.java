package fr.gpmsi.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.gpmsi.Groovy;

/**
 * Lancement du test Groovy StringTable1Test.groovy
 */
public class StringTable1TestJ {

  /**
   * Préparation (ne fait rien ici)
   * @throws Exception _
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * Rangement 
   * @throws Exception _
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Lancement du test
   * @throws Exception _
   */
  @Test
  public void test() throws Exception {
    Groovy g = new Groovy();
    System.out.println("Current dir : " + System.getProperty("user.dir"));
    String[] args = {"-debug", "-script", "src\\test\\groovy\\fr\\gpmsi\\tests\\StringTable1Test.groovy"};
    g.init(args);
    g.run();
  }

}
