/**:encoding=UTF-8: */
package fr.gpmsi.pmsi_rules

/**
 * Negation du sous-critere logique PMSI.
 * Est évalué à "true" si le sous-critère est évalué à "false", et inversement.
 */
class PmsiNotCriterion implements PmsiCriterion {
    PmsiCriterion subcriterion
    
    PmsiNotCriterion(PmsiCriterion subcriterion) {
        this.subcriterion = subcriterion
    }
    
    boolean eval(HashMap context) {
        return !subcriterion.eval(context)
    }
}