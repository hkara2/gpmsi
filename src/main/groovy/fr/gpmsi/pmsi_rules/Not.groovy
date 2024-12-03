package fr.gpmsi.pmsi_rules;

/**
 * Noeud "Not" d'une AndOrExpr.
 */
public class Not
    extends AoeNode 
{
  AoeNode nd;
   
  void dump(String indent, StringBuilder sb) {
    sb.append("$indent(not:\n")
    nd.dump(indent+"  ", sb)
    sb.append("$indent)\n")
  }
  
  @Override
  public String toString() {
    return "(not: "+nd+")"
  }
}

