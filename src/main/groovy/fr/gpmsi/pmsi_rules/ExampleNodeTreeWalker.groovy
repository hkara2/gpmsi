package fr.gpmsi.pmsi_rules;

/**
 * Exemple d'évaluation d'arbre ; ici prend "0" pour false, tout le reste pour true, 
 * et on évalue les opérations booléennes en parcourant un arbre à partir d'un noeud And, Or, Not ou Val.
 * <p>
 * (Réservé pour usages futurs)
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
  
  public Object walk(String indent, Map ctx, Deque stk, And nd) {
    println "${indent}walk and nd:$nd"
    boolean v1 = eval(walk(indent+"  ", ctx, stk, nd.left))
    println "${indent}result of and (v1:$v1)"
    boolean v2 = eval(walk(indent+"  ", ctx, stk, nd.right))
    println "${indent}result of and (v2:$v2)"
    return toStr(v1 && v2)
  }
  
  public Object walk(String indent, Map ctx, Deque stk, Or nd) {
    println "${indent}walk or nd:$nd"
    boolean v1 = eval(walk(indent+"  ", ctx, stk, nd.left))
    println "${indent}result of or (v1:$v1)"
    boolean v2 = eval(walk(indent+"  ", ctx, stk, nd.right))
    println "${indent}result of or (v2:$v2)"
    return toStr(v1 || v2)
  }
  
  public Object walk(String indent, Map ctx, Deque stk, Not nd) {
    println "${indent}walk not nd:$nd"
    boolean v1 = eval(walk(indent+"  ", ctx, stk, nd.nd))
    println "${indent}result of not (v1:$v1)"
    return toStr(!v1)
  }
  
  public Object walk(String indent, Map ctx, Deque stk, Val nd) {
    println "${indent}Walk val nd:$nd"
    boolean v1 = eval(nd.val)
    println "${indent}result of val (v1:$v1)"
    return toStr(v1)
  }
  
}
