package fr.karadimas.gpmsi.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.karadimas.gpmsi.CsvUtils;

public class CsvUtilsTests {

  @Test
  public void testReplaceNewlines1() {
    String row[] = {"foo", "bar"};
    CsvUtils.replaceNewlines(row, "\\n");
    assertEquals("foo should not have changed", "foo", row[0]);
    assertEquals("bar should not have changed", "bar", row[1]);
  }

  @Test
  public void testReplaceNewlines2() {
    String row[] = {"foo", "ba\rr"};
    CsvUtils.replaceNewlines(row, "#n");
    assertEquals("foo should not have changed", "foo", row[0]);
    assertEquals("row[1] bad value, ", "ba#nr", row[1]);
  }

  @Test
  public void testReplaceNewlines3() {
    String row[] = {"foo", "ba\r\nr"};
    CsvUtils.replaceNewlines(row, "#n");
    assertEquals("foo should not have changed", "foo", row[0]);
    assertEquals("row[1] bad value, ", "ba#nr", row[1]);
  }

  /**
   * Doit marcher même si on mélange les séquences de fin de ligne
   */
  @Test
  public void testReplaceNewlines4() {
    String row[] = {"foo", "ba\n\r\nr"};
    CsvUtils.replaceNewlines(row, "#n");
    assertEquals("foo should not have changed", "foo", row[0]);
    assertEquals("row[1] bad value, ", "ba#n#nr", row[1]);
  }

  /**
   * Doit marcher même si on mélange les séquences de fin de ligne
   */
  @Test
  public void testReplaceNewlines5() {
    String row[] = {"foo", "ba\n\r\r\n\nr"};
    CsvUtils.replaceNewlines(row, "#n");
    assertEquals("foo should not have changed", "foo", row[0]);
    assertEquals("row[1] bad value, ", "ba#n#n#n#nr", row[1]);
  }

}
