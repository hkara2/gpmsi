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

  void init(Map context) {
  }

  boolean eval(Map context) { eval(context, null) }
  
  boolean eval(Map context, Map outputContext) {
      eval(context, outputContext, context['rsa'])
  }

  boolean eval(Map context, FszGroup rsa) { eval(context, null, rsa) }
      
  boolean eval(Map context, Map outputContext, FszGroup rsa) {
      if (crit) return crit.eval(context, outputContext)
      else return false
  }

  void action(Map context) { action(context, null) }
  
  void action(Map context, Map outputContext) {
      action(context, outputContext, context['rsa'])
  }

  void action(Map context, FszGroup rsa) { action(context, null, rsa) }
  
  void action(Map context, Map outputContext, FszGroup rsa) {
  }
}
