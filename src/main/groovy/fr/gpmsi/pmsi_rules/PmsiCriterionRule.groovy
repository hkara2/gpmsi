package fr.gpmsi.pmsi_rules;

import java.util.HashMap;

import groovy.util.logging.Log4j2

/**
 * Classe de base de règle PMSI qui est juste construite sur un ou plusieurs critères PMSI,
 * qui doivent tous être vérifiés pour que la règle soit déclenchée.
 * Si on dérive cette classe, on peut étendre "init" et "action" pour des initialisations particulières
 * ou une action à exécuter si eval renvoie "true".
 * @author hkaradimas
 */
@Log4j2
public class PmsiCriterionRule
  implements PmsiRule 
{
  PmsiAllCriteria criteria
  
  public PmsiCriterionRule(PmsiCriterion... criteria) {
    this.criteria = new PmsiAllCriteria(criteria)
  }

  /**
   * Ramène le résultat de l'évaluation des critères
   * @return un boolean résultant de l'évaluation
   */
  @Override
  public boolean eval(HashMap context) {
    return criteria.eval(context);
  }

  /**
   * Ici ne fait rien d'autre qu'envoyer un message de débogage dans le log.
   * Etendre cette méthode dans une sous-classe pour faire des initialisations supplémentaires
   * @param context Le contexte
   */
  @Override
  public void init(HashMap context) { 
      log.debug("Appel a init, context : "+context)
  }

  /**
   * Ici ne fait rien d'autre qu'envoyer un message de débogage dans le log.
   * Etendre cette méthode dans une sous-classe pour faire des initialisations supplémentaires.
   * @param context Le contexte
   */
  @Override
  public void action(HashMap context) { 
      log.debug("Appel a action, context : "+context)
  }

  /**
   * Renvoie les critères de la règle
   * @return les critères sous forme d'un objet PmsiAllCriteria
   */
  public PmsiAllCriteria getCriteria() {
    return criteria;
  }
}
