package fr.karadimas.gpmsi.pmsi_rules

/**
 * Ensemble de criteres logiques PMSI (qui est lui-m�me un crit�re).
 * Est �valu� � "true" si au moins un des crit�res est �valu� � "true".
 * Si tous les crit�res sont faux, est �valu� � "false".
 * Est �quivalent � "OR".
 * Evalue tous les crit�res dans l'ordre o� ils ont �t� ajout�s, et arr�te
 * l'�valuation d�s qu'un crit�re est �valu� � "true".
 */
class PmsiAnyCriterion 
    extends ArrayList<PmsiCriterion> 
    implements PmsiCriterion 
{

    boolean eval(HashMap context) {
        if (size() == 0) return false
        for (int i = 0; i < size(); i++) {
            PmsiCriterion crit = get(i)
            if (crit.eval(context) == true) return true
        }
        //aucun crit�re n'est vrai, le r�sultat de l'�valuation est "false"
        return false
    }

}