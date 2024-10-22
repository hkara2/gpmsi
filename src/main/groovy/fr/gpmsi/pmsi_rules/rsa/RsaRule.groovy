package fr.gpmsi.pmsi_rules.rsa
import fr.gpmsi.pmsi_rules.PmsiRule
import fr.gpmsi.pmsixml.FszGroup

/**
 * Règle qui est spécialisée pour les RSAs.
 * Etendre un objet et implementer init/eval/action
 */
class RsaRule
    implements PmsiRule 
{
  void init(HashMap context) {
  }
  
  boolean eval(HashMap context) {
      eval(context, context['rsa'])
  }
  
  boolean eval(HashMap context, FszGroup rum) {}

  void action(HashMap context) {
      action(context, context['rsa'])
  }
  
  void action(HashMap context, FszGroup rum) {
  }
}
