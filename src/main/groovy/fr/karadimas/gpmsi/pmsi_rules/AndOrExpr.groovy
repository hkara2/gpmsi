package fr.karadimas.gpmsi.pmsi_rules;

/**
 * Expression And et Or génériques.
 * Le caractère pour "Or" est la virgule.
 * Le caractère pour "And" est la perluette "&".
 * Le caractère de négation est le point d'exclamation "!".
 * Les autres caractères sont juste ajoutés en valeur.
 * Les espaces sont ignorés.
 * Après analyse, on a un arbre And/Or avec la racine AndOrExpr.Node en résultat
 * Par exemple, 
 * @author hkaradimas
 *
 */
public class AndOrExpr {
  
  public AndOrExpr(String src)
      throws ParseException 
  {
    node = startParse(src)
  }

  private AoeNode node //the equivalent node of the expression
  private char cc //current char
  private int sp //source pos
  private int len //length of source 
  private CharSequence src //source to analyze
  
  public AoeNode getNode() { return node }
  
  private AoeNode startParse(String src)
  throws ParseException 
  {
    this.src = src
    this.len = src.length()
    this.sp = 0
    next(); skipWs()
    node = parse()
    //verification qu'on est bien à la fin, sinon c'est une erreur
    if (cc != 0) throw new ParseException("Erreur de syntaxe en position "+sp)
    return node
  }
  
  private final boolean isWs(char c) { c == ' ' || c == '\t' }
  
  private final void skipWs() { while (isWs(cc)) next() }
  
  /* 
   * un caractere de valeur est tout caractere (y compris un espace) qui n'est pas zéro,
   * et pas !,&()
   */
  private boolean isValChar(char c) {
    c != 0 && c != ',' && c!= '&' && c!= '!' && c != '(' && c != ')'
  }
  
  /** Retourner caractere suivant, retourner 0 s'il n'y a plus de caractère suivant */
  private char next() {
    if (sp >= len) cc = 0 else cc = src.charAt(sp++)
    return cc
  }
  
  private AoeNode parse()
  throws ParseException 
  {
    AoeNode nd = parseOr()
    return nd
  }
  
  private AoeNode parseOr() {
    AoeNode nd = parseAnd()
    while (cc == ',') {
      next(); skipWs()
      if (cc == 0) throw new ParseException("Erreur valeur manquante après ',' à la position "+sp)
      AoeNode right = parseAnd()
      nd = new Or(left: nd, right: right)
    }
    return nd
  }
  
  private AoeNode parseAnd() {
    AoeNode nd = parseNot()
    while (cc == '&') {
      next(); skipWs()
      if (cc == 0) throw new ParseException("Erreur valeur manquante après '&' à la position "+sp)
      AoeNode right = parseNot()
      nd = new And(left: nd, right: right)
    }
    return nd
  }
  
  private AoeNode parseNot() {
    AoeNode nd
    if (cc == '!') {
      next(); skipWs()
      if (cc == 0) throw new ParseException("Erreur valeur manquante après '!' à la position "+sp)
      nd = parseTerm()
      nd = new Not(nd: nd) 
    }
    else {
      nd = parseTerm()
    }
    return nd
  }
  
  private AoeNode parseTerm()
  throws ParseException 
  {
    //sauter les blancs initiaux
    skipWs()
    if (cc == '(') {
      next(); skipWs()
      AoeNode subnode = parse()
      if (cc != ')') throw new ParseException("Absence de parenthèse fermante en position "+sp)
      next(); skipWs()
      return subnode
    }
    else {
      StringBuilder sb = new StringBuilder()
      boolean esc = false;
      while (isValChar(cc)) {
        if (cc == '\\') {
          //mécanisme d'échappement pour entrer dans un terme les caractères spéciaux, ex : 3\,3&5 : on a "3,3" et "5"
          next()
          if (cc != 0) sb.append(cc)
        }
        else {
          sb.append(cc) //ajout normal du caractère de terme
        }
        next()
      }
      if (sb.length() == 0) throw new ParseException("Valeur vide en position "+sp)
      AoeNode valNode = new Val(val: sb.toString().trim())
      return valNode
    }
  }
  
  public void dump(StringBuilder sb) {
    node.dump("", sb)
  }
  
  public String dump() {
    StringBuilder sb = new StringBuilder()
    node.dump("", sb)
    return sb.toString()
  }
  
}
