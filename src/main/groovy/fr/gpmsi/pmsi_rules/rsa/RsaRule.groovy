package fr.gpmsi.pmsi_rules.rsa
import groovy.lang.Closure;
import fr.gpmsi.pmsixml.FszGroup
import fr.gpmsi.pmsi_rules.PmsiRule
import fr.gpmsi.pmsi_rules.GenericPmsiCriterion
import fr.gpmsi.pmsi_rules.PmsiCriterion

/**
 * Règle qui est spécialisée pour les RSAs.
 * Etendre un objet et implementer init/eval/action
 * Ou sinon utiliser le constructeur qui prend une Closure en argument
 */
class RsaRule
    implements PmsiRule 
{
  PmsiCriterion crit
  
  public RsaRule() {}
  
  public RsaRule(Closure c) { crit = new GenericPmsiCriterion(c) }
  
  void init(HashMap context) {
  }
  
  boolean eval(HashMap context) {
      eval(context, context['rsa'])
  }
  
  boolean eval(HashMap context, FszGroup rsa) {
      if (crit) return crit.eval(context)
      else return false
  }

  void action(HashMap context) {
      action(context, context['rsa'])
  }
  
  void action(HashMap context, FszGroup rsa) {
  }
}
