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
  void init(HashMap context) {
  }

  boolean eval(HashMap context) {
      eval(context, context['rum'])
  }

  boolean eval(HashMap context, FszGroup rum) {}

  void action(HashMap context) {
      action(context, context['rum'])
  }

  void action(HashMap context, FszGroup rum) {
  }
}
