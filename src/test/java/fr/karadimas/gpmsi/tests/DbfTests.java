package fr.karadimas.gpmsi.tests;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

import fr.karadimas.gpmsi.Groovy;

public class DbfTests {

  /**
   * Tester qu'on a bien 1144 lignes retrouvées dans le fichier DBF de test (la ligne de titres compte comme une ligne, excel fait pareil).
   * @throws Exception _
   */
  @Test
  public void testDbf1()
      throws Exception
  {
    String[] args = {"-script", "test-files/test_scripts/Dbf1Test.groovy"}; 
    Groovy g = new Groovy();
    g.init(args);
    g.run();
    assertEquals("Il y a 1144 lignes dans le fichier de test R_ACTE_FORFAIT.dbf", 1144, g.getReturnedObject());
  }

  /**
   * Tester qu'on a bien 1144 lignes retrouvées dans le fichier DBF de test (la ligne de titres compte comme une ligne, excel fait pareil).
   * @throws Exception _
   */
  @Test
  public void testDbf2()
      throws Exception
  {
    String[] args = {"-script", "test-files/test_scripts/Dbf2Test.groovy"}; 
    Groovy g = new Groovy();
    g.init(args);
    g.run();
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(2011, 01-1, 10);
    assertEquals("En ligne 6 la date est 10/01/2011", cal.getTime(), g.getReturnedObject());
  }

  //ligne 6 : 10/01/2011
  
}
