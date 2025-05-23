package fr.gpmsi.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.gpmsi.StringUtils;

public class StringUtilsTests {

  @Test
  public void testNormalizeNewlines1() {
    String a = "foo\nbar\nbaz";
    String b = StringUtils.normalizeLineSeparators(a, "@");
    assertEquals(b, "foo@bar@baz");
  }

  @Test
  public void testNormalizeNewlines2() {
    String a = "foo\rbar\rbaz";
    String b = StringUtils.normalizeLineSeparators(a, "@");
    assertEquals(b, "foo@bar@baz");
  }

  @Test
  public void testNormalizeNewlines3() {
    String a = "foo\r\nbar\r\nbaz";
    String b = StringUtils.normalizeLineSeparators(a, "@");
    assertEquals(b, "foo@bar@baz");
  }

  @Test
  public void testNormalizeNewlines4() {
    String a = "foo\r\nbar\r\nbaz\r";
    String b = StringUtils.normalizeLineSeparators(a, "@");
    assertEquals(b, "foo@bar@baz@");
  }

  @Test
  public void testNormalizeNewlines5() {
    String a = "foo\r\nbar\r\r\nbaz\r";
    String b = StringUtils.normalizeLineSeparators(a, "@");
    assertEquals(b, "foo@bar@@baz@");
  }

}
