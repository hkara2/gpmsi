package fr.gpmsi.pmsi_rules;

/**
 * And-Or expression node (abstract class)
 * @author hkaradimas
 *
 */
public abstract class AoeNode {
  
  void dump(String indent, StringBuilder sb) {}
  
  public String dump() {
    StringBuilder sb = new StringBuilder()
    dump("", sb)
    return sb.toString()
  }
  
}

