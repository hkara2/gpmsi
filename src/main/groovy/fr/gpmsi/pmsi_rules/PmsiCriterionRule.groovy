package fr.gpmsi.pmsi_rules;

import java.util.HashMap;

import groovy.util.logging.Log4j2

/**
 * Classe de base de règle PMSI qui est juste construite sur un ou plusieurs critères PMSI,
 * qui doivent tous être vérifiés pour que la règle soit déclenchée.
 * Il faut implémenter "init" et "action".
 * @author hkaradimas
 */
@Log4j2
public abstract class PmsiCriterionRule
  implements PmsiRule 
{
  PmsiCriterion criterion
  
  public PmsiCriterionRule(PmsiCriterion... criteria) {
    this.criterion = new PmsiAllCriteria(criteria)
  }

  @Override
  public boolean eval(HashMap context) {
    return criterion.eval(context);
  }

  /**
   * Ici ne fait rien d'autre qu'envoyer un message
   * @param context Le contexte
   */
  @Override
  public void init(HashMap context) { 
      log.info("Appel a init, context : "+context)
  }

  /**
   * Ici ne fait rien d'autre qu'envoyer un message dans le log
   * @param context Le contexte
   */
  @Override
  public void action(HashMap context) { 
      log.info("Appel a action, context : "+context)
  }

}
