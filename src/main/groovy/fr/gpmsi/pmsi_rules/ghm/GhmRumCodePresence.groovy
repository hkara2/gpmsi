//☺:encoding=UTF-8: ATTENTION LIRE AVERTISSEMENT !
package fr.gpmsi.pmsi_rules.ghm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern

import fr.gpmsi.StringUtils
import fr.gpmsi.pmsi_rules.PmsiCriterion;
import fr.gpmsi.pmsixml.FszField
import fr.gpmsi.pmsixml.FszGroup

/**
 * AVERTISSEMENT ! A utiliser uniquement en attente de la production d'un RSA.
 * En effet le DP retenu en cas de multiRUMs peut ne pas être le bon, de même
 * que les actes peuvent être sur un autre RUM que le DP qui est correct.
 * Il arrive donc que cette règle échoue pour du multirums.
 *
 * Recherche la presence de codes ou de motifs dans le GHM d'un RUM.
 * On peut rechercher un GHM normal ou bien une expression régulière
 */
public class GhmRumCodePresence
implements PmsiCriterion
{
  static Pattern ghmPattern =  Pattern.compile(/[0-9][0-9][A-Z][0-9][0-9][0-9A-Z]/) //pattern pour un code GHM autorisé

  HashSet<String> codes = new HashSet<>();
  HashSet<Pattern> codeExprs = null;

  /**
   * Rechercher la presence du code.
   * @param code
   */
  public GhmRumCodePresence(String... codeColl) {
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
    def ncmd = rum.getChildField('NCMD').value //c'est comme ça dans la doc GENRSA
    def nghm = rum.getChildField('NGHM').value //c'est comme ça dans la doc GENRSA
    def ghm = ncmd+nghm
    ghm = StringUtils.normalizeCode(ghm) //par sécurité
    if (codes.contains(ghm)) return true
    //boolean r = codeExprs.any { ghm =~ it }
    //System.err.println("ghm:$ghm, r:$r")
    return codeExprs.any { ghm =~ it }
  }

}
