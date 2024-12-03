package fr.gpmsi.pmsi_rules;

/**
 * Classe de base pour tous les noeuds impliqués dans une expression And-Or (AndOrExpr).
 * @author hkaradimas
 */
public abstract class AoeNode {
  
  void dump(String indent, StringBuilder sb) {}
  
  public String dump() {
    StringBuilder sb = new StringBuilder()
    dump("", sb)
    return sb.toString()
  }
  
}

