package fr.gpmsi.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.gpmsi.Phonex;
import junit.framework.TestCase;

/**
 * Tests pour algorithme Phonex
 */
public class PhonexTest extends TestCase {

  /**
   * Préparation 
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * Nettoyage
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test 1 : DE MUNICO -&gt; TE NUNIKO
   */
  @Test
  public void testToPhonex1() {
    String nom = "DE MUNICO";
    String r = Phonex.toPhonex(nom);
    assertEquals("Attendu : TE NUNIKO", r, "TE NUNIKO");
    //System.out.println(r);
  }

  /**
   * Test 2 : sans espaces : DE MUNICO -&gt; TE NUNIKO
   */
  @Test
  public void testToPhonex2() {
    String nom = "DE MUNICO";
    String r = Phonex.toPhonexWithoutSpaces(nom);
    assertEquals("Attendu : TENUNIKO", r, "TENUNIKO");
    //System.out.println(r);
  }

  /**
   * Test de standardisation 1 : "Hélène Carrère d'encausse" -> "HELENECARREREDENCAUSSE"
   */
  @Test
  public void testStandardize1_1() {
      String nom = "Hélène Carrère d'encausse";
      String r = Phonex.standardize1(nom);
      assertEquals("Attendu  : HELENECARREREDENCAUSSE", r, "HELENECARREREDENCAUSSE");
  }

  /**
   * Test de standardisation 2 : "Jean-Philippe" -&gt; "JEANPHILIPPE"
   */
  @Test
  public void testStandardize1_2() {
      String nom = "Jean-Philippe";
      String r = Phonex.standardize1(nom);
      assertEquals("Attendu : JEANPHILIPPE", r, "JEANPHILIPPE");
  }

}
