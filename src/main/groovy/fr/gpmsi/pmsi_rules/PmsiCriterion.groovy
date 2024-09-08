package fr.gpmsi.pmsi_rules

/**
 * Interface pour un critere PMSI qui sera evalue
 */
interface PmsiCriterion {
    boolean eval(HashMap context);
}
