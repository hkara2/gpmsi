package fr.karadimas.gpmsi.pmsi_rules

import fr.karadimas.pmsixml.FszGroup

/**
 * Moteur de regles tres simpliste que l'on peut appeler pour evaluation de
 * chaque RUM par exemple
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


