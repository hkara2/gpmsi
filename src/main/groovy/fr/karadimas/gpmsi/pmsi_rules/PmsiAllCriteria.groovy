package fr.karadimas.gpmsi.pmsi_rules

/**
 * Ensemble de criteres logiques PMSI (qui est lui-m�me un crit�re).
 * Est �valu� � "true" si tous les crit�res sont �valu�s � "true".
 * S'il n'y a aucun crit�re, est �valu� � "false".
 * Equivalent � "AND".
 * Evalue tous les crit�res dans l'ordre o� ils ont �t� ajout�s, et arr�te
 * l'�valuation d�s qu'un crit�re est �valu� � "false".
 */
class PmsiAllCriteria
    extends ArrayList<PmsiCriterion>
    implements PmsiCriterion 
{

    boolean eval(HashMap context) {
        if (size() == 0) return false
        for (int i = 0; i < size(); i++) {
            PmsiCriterion crit = get(i)
            if (crit.eval(context) == false) return false
        }
        //tous les criteres sont vrais, on evalue a "true".
        return true
    }

}
