package fr.gpmsi.pmsi_rules

import fr.karadimas.pmsixml.FszGroup

/**
 * Moteur de regles tres simpliste pour l'instant que l'on peut appeler pour evaluation de
 * chaque RUM par exemple.
 *
 * Idees futures pour augmenter les possibilités :
 * - Ajouter une API pour avoir les autres RUMs d'un même RSS, ainsi que le parcours (UMs dans l'ordre chronologique)
 * - Ajouter une API pour avoir les autres RSS pour le même patient, avec requête sur l'ordre (suivant/précédent), et sur le temps (de l'année dernière, par ex.)
 * - Ajouter une API pour avoir les autres RSA pour le même patient, avec requête sur l'ordre (suivant/précédent), et sur le temps (de l'année dernière, par ex.)
 * - API pour collecter diagnostics, actes, sur un ensemble de RUM/RSS/RSA
 * L'idée étant que l'on donne un ensemble de RSS+VIDHOSP, ou bien un ensemble de RSA+TRA+VIDHOSP, et qu'ensuite
 * on parcourt les données à analyser, mais cette fois avec un contexte riche qui permet d'analyser l'antériorité
 * voire quelque chose qui s'est produit plus tard (par ex. le patient recoit des
 * chimios mais à aucun moment on n'a de DP de cancer).
 * A voir aussi : stockage de valeurs qui contrôlent telle ou telle évaluation
 * pour enlever les faux déclenchements (ex : on n'a pas trouvé de DP de cancer
 * parce que en fait le cancer a été traité à l'extérieur, on ne veut donc pas
 * qu'il y ait un avertissement à chaque fois)
 */
class PmsiRuleEngine {

    List<PmsiRule> rules = []
    def context = [:]

    PmsiRuleEngine() {
        context['engine'] = this
        context['out'] = new PrintWriter(System.out, true)
        context['collect'] = []
    }

    void add(PmsiRule rule) { rules << rule }

    /** initialiser toutes les regles */
    void initialize() {
        rules.each() { rule ->
            init(context)
        }
    }

    /**
     * a appeler si on evalue un RUM
     * @param g Le RUM/RSS a evaluer
     * @return Le nombre de regles qui ont ete evaluees a true
     */
    int evalRum(FszGroup g) {
        int ntrue = 0
        context['rum'] = g
        rules.each() { rule ->
            if (rule.eval(context)) {
              rule.action(context)
              ntrue++
            }
        }
        return ntrue;
    }

    /**
     * a appeler si on evalue un RSA
     * @param rsa Le RSA
     * @return Le nombre de regles qui ont ete evaluees a true
     */
    int evalRsa(FszGroup rsa) {
        int ntrue = 0
        context['rsa'] = rsa
        rules.each() { rule ->
            if (rule.eval(context)) {
              rule.action(context)
              ntrue++
            }
        }
        return ntrue;
    }

    Map getContext() { context }

}


