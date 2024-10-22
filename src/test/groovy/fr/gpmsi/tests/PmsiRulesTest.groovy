//☺:encoding=UTF-8:
package fr.gpmsi.tests;

import static org.junit.Assert.*;

import java.util.HashMap

import org.junit.BeforeClass
import org.junit.Test;

//import fr.gpmsi.tests.PmsiRulesTest.RssRuleGhmEstZ
import fr.gpmsi.inits
import fr.gpmsi.pmsi_rules.PmsiRule;
import fr.gpmsi.pmsi_rules.ccam.CcamCodePresence
import fr.gpmsi.pmsi_rules.cim.CimCodePresence
import fr.gpmsi.pmsi_rules.ghm.GhmCodePresence
import fr.gpmsi.pmsi_rules.rsa.RsaRule
import fr.gpmsi.pmsi_rules.rsa.RsaRuleEngine
import fr.gpmsi.pmsi_rules.rss.RssRule;
import fr.gpmsi.pmsi_rules.rss.RssRuleEngine
import fr.gpmsi.pmsixml.FszGroup
import fr.gpmsi.pmsixml.FszNode
import fr.gpmsi.pmsixml.RsaReader
import fr.gpmsi.pmsixml.RssReader

public class PmsiRulesTest {

  static String RSS1 = '1128Z07Z 120   910019447020279302              012345678           326987    1703196224024  030120228 030120228 91530              010100000Z511    C349    000                  2              C793    '
  static String RSS2 = '1128Z07Z 120   910019447020279302              012345678           326987    1703196224024  030120228 030120228 91530              010100000Z511    C359    000                  2              C793    '
  static String RSS3 = '1108C491 121   910019447021286341              900168533           335106    0208197114102  2706202285040720227191530              000100005S7210           000                  2     2        W190    27062022NAQK071-0001 UZ   10127062022NBCA006-00041      0127062022NBCA006-0001       0127062022ZBQK002-0001 Z    20128062022NAQK071-0001 Z    101'
  static String RSA1 = '91001944722500000134561190051108C3710001108C37100001067   28 1120198  0004915800000     00283100000000 0 00 00000000000000000000000 028310       00000000000000000000000000000                 0 0 0    01M201        000100003               01   910001973M201        0000001003000453 C000000          M7747 001NDPA002  041    01011001NDPA002  01     01011002NDQK001  01 Y   01011'
  
  /** Permet d'avoir les métaclasses pour appeler ZA, DP, DA, etc. dans les tests. */
  @BeforeClass
  static void init() {
    inits.main()
  }
  
  /** Regle qui ecrit 'code found !' si le DP est Z511 */
  class RssRuleR1 extends RssRule {
    CimCodePresence p1 = new CimCodePresence('DP', 'Z511')
    
    @Override
    public boolean eval(HashMap context) {
      return p1.eval(context)
    }
    
    @Override
    public void action(HashMap context) {
      PrintWriter outPw = context['out']
      outPw.write('code found ! \n')
      System.out.println('code found !!')
    }
  }
  
  /** Regle qui ecrit 'code found !' si le DR est C34. (expression reguliere) */
  class RssRuleR2 extends RssRuleR1 {
    CimCodePresence p2 = new CimCodePresence('DR', 'C34.')
    
    @Override
    public boolean eval(HashMap context) { return p2.eval(context) }
  }

  class RssRuleGhmEstZ extends RssRule {
    GhmCodePresence p1 = new GhmCodePresence('..Z.+')
    
    @Override
    public boolean eval(HashMap context) { return p1.eval(context); }
    
    @Override
    public void action(HashMap context) {
      PrintWriter outPw = context['out']
      outPw.write('code GHM en Z trouve ! \n')
      System.out.println('code Z trouve !!')
    }
  }
  
  /** Regle qui ecrit 'code ccam osteosynthese trouve !' si un des codes CCAM de ZA est dans la liste. */
  class RssCcamRuleR1 extends RssRule {
    CcamCodePresence cp = new CcamCodePresence(['NBCA006', 'NBCA009', 'NBCA004'] as Set)
    
    @Override
    public boolean eval(HashMap context) {
      return cp.eval(context)
    }
    
