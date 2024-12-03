package fr.gpmsi.pmsi_rules;

import java.util.HashMap;

import groovy.lang.Closure;

/**
 * Critère PMSI générique. L'évaluation est déléguée à une <i>closure</i> de code utilisateur qui devra 
 * retourner true ou false (sinon une exception sera lancée !)
 */
public class GenericPmsiCriterion
    implements PmsiCriterion 
{
  Closure closureToEval;

  /**
   * Constructeur avec la <i>closure</i> fournie
   * @param closureToEval La <i>closure</i> qui sera évaluée à chaque fois
   */
  public GenericPmsiCriterion(Closure closureToEval) {
    this.closureToEval = closureToEval;
  }

  /**
   * @see PmsiCriterion
   * @param context Contexte dans lequel rechercher
   * @return un booléen résultat de l'évaluation
   */
  @Override
  public boolean eval(HashMap context) {
    return closureToEval.call(context);
  }

}
