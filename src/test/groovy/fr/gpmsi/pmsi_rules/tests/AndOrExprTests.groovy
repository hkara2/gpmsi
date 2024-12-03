package fr.gpmsi.pmsi_rules.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.gpmsi.pmsi_rules.AndOrExpr
import fr.gpmsi.pmsi_rules.ParseException

public class AndOrExprTests {

  @Test
  void testParsing1() {
    AndOrExpr e = new AndOrExpr("foo")
    println "e = ${e.dump()}"
  }
  
  @Test
  void testParsing2() {
    AndOrExpr e = new AndOrExpr("foo|bar")
    println "e = ${e.dump()}"
  }

  @Test
  void testParsing3() {
    AndOrExpr e = new AndOrExpr("foo|bar|baz")
    println "e = ${e.dump()}"
  }

  @Test
  void testParsing4() {
    AndOrExpr e = new AndOrExpr("foo|bar&2|baz&3")
    println "e = \n${e.dump()}"
  }

  @Test
  void testParsing5() {
    AndOrExpr e = new AndOrExpr("foo & bar & (x | bar & 2) | baz & 3")
    println "e = \n${e.dump()}"
  }

  @Test
  void testParsing6() {
    AndOrExpr e = new AndOrExpr("!foo & !(bar) | !baz & !(3,4)")
    println "e = \n${e.dump()}"
  }

  @Test
  void testParsing7() {
    AndOrExpr e = new AndOrExpr("!x>3 & (!z ~= 5 & !baz of all)")
    println "e = \n${e.dump()}"
  }

  @Test
  void testParsingError1() {
    try {
      new AndOrExpr("foo & bar &")
      fail("Il y aurait du y avoir une exception ParseException")
    }
    catch (ParseException pex) {
      assertTrue("Il doit y avoir une erreur 'Erreur valeur manquante...'", pex.message.startsWith("Erreur valeur manquante après '&'"))
    }
  }

  @Test
  void testParsingError2() {
    try {
      new AndOrExpr("(foo & bar))")
      fail("Il y aurait du y avoir une exception ParseException")
    }
    catch (ParseException pex) {
      //pex.printStackTrace()
      assertTrue("Il doit y avoir une erreur 'Erreur de syntaxe en position 12'", pex.message.startsWith("Erreur de syntaxe en position 12"))
    }
  }

  @Test
  void testParsingError3() {
    try {
      def expr = new AndOrExpr("foo && bar")
      println "${expr.dump()}"
      fail("Il y aurait du y avoir une exception ParseException")
    }
    catch (ParseException pex) {
      //pex.printStackTrace()
      assertTrue("Il doit y avoir une erreur 'Valeur vide en position 6'", pex.message.startsWith("Valeur vide en position 6"))
    }
  }

  @Test
  void testParsingError4() {
    try {
      def expr = new AndOrExpr("foo & ")
      //println "${expr.dump()}"
      fail("Il y aurait du y avoir une exception ParseException")
    }
    catch (ParseException pex) {
      //pex.printStackTrace()
      assertTrue("Il doit y avoir une erreur 'Erreur valeur manquante après '&' à la position 6'", pex.message.startsWith("Erreur valeur manquante après '&' à la position 6"))
    }
  }

  @Test
  void testParsingError5() {
    try {
      def expr = new AndOrExpr("!")
      //println "${expr.dump()}"
      fail("Il y aurait du y avoir une exception ParseException")
    }
    catch (ParseException pex) {
      //pex.printStackTrace()
      assertTrue("Il doit y avoir une erreur 'Erreur valeur manquante après '!' à la position 1'", pex.message.startsWith("Erreur valeur manquante après '!' à la position 1"))
    }
  }

  @Test
  void testParsingError6() {
    try {
      def expr = new AndOrExpr("5&!")
      //println "${expr.dump()}"
      fail("Il y aurait du y avoir une exception ParseException")
    }
    catch (ParseException pex) {
      //pex.printStackTrace()
      assertTrue("Il doit y avoir une erreur 'Erreur valeur manquante après '!' à la position 3'", pex.message.startsWith("Erreur valeur manquante après '!' à la position 3"))
    }
  }

  @Test
  void testParsingError7() {
    try {
      def expr = new AndOrExpr("5 & !")
      println "${expr.dump()}"
      fail("Il y aurait du y avoir une exception ParseException")
    }
    catch (ParseException pex) {
      //pex.printStackTrace()
      assertTrue("Il doit y avoir une erreur 'Erreur valeur manquante après '!' à la position 5'", pex.message.startsWith("Erreur valeur manquante après '!' à la position 5"))
    }
  }

  @Test
  void testParsingError8() {
    try {
      def expr = new AndOrExpr("5 & !(")
      println "${expr.dump()}"
      fail("Il y aurait du y avoir une exception ParseException")
    }
    catch (ParseException pex) {
      //pex.printStackTrace()
      assertTrue("Il doit y avoir une erreur 'Valeur vide en position 6'", pex.message.startsWith("Valeur vide en position 6"))
    }
  }

  @Test
  void testParsingError9() {
    try {
      def expr = new AndOrExpr("5 & !(5|2")
      println "${expr.dump()}"
      fail("Il y aurait du y avoir une exception ParseException")
    }
    catch (ParseException pex) {
      //pex.printStackTrace()
      assertTrue("Il doit y avoir une erreur 'Absence de parenthèse fermante en position 9'", pex.message.startsWith("Absence de parenthèse fermante en position"))
    }
  }

}
