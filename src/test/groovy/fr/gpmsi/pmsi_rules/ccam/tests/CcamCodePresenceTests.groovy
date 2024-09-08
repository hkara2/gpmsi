package fr.gpmsi.pmsi_rules.ccam.tests;

import static org.junit.Assert.*;

import java.util.regex.Matcher

import org.junit.Test;

import fr.gpmsi.pmsi_rules.ccam.CcamCodePresence

public class CcamCodePresenceTests {

  @Test
  void testCcamPattern1() {
    String code = "AHGA801-02"
    boolean match = CcamCodePresence.ccamPattern.matcher(code).matches()
    assertTrue("'$code' devrait etre accepte par l'expression reguliere", match)
  }
  
  @Test
  void testCcamPattern2() {
    String code = "AHGA801"
    boolean match = CcamCodePresence.ccamPattern.matcher(code).matches()
    assertTrue("'$code' devrait etre accepte par l'expression reguliere", match)
  }

  @Test
  void testBadCcamPattern1() {
    String code = "AHGA80"
    boolean match = CcamCodePresence.ccamPattern.matcher(code).matches()
    assertFalse("'$code' ne devrait pas etre accepte par l'expression reguliere", match)
  }

  @Test
  void testBadCcamPattern2() {
    String code = "AHGA8013"
    Matcher matcher = CcamCodePresence.ccamPattern.matcher(code)
    boolean match = matcher.matches()
    if (match) {
      def str = matcher.group(1)
      println "matched : $str"
    }
    assertFalse("'$code' ne devrait pas etre accepte par l'expression reguliere", match)
  }

  @Test
  void testBadCcamPattern3() {
    String code = "AHGA801-"
    boolean match = CcamCodePresence.ccamPattern.matcher(code).matches()
    assertFalse("'$code' ne devrait pas etre accepte par l'expression reguliere", match)
  }

  @Test
  void testBadCcamPattern4() {
    String code = "AHGA801-0"
    boolean match = CcamCodePresence.ccamPattern.matcher(code).matches()
    assertFalse("'$code' ne devrait pas etre accepte par l'expression reguliere", match)
  }

}
