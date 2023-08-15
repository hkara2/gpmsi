package fr.karadimas.gpmsi.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.karadimas.gpmsi.Phonex;
import junit.framework.TestCase;

public class PhonexTest extends TestCase {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testToPhonex1() {
    String nom = "DE MUNICO";
    String r = Phonex.toPhonex(nom);
    assertEquals("Attendu : TE NUNIKO", r, "TE NUNIKO");
    //System.out.println(r);
  }

  @Test
  public void testToPhonex2() {
    String nom = "DE MUNICO";
    String r = Phonex.toPhonexWithoutSpaces(nom);
    assertEquals("Attendu : TE NUNIKO", r, "TENUNIKO");
    //System.out.println(r);
  }

}
