package fr.karadimas.gpmsi.pmsi_rules;

/**
 * And-Or expression node
 * @author hkaradimas
 *
 */
public class AoeNode {
  
  void dump(String indent, StringBuilder sb) {}
  
  public String dump() {
    StringBuilder sb = new StringBuilder()
    dump("", sb)
    return sb.toString()
  }
  
}

