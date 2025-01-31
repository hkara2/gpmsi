package fr.gpmsi.pmsi_rules

import fr.gpmsi.pmsixml.FszGroup

/**
 * Moteur de regles tres simpliste pour l'instant que l'on peut appeler pour évaluation de
 * chaque RUM par exemple.<br>
 * On initialise le moteur avec toutes les règles que l'on désire utiliser.<br>
 * On appelle initialize(), ce qui initialise le moteur.<br>
 * Ensuite pour chaque élément que l'on veut évaluer avec le moteur de règles, on appelle la
 * méthode correspondante, par ex. <code>evalRum(rum)</code>.
 * <p>
 *
 * Idees futures pour augmenter les possibilités :
 * <ul>
 *   <li> Ajouter une API pour avoir les autres RUMs d'un même RSS, ainsi que le parcours (UMs dans l'ordre chronologique)
 *   <li> Ajouter une API pour avoir les autres RSS pour le même patient, avec requête sur l'ordre (suivant/précédent), et sur le temps (de l'année dernière, par ex.)
 *   <li> Ajouter une API pour avoir les autres RSA pour le même patient, avec requête sur l'ordre (suivant/précédent), et sur le temps (de l'année dernière, par ex.)
 *   <li> API pour collecter diagnostics, actes, sur un ensemble de RUM/RSS/RSA
 * </ul>
 * L'idée étant que l'on donne un ensemble de RSS+VIDHOSP, ou bien un ensemble de RSA+TRA+VIDHOSP, et qu'ensuite
 * on parcourt les données à analyser, mais cette fois avec un contexte riche qui permet d'analyser l'antériorité
 * voire quelque chose qui s'est produit plus tard (par ex. le patient recoit des
 * chimios mais à aucun moment on n'a de DP de cancer).
 * <p>
 * A voir aussi : stockage de valeurs qui contrôlent telle ou telle évaluation
 * pour enlever les faux déclenchements (ex : on n'a pas trouvé de DP de cancer
 * parce que en fait le cancer a été traité à l'extérieur, on ne veut donc pas
 * qu'il y ait un avertissement à chaque fois)
 * <p>
 * Exemple (groovy) de script utilisant un PmsiRuleEngine et des critères :
 * <pre>
 * //recherche de rums cancéro dans l'UF de médecine, sortis entre septembre et décembre 2024 
 * package pmsi_rules
 * import java.time.LocalDate
 * import fr.gpmsi.DateUtils
 * import fr.gpmsi.pmsi_rules.*
 * import fr.gpmsi.pmsi_rules.cim.*
 * import fr.gpmsi.pmsi_rules.ccam.*
 * import fr.gpmsi.pmsi_rules.ghm.*
 * import fr.gpmsi.pmsi_rules.rss.*
 * 
 * ufsAutorisees = ['4001'] as Set
 * 
 * dateSorMin = LocalDate.of(2024,  9,  1)
 * dateSorMax = LocalDate.of(2024, 12, 31)
 * 
 * critUfAutorisee = new GenericPmsiCriterion({context ->
 *     def rum = context['rum']
 *     ufsAutorisees.contains(rum.txtNUM)
 * })
 * 
 * //critère dateSorMin <= dateSor <= dateSorMax
 * critDateSorOk  = new GenericPmsiCriterion({context ->
 *     def rum = context['rum']
 *     def dateSor = rum.DSUM.toLocalDate()
 *     dateSor != null && dateSorMin <= dateSor && dateSor <= dateSorMax
 * })
 * 
 * cimCancers = ("C00:D09").split(',') //liste des expressions de codes de cancer
 * 
 * critCimDansSelection = new CimCodePresence("DP,DR,DAS", cimCancers) //est-ce que le code CIM du rsa est dans la selection ?
 * 
 * //règle pour voir si cim dans la sélection et uf est autorisee et date de sortie dans la plage autorisée
 * regleCimDansSelection = new PmsiCriterionRule(critCimDansSelection, critUfAutorisee, critDateSorOk)
 * 
 * eng = new PmsiRuleEngine(regleCimDansSelection) //moteur de règles basé sur cette règle
 * 
 * dateSorParNadl = [:]
 * 
 * nadls = [] as Set
 * 
 * / ** remplir dates de sortie par nadl * /
 * rss {
 *     input args.input
 *     onItem {item ->
 *         rum = item.rum
 *         def dateSor = rum.DSUM.toLocalDate()
 *         def nadl = rum.txtNADL
 *         def oldDateSor = dateSorParNadl[nadl]
 *         if (dateSor != null && (oldDateSor == null || oldDateSor.isBefore(dateSor))) {
 *             dateSorParNadl[nadl] = dateSor
 *         }
 *     }
 * }
 * 
 * rss {
 *     input args.input
 *     output args.output
 * 
 *     onInit {
 *         outf = new FileWriter(outputFilePath)
 *         outf << 'NADL\r\n'
 *     }
 * 
 *     onItem {item->
 *         rum = item.rum
 *         int n = eng.evalRum(rum)
 *         def nadl = rum.txtNADL
 *         if (n > 0) outf << "$nadl\r\n"
 *     }
 * 
 *     onEnd {
 *         outf.close()
 *     }
 * }
 *
 * </pre>
 */
class PmsiRuleEngine {

    List<PmsiRule> rules = []
    def context = [:]

    /**
     * Constructeur simple.
     */
    PmsiRuleEngine() {
        context['engine'] = this
        context['out'] = new PrintWriter(System.out, true)
        context['collect'] = []
        rules?.each { rule -> add(rule) }      
    }

    /**
     * Constructeur, permet d'ajouter des règles dès la construction.
     * On peut ajouter plus de règles par la suite à l'aide de #add(PmsiRule)
     * @param rules 0 ou plus règles à ajouter
     */
    PmsiRuleEngine(PmsiRule... rules) {
        context['engine'] = this
        context['out'] = new PrintWriter(System.out, true)
        context['collect'] = []
        rules?.each { rule -> add(rule) }      
    }

    void add(PmsiRule rule) { rules << rule }

    /**
     * initialiser toutes les règles
     */
    void initialize() {
        rules.each() { rule ->
            init(context)
        }
    }

    /**
     * à appeler si on evalue un RUM
     * @param g Le RUM/RSS a évaluer
     * @return Le nombre de règles qui ont été évaluées a true
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
     * à appeler si on évalue un RSA
     * @param rsa Le RSA
     * @return Le nombre de règles qui ont été évaluées a true
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

    /**
     * Retourner le contexte du moteur
     * @return Le contexte
     */
    Map getContext() { context }

}


