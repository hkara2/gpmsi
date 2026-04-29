//☺:encoding=UTF-8:
package fr.gpmsi.pmsi_rules.rss
import fr.gpmsi.pmsi_rules.PmsiRule
import fr.gpmsi.pmsixml.FszGroup

/**
 * Règle qui est spécialisée pour les RSS/RUMs.
 * Etendre un objet et implementer init/eval/action
 */
class RssRule
    implements PmsiRule 
{
  void init(Map context) {
  }

  boolean eval(Map context) { eval(context, null) }
    
  boolean eval(Map context, Map outputContext) {
      eval(context, outputContext, context['rum'])
  }

  boolean eval(Map context, FszGroup rum) { eval(context, null, rum) }
  
  boolean eval(Map context, Map outputContext, FszGroup rum) {}

  void action(Map context) { action(context, null) }
  
  void action(Map context, Map outputContext) {
      action(context, outputContext, context['rum'])
  }

  void action(Map context, FszGroup rum) { action(context, null, rum) }
  
  void action(Map context, Map outputContext, FszGroup rum) {
  }
}
