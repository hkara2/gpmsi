package fr.gpmsi.pmsi_rules.cim.tests;

import static org.junit.Assert.*;

import java.util.regex.Matcher

import org.junit.BeforeClass
import org.junit.Test;

import fr.gpmsi.Groovy
import fr.gpmsi.pmsi_rules.cim.CimCodePresence
import fr.gpmsi.pmsixml.RssReader
import fr.gpmsi.tests.DummyScriptClass

public class CimCodePresenceTests {

  static Groovy g
  String rum1 = "1115M05C 122   910019447022314000              987654321           366938    0504202414201  05042024N 050420246191150328038        000400000A001            000                  2     2        P220    Z762    Z1351   P041    "
  String rum2 = "1115M05C 122   910019447022314000              987654321           366939    0504202414201  05042024N 050420246191150328038        000400000Z511    C968    000                  2     2        P220    Z762    Z1351   P041    "
  
  /**
   * Code d'initialisation qui permet d'attacher les méthodes dynamiques pour exécution par les tests
   */
  @BeforeClass
  static void setup() {
    g = new Groovy()
    String[] args = ['-run', 'fr.gpmsi.tests.DummyScriptClass'] 
    g.init(args)
    g.run()
  }
  
  @Test
  void testCimPattern1() {
    String code = "A001" //A00.1 A Vibrio cholerae 01, biovar El Tor
    RssReader rr = new RssReader()
    def nd = rr.readOne(rum1)
    def ccp = new CimCodePresence("DP", code)
    HashMap context = new HashMap()
    context.put("rum", nd)
    boolean match = ccp.eval(context)
    assertTrue("'$code' devrait etre retrouve dans le rum", match)
  }
  
  @Test
  void testCimPattern2() {
    String range = "Z13.*" //n'importe quel code qui commence par Z13
    RssReader rr = new RssReader()
    def nd = rr.readOne(rum1)
    def ccp = new CimCodePresence("DP,DAS", range) //recherche dans DP ou DAS
    HashMap context = new HashMap()
    context.put("rum", nd)
    boolean match = ccp.eval(context)
    assertTrue("'$range' devrait etre retrouve dans le rum", match)
  }
  
  @Test
  void testCimRange1() {
    String pattern = "P0:P2" //codes de P0 à P29999
    RssReader rr = new RssReader()
    def nd = rr.readOne(rum1)
    def ccp = new CimCodePresence("DP,DAS", pattern) //recherche dans DP ou DAS
    HashMap context = new HashMap()
    context.put("rum", nd)
    boolean match = ccp.eval(context)
    assertTrue("'$pattern' devrait etre retrouve dans le rum", match)
  }
  
  @Test
  void testCimRange2() {
    String pattern = "P2:P3" //codes de P2 à P39999
    RssReader rr = new RssReader()
    def nd = rr.readOne(rum1)
    def ccp = new CimCodePresence("DP,DAS", pattern) //recherche dans DP ou DAS
    HashMap context = new HashMap()
    context.put("rum", nd)
    boolean match = ccp.eval(context)
    assertTrue("'$pattern' devrait etre retrouve dans le rum", match)
  }
  
  @Test
  void testCimRange3() {
    String pattern = "P3:P4" //codes de P3 à P49999
    RssReader rr = new RssReader()
    def nd = rr.readOne(rum1)
    def ccp = new CimCodePresence("DP,DAS", pattern) //recherche dans DP ou DAS
    HashMap context = new HashMap()
    context.put("rum", nd)
    boolean match = ccp.eval(context)
    assertFalse("'$pattern' ne devrait pas etre retrouve dans le rum", match)
  }
  
  /**
   * Ce test se fait sur le rum2 et teste le DR
   */
  @Test
  void testCimRange4() {
    String pattern = "C00:D09" //codes de tumeurs malignes + tumeurs in situ
    RssReader rr = new RssReader()
    def nd = rr.readOne(rum2)
    def ccp = new CimCodePresence("DR", pattern) //recherche dans DR
    HashMap context = new HashMap()
    context.put("rum", nd)
    boolean match = ccp.eval(context)
    assertTrue("'$pattern' devrait etre retrouve en DR dans le rum", match)
  }
  
}