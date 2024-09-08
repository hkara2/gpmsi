package fr.gpmsi.pmsi_rules.ghm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern

import fr.gpmsi.StringUtils
import fr.gpmsi.pmsi_rules.PmsiCriterion;
import fr.karadimas.pmsixml.FszField
import fr.karadimas.pmsixml.FszGroup

/**
 * Recherche la presence d'un motif dans le GHM d'un RSA.
 * Par défaut on recherche le GHM obtenu par groupage GENRSA, mais on
 * peut aussi utiliser celui qui a été lu.
 */
public class GhmRsaCodePresence
implements PmsiCriterion
{
  public static final int LOC_GENRSA = 1
  public static final int LOC_LU = 2
  
  static Pattern ghmPattern =  Pattern.compile(/[0-9][0-9][A-Z][0-9][0-9][0-9A-Z]/) //pattern pour un code GHM autorisé
  
  HashSet<String> codes = new HashSet<>();
  HashSet<Pattern> codeExprs = null;
  int location; //où lire le GHM : "lu" ou "GENRSA"

  /**
   * Rechercher la presence du code. La localisation du code est "GENRSA" par defaut.
   * @param code
   */
  public GhmRsaCodePresence(String... code) {
    this(LOC_GENRSA, code)
  }
  
  public GhmRsaCodePresence(int location, String... codeColl) {
    codes.clear()
    codeColl.each {code->
      codes << StringUtils.normalizeCode(code)
    }
    this.location = location;
    //System.out.println("codeList:$codeColl")
  }

  @Override
  public boolean eval(HashMap context) {
    FszGroup rsa = context['rsa']
    
    if (codeExprs == null) {
      //initialiser les expressions de recherche
      codeExprs = new HashSet<Pattern>()
      codes.removeAll { code ->
          def isRegular = code ==~ ghmPattern //true is code est un code GHM "normal"
          //System.out.println("code:$code,isRegular:$isRegular")
          if (!isRegular) codeExprs << Pattern.compile(code) //si code est une regexp, on la compile
          !isRegular //si code est une regexp elle sera enlevee des codes normaux
      }
    }
    def ghm = null
    if (location == LOC_LU) {
      def glcmd = rsa.getChildField('GLCMD').value
      def gltyp = rsa.getChildField('GLTYP').value
      def glnum = rsa.getChildField('GLNUM').value
      def glcpx = rsa.getChildField('GLCPX').value
      ghm = glcmd+gltyp+glnum+glcpx
    }
    else if (location == LOC_GENRSA) {
      def grcmd = rsa.getChildField('GRCMD').value
      def grtyp = rsa.getChildField('GRTYP').value
      def grnum = rsa.getChildField('GRNUM').value
      def grcpx = rsa.getChildField('GRCPX').value
      ghm = grcmd+grtyp+grnum+grcpx
    }
    else {
      throw new Exception("Type de localisation non supporté : $location")
    }
    ghm = StringUtils.normalizeCode(ghm) //par sécurité
    if (codes.contains(ghm)) return true
    //boolean r = codeExprs.any { ghm =~ it }
    //System.err.println("ghm:$ghm, r:$r")
    return codeExprs.any { ghm =~ it }
  }

}
