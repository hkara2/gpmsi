package fr.gpmsi.pmsi_rules

/**
 * Interface pour un critère PMSI qui sera évalué.
 * Tous les critères implémentent cette interface.
 */
interface PmsiCriterion {
    /**
     * Evaluation du critère
     * @param context Une map qui donne des critères qui pourront être à disposition de la méthode d'évaluation.
     * @param outputContext Une map dans laquelle l'évaluateur peut écrire des résultats intermédiaires, des messages, des informations
     * @return un booléen true ou false, résultat de l'évaluation.
     */
    boolean eval(Map context, Map outputContext);
}
