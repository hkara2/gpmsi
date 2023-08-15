package fr.karadimas.gpmsi.pmsi_rules

/**
 * Negation du sous-critere logique PMSI.
 * Est �valu� � "true" si au moins un des crit�res est �valu� � "true".
 * Si tous les crit�res sont faux, est �valu� � "false".
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