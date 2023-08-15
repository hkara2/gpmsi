package fr.karadimas.gpmsi.pmsi_rules.rsa
import fr.karadimas.gpmsi.pmsi_rules.PmsiRule
import fr.karadimas.pmsixml.FszGroup

/**
 * Règle qui est spécialisée pour les RSAs.
 * Etendre un objet et implementer init/evaluate/action
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
