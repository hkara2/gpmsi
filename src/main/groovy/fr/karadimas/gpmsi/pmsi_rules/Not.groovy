package fr.karadimas.gpmsi.pmsi_rules;

public class Not
    extends AoeNode 
{
  AoeNode nd;
   
  void dump(String indent, StringBuilder sb) {
    sb.append("$indent(not:\n")
    nd.dump(indent+"  ", sb)
    sb.append("$indent)\n")
  }
}

