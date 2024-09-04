package fr.karadimas.gpmsi.pmsi_rules

/**
 * Ensemble de critères logiques PMSI (qui est lui-même un critère).
 * Est évalué à "true" si au moins un des critères est évalué à "true".
 * Si tous les critères sont faux, est évalué à "false".
 * Est équivalent à "OR".
 * Evalue tous les critères dans l'ordre où ils ont été ajoutés, et arrête
 * l'évaluation dès qu'un critère est évalué à "true".
 */
class PmsiAnyCriterion 
    extends ArrayList<PmsiCriterion> 
    implements PmsiCriterion 
{
    public PmsiAnyCriterion(PmsiCriterion ... criteria) {
        addAll(criteria)
    }

    boolean eval(HashMap context) {
        if (size() == 0) return false
        for (int i = 0; i < size(); i++) {
            PmsiCriterion crit = get(i)
            if (crit.eval(context) == true) return true
        }
        //aucun critère n'est vrai, le résultat de l'évaluation est "false"
        return false
    }

}