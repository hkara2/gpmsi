package fr.gpmsi.pmsi_rules.ghm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern

import fr.gpmsi.StringUtils
import fr.gpmsi.pmsi_rules.PmsiCriterion;
import fr.karadimas.pmsixml.FszField
import fr.karadimas.pmsixml.FszGroup

public class GhmCodePresence
implements PmsiCriterion
{
  static Pattern ghmPattern =  Pattern.compile(/[0-9][0-9][A-Z][0-9][0-9][0-9A-Z]/) //pattern pour un code GHM autorisé
  
  HashSet<String> codes = new HashSet<>();
  HashSet<Pattern> codeExprs = null;

  public GhmCodePresence(String code) {
    this([code])
  }
  
  public GhmCodePresence(Collection<String> codeColl) {
    codes.clear()
    codeColl.each {code->
      codes << StringUtils.normalizeCode(code)
    }
    //System.out.println("codeList:$codeColl")
  }

  @Override
  public boolean eval(HashMap context) {
    FszGroup rum = context['rum']
    
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
    String ncmd = rum.getChildField('NCMD').value //2 caracteres
    String nghm = rum.getChildField('NGHM').value //4 caracteres
    String ghm = StringUtils.normalizeCode(ncmd+nghm)
    if (codes.contains(ghm)) return true
    //boolean r = codeExprs.any { ghm =~ it }
    //System.err.println("ghm:$ghm, r:$r")
    return codeExprs.any { ghm =~ it }
  }

}
