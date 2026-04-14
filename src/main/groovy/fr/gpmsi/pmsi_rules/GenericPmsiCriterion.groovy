package fr.gpmsi.pmsi_rules;

import java.util.HashMap;

import groovy.lang.Closure;

/**
 * Critère PMSI générique. L'évaluation est déléguée à une <i>closure</i> de code utilisateur qui devra 
 * retourner true ou false (sinon une exception sera lancée !).
 * On peut fournir une closure avec juste un paramètre contexte, ou une
 * closure avec deux arguments (context et outputContext).
 * L'argument passé à la closure sera un objet context de type Map, et
 * un objet outputContext de type Map également.
 */
public class GenericPmsiCriterion
    implements PmsiCriterion 
{
  Closure closureToEval
  int closureParamCount

  /**
   * Constructeur avec la <i>closure</i> fournie
   * @param closureToEval La <i>closure</i> qui sera évaluée à chaque fois.
   *     Elle peut avoir 1 ou 2 arguments (context ou context, outputContext)
   */
  public GenericPmsiCriterion(Closure closureToEval) {
    this.closureToEval = closureToEval;
    if (closureToEval == null) closureParamCount = 0
    else closureParamCount = closureToEval.maximumNumberOfParameters
  }

  /**
   * @see PmsiCriterion
   * @param context Contexte dans lequel rechercher
   * @param outputContext Contexte dans lequel la closure va pouvoir écrire
   * @return un booléen résultat de l'évaluation
   */
  @Override
  public boolean eval(Map context, Map outputContext) {
    if (closureParamCount == 1) return closureToEval.call(context)
    else return closureToEval.call(context, outputContext);
  }

}
