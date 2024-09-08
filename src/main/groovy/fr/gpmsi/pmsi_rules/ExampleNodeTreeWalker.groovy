package fr.gpmsi.pmsi_rules;

/**
 * Exemple d'évaluation d'arbre ; ici prend "0" pour false, tout le reste pour true, et évalue les opérations booléennes.
 * @author hkaradimas
 *
 */
public class ExampleNodeTreeWalker {

  public ExampleNodeTreeWalker() {
    // TODO Auto-generated constructor stub
  }

  //'0' est false, tout le reste est true
  private boolean eval(Object o) {
    def v = String.valueOf(o) != '0'
    //println "eval('$o'):$v"
    return v
  }
  
  private final String toStr(boolean b) { b ? '1' : '0' }
  
  public Object walk(Map ctx, Deque stk, And nd) {
    println "walk and nd:$nd"
    boolean v1 = eval(walk(ctx, stk, nd.left))
    println "walk and v1:$v1"
    boolean v2 = eval(walk(ctx, stk, nd.right))
    println "walk and v2:$v2"
    return toStr(v1 && v2)
  }
  
  public Object walk(Map ctx, Deque stk, Or nd) {
    println "walk or nd:$nd"
    boolean v1 = eval(walk(ctx, stk, nd.left))
    println "walk or v1:$v1"
    boolean v2 = eval(walk(ctx, stk, nd.right))
    println "walk or v2:$v2"
    return toStr(v1 || v2)
  }
  
  public Object walk(Map ctx, Deque stk, Not nd) {
    println "walk not nd:$nd"
    boolean v1 = eval(walk(ctx, stk, nd.nd))
    println "walk not v1:$v1"
    return toStr(!v1)
  }
  
  public Object walk(Map ctx, Deque stk, Val nd) {
    println "Walk val nd:$nd"
    boolean v1 = eval(nd.val)
    println "walk val v1:$v1"
    return toStr(v1)
  }
  
}