    @Override
    public void action(HashMap context) {
      PrintWriter outPw = context['out']
      outPw.write('code ccam osteosynthese trouve ! \n')
      System.out.println('code ccam osteosynthese trouve !')
    }
  }
  
  /** Regle qui ecrit 'code ccam osteosynthese trouve !' si un des codes CCAM de ZA est dans l'expression régulière. */
  class RssCcamRuleR2 extends RssRule {
    CcamCodePresence cp = new CcamCodePresence(['NBCA00.*', 'NAQK072', 'NAQK073'] as Set)
    
    @Override
    public boolean eval(HashMap context) {
      return cp.eval(context)
    }
    
    @Override
    public void action(HashMap context) {
      PrintWriter outPw = context['out']
      outPw.write('code ccam osteosynthese (code commencant par NBCA00) trouve ! \n')
      System.out.println('code ccam osteosynthese (code commencant par NBCA00) trouve !')
    }
  }
  
  /** Regle qui ecrit 'code ccam osteosynthese trouve !' si un des codes CCAM de ZA est dans l'expression régulière. */
  class RsaCcamRuleR1 extends RsaRule {
    CcamCodePresence cp = new CcamCodePresence(['NDPA002.*', 'NDQK001-01'] as Set, false) //attention indiquer qu'on n'ignore pas l'extension !
    
    @Override
    public boolean eval(HashMap context) {
      return cp.eval(context)
    }
    
    @Override
    public void action(HashMap context) {
      PrintWriter outPw = context['out']
      outPw.write('code ccam NDPA002 ou NDQK001 trouve ! \n')
      System.out.println('code ccam NDPA002 ou NDQK001 trouve ! ')
    }
  }
  
  @Test
  public void testR1_1() {
    RssRuleEngine eng = new RssRuleEngine()
    eng.add(new RssRuleR1())
    RssReader rrdr = new RssReader()
    FszNode g = rrdr.readOne(RSS1)
    eng.evalRum(g);
  }

  @Test
  public void testR2_1() {
    RssRuleEngine eng = new RssRuleEngine()
    eng.add(new RssRuleR2())
    RssReader rrdr = new RssReader()
    FszNode g = rrdr.readOne(RSS1)
    eng.evalRum(g);
  }

  /** Test avec le RSS2 qui ne contient pas de DR qui commence par C34 */
  @Test
  public void testR2_2() {
    RssRuleEngine eng = new RssRuleEngine()
    eng.add(new RssRuleR2())
    RssReader rrdr = new RssReader()
    FszNode g = rrdr.readOne(RSS2)
    eng.evalRum(g);
  }

  /** Test avec le RSS3 qui contient un acte que l'on veut tester */
  @Test
  public void testR3_1() {
    RssRuleEngine eng = new RssRuleEngine()
    eng.add(new RssCcamRuleR1())
    RssReader rrdr = new RssReader()
    FszNode g = rrdr.readOne(RSS3)
    eng.evalRum(g);
  }

  /** Test avec le RSS3 qui contient un acte que l'on veut tester, mais en utilisant la règle 2 pour ccam, avec une expression régulière */
  @Test
  public void testR3_2() {
    RssRuleEngine eng = new RssRuleEngine()
    eng.add(new RssCcamRuleR2())
    RssReader rrdr = new RssReader()
    FszNode g = rrdr.readOne(RSS3)
    eng.evalRum(g);
  }

  /** Test avec le RSA1 et la règle RsaCcamRuleR1 */
  @Test
  public void testRSA1_Ccam2() {
    RsaRuleEngine eng = new RsaRuleEngine()
    eng.add(new RsaCcamRuleR1())
    RsaReader rrdr = new RsaReader()
    FszNode g = rrdr.readRSA(RSA1, 1)
    eng.evalRsa(g);
  }

  @org.junit.Test
  void testRssRuleGhmEstZ() {
    RssRuleEngine eng = new RssRuleEngine()
    eng.add(new RssRuleGhmEstZ()) 
    RssReader rrdr = new RssReader()
    FszNode g = rrdr.readOne(RSS1)
    eng.evalRum(g);
  }
}
