package fr.karadimas.gpmsi.pmsi_rules

/**
 * Ensemble de critères logiques PMSI (qui est lui-même un critère).
 * Est évalué à "true" si tous les critères sont évalués à "true".
 * S'il n'y a aucun critère, est évalué à "false".
 * Equivalent à "AND".
 * Evalue tous les critères dans l'ordre où ils ont été ajoutés, et arrête
 * l'évaluation dès qu'un critère est évalué à "false".
 */
class PmsiAllCriteria
    extends ArrayList<PmsiCriterion>
    implements PmsiCriterion 
{
    public PmsiAllCriteria(PmsiCriterion ... criteria) {
        addAll(criteria)
    }

    boolean eval(HashMap context) {
        if (size() == 0) return false
        for (int i = 0; i < size(); i++) {
            PmsiCriterion crit = get(i)
            if (crit.eval(context) == false) return false
        }
        //tous les critères sont vrais, on évalue a "true".
        return true
    }

}
