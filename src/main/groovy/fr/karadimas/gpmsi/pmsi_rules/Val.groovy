package fr.karadimas.gpmsi.pmsi_rules;

public class Val extends AoeNode { 
  String val 
  void dump(String indent, StringBuilder sb) { sb.append("$indent(v:$val)\n")}
}
  
