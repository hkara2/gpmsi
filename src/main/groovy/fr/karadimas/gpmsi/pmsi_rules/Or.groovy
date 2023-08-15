package fr.karadimas.gpmsi.pmsi_rules;

public class Or extends AoeNode { 
  AoeNode left 
  AoeNode right 
  void dump(String indent, StringBuilder sb) {
    sb.append("$indent(or:\n")
    left.dump(indent+"  ", sb)
    right.dump(indent+"  ", sb) 
    sb.append("$indent)\n")
  }
}


