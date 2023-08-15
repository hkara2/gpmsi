package fr.karadimas.gpmsi.pmsi_rules.tests;

import static org.junit.Assert.*
import org.junit.Test;

import fr.karadimas.gpmsi.pmsi_rules.And
import fr.karadimas.gpmsi.pmsi_rules.AndOrExpr
import fr.karadimas.gpmsi.pmsi_rules.Val
import fr.karadimas.gpmsi.pmsi_rules.AoeNode
import fr.karadimas.gpmsi.pmsi_rules.ExampleNodeTreeWalker;

public class ExampleNodeTreeWalkerTests {

  @Test
  public void testWalk1() {
    ExampleNodeTreeWalker ntw = new ExampleNodeTreeWalker();
    Val v1 = new Val(val: "foo")
    Val v2 = new Val(val:"bar")
    And nd = new And(left: v1 , right: v2)
    def m = [:]
    def stk = new LinkedList()
    ntw.walk(m, stk, nd)
  }

  @Test
  public void testWalk2() {
    String strExpr = '0&1'
    AndOrExpr expr = new AndOrExpr(strExpr)
    println "Expr: $strExpr"
    AoeNode nd = expr.getNode()
    ExampleNodeTreeWalker ntw = new ExampleNodeTreeWalker();
    def m = [:]
    def stk = new LinkedList()
    def r = ntw.walk(m, stk, nd)
    println "r = $r"
  }

  @Test
  public void testWalk3() {
    String strExpr = '(0, !0) & 1'
    AndOrExpr expr = new AndOrExpr(strExpr)
    println "Expr: $strExpr"
    AoeNode nd = expr.getNode()
    ExampleNodeTreeWalker ntw = new ExampleNodeTreeWalker();
    def m = [:]
    def stk = new LinkedList()
    def r = ntw.walk(m, stk, nd)
    println "r = $r"
  }

}
