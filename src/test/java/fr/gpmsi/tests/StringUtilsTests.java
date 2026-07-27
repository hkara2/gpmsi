package fr.gpmsi.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.gpmsi.StringUtils;

/**
 * Tests pour StringUtils
 */
public class StringUtilsTests {

  /**
   * Test normalisation de ligne 1
   */
  @Test
  public void testNormalizeNewlines1() {
    String a = "foo\nbar\nbaz";
    String b = StringUtils.normalizeLineSeparators(a, "@");
    assertEquals(b, "foo@bar@baz");
  }

  /**
   * Test normalisation de ligne 2
   */
  @Test
  public void testNormalizeNewlines2() {
    String a = "foo\rbar\rbaz";
    String b = StringUtils.normalizeLineSeparators(a, "@");
    assertEquals(b, "foo@bar@baz");
  }

  /**
   * Test normalisation de ligne 3
   */
  @Test
  public void testNormalizeNewlines3() {
    String a = "foo\r\nbar\r\nbaz";
    String b = StringUtils.normalizeLineSeparators(a, "@");
    assertEquals(b, "foo@bar@baz");
  }

  /**
   * Test normalisation de ligne 4
   */
  @Test
  public void testNormalizeNewlines4() {
    String a = "foo\r\nbar\r\nbaz\r";
    String b = StringUtils.normalizeLineSeparators(a, "@");
    assertEquals(b, "foo@bar@baz@");
  }

  /**
   * Test normalisation de ligne 5
   */
  @Test
  public void testNormalizeNewlines5() {
    String a = "foo\r\nbar\r\r\nbaz\r";
    String b = StringUtils.normalizeLineSeparators(a, "@");
    assertEquals(b, "foo@bar@@baz@");
  }

  /**
   * Test de la méthode getUtf8StringResource
   * @throws Exception Ne devrait jamais se produire
   */
  @Test
  public void testGetUtf8StringResource()
      throws Exception
  {
      String str = StringUtils.getUtf8StringResource("/fr/gpmsi/pmsixml/vidhospV016.csv");
      assertNotNull("\"/fr/gpmsi/pmsixml/vidhospV016.csv\" devrait être trouvé", str);
      //System.out.println("\"/fr/gpmsi/pmsixml/vidhospV016.csv\" : "+str);
      assertTrue("La resource devrait commencer par :Typ;Libellé;Nomc", str.startsWith("Typ;Libellé;Nomc"));
  }
}
