package fr.gpmsi.pmsi_rules;

/**
 * Noeud "valeur" d'une AndOrExpr. La valeur est représentée par une String.
 */
public class Val extends AoeNode { 
  String val 
  void dump(String indent, StringBuilder sb) { sb.append("$indent(v:$val)\n") }
  
  @Override
  public String toString() {
      return ""+val
  }
  
}
  
