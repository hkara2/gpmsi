package fr.gpmsi.pmsi_rules;

/**
 * Noeud "And" d'une AndOrExpr.
 * C'est un noeud qui a 2 enfants.
 */
public class And extends AoeNode { 
  AoeNode left
  AoeNode right 
  void dump(String indent, StringBuilder sb) {
    sb.append("$indent(and:\n")
    left.dump(indent+"  ", sb)
    right.dump(indent+"  ", sb) 
    sb.append("$indent)\n")
  }
  @Override
  public String toString() {
      return "(and:"+String.valueOf(left)+","+String.valueOf(right)+")"
  }
}

