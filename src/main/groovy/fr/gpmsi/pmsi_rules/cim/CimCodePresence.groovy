package fr.gpmsi.pmsi_rules.cim
import fr.gpmsi.cim.SharedCim10
import fr.gpmsi.pmsi_rules.PmsiCriterion
import fr.gpmsi.pmsixml.FszGroup
import java.util.regex.Pattern

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Recherche de codes cim 10 dans différents endroits, avec diverses syntaxes de recherche.
 * Gere les codes CIM-10 OMS avec extensions proposées par l'ATIH, avec une longueur de 6 caractères maximum ( 1 x (lettre) + 5 x (chiffre ou caractère '+') )
 * <br>
 * Les endroits à rechercher sont :
 * <ul>
 * <li>DP : le DP qui est dans "rum"
 * <li>DR : le DR qui est dans "rum"
 * <li>DAS : les DASs qui sont dans "rum" ou "rhs"
 * <li>DAD : les DADs qui sont dans "rum" ou "rhs"
 * <li>FPP : la FPP qui est dans "rhs"
 * <li>AE : l'AE qui est dans "rhs"
 * <li>MMP : la MMP qui est dans "rhs"
 * <li>DPA : le DP qui est dans "rsa"
 * <li>DRA : le DR qui est dans "rsa"
 * </ul>
 * Par exemple si on veut rechercher dans DR ou DAS on utilisera "DR,DAS".
 * <br>
 * <p>
 * Il y a deux modes de recherche : par intervalle ou par liste de codes.
 * <br>
 * Recherche par intervalle : il faut utiliser le constructeur  #CimCodePresence(String locations, String firstCode, String lastCode, boolean includeLast) ou le constructeur CimCodePresence(String locations, String firstCode, String lastCode) :
 * ce mode de recherche est déconseillé puisque maintenant on peut mettre des intervalles de code dans la liste des codes à rechercher. 
 * <br>
 * Recherche par liste de codes : il faut utiliser le constructeur CimCodePresence(String locations, String[] codeList) ou le constructeur CimCodePresence(String locations, String uniqueCode)  
 * <br>
 * Le ou les codes CIM 10 collectés dans les endroits à rechercher seront normalisés avant d'être analysés :
 * <ul>
 * <li>Les espaces de début et de fin sont enlevés
 * <li>Les points ('.') sont enlevés
 * <li>Le code est converti en majuscules
 * </ul>
 * <p>
 * <h3>Critère de recherche de codes</h3>
 * <h4>Code CIM-10 normal</h4>
 * Si l'expression à rechercher ne contient que une lettre suivie de chiffres ou de '+', elle est considérée comme un 
 * code CIM-10 normal et comparée telle quelle.<br>
 * <h4>Intervalle de codes CIM-10</h4>
 * Si l'expression à rechercher est composée de deux codes CIM-10 séparés par ':', c'est un intervalle. Un code CIM-10
 * sera sélectionné s'il est dans l'intervalle. Bornes hautes et basses sont comprises. Par exemple, pour l'intervalle A00:B94
 * le test sera de voir si le code est &gt;= "A00" et &lt;= "B94999". 
 * <h4>Expression régulière</h4>
 * Si l'expression à rechercher n'est ni un intervalle ni un code CIM-10 normal, l'expression à rechercher sera considérée comm une expression régulière (cf. <a href="https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html">Pattern</a>) :<br>
 * Ainsi on peut rechercher 'F01[89]1.' : code qui commence par F01, puis suivi d'un 8 ou d'un 9, puis suivi d'un 1, puis de n'importe quel chiffre.<br>
 * Les autres codes sont recherchés tels quels.<br>
 * Par exemple A03 ne recherche que "Shigellose", et pas les sous-codes.<br>
 * Pour rechercher A03 et ses sous-codes, il faut mettre A03.* (ou A03:A03 qui marche aussi)<br>
 * Pour ne recherche <i>que</i> les sous codes (donc sans A03), il faut mettre A03.+<br>
 * La recherche par expression régulière est forcément un peu plus lente ; si ce
 * point est important et si c'est possible mettre plutôt tous les codes possibles. <br>
 * Par exemple au lieu d'utiliser 'A03.+' on peut mettre à la place
 * 'A030,A031,A032,A033,A038,A039'<br>
 * N.B. si on recherche le caractère '+' en tant que caractère de code CIM-10, il faut se souvenir qu'il devra
 * être précédé d'un caractère d'échappement. Par ex
 * si on recherche tous les codes qui commencent par U831+ on peut utiliser les expressions suivantes :
 * <ul>
 * <li><code>U831\+</code> pour rechercher <i>exactement</i> le code <code>U831+</code>
 * <li><code>U831\+.</code> pour rechercher le code <code>U831+</code> suivi de exactement un caractère
 * <li><code>U831\+.*</code> pour rechercher le code <code>U831+</code> suivi zéro ou plus caractères
 * <li><code>U831\+.+</code> pour rechercher le code <code>U831+</code> suivi de <b>un</b> caractère ou plus
 * <li><code>U831\+[08]</code>  pour rechercher le code <code>U831+</code> suivi du caractère '0' ou du caractère '8' 
 * </ul>
 * Voir <a href="https://docs.oracle.com/javase/tutorial/essential/regex/">https://docs.oracle.com/javase/tutorial/essential/regex/</a>
 * ou <a href="https://www.jmdoudoux.fr/java/dej/chap-regex.htm">https://www.jmdoudoux.fr/java/dej/chap-regex.htm</a>
 * pour des tutoriels qui permettent de comprendre en profondeur les expressions régulières (on en a rarement besoin pour la CIM-10 !).
 */
class CimCodePresence
    implements PmsiCriterion 
{
    final static Logger lg = LoggerFactory.getLogger(CimCodePresence.class);
    
    static Pattern cimPattern =  Pattern.compile(/[A-Z][0-9\+]+/) //pattern pour un code CIM10 autorisé
    static Pattern cimRangePattern =  Pattern.compile(/[A-Z][0-9\+]+\:[A-Z][0-9\+]+/) //pattern pour un intervalle de codes CIM10 autorisés
    
    String locations
    String firstCode
    String lastCode
    boolean includeLast
    Set<String> codes = []
    Set<Pattern> patterns = []
    Set<Tuple2> ranges = []
    int searchType //0 : liste de codes, 1 : intervalle
    def codeExprs = null //contient les regexps s'il y a lieu
    
    /**
     * Rechercher un code ou une expression régulière
     */
    CimCodePresence(String locations, String uniqueCode) {
        this(locations, [uniqueCode])
    }

    /**
     * Rechercher les codes qui sont compris entre le premier code (inclus) et le dernier code (inclus si includeLast est vrai, exclus sinon)
     * Si on est sûr que tous les codes à chercher font partie de la CIM 10 PMSI, on
     * peut remplacer l'appel à ce constructeur par :
     * <code/>CimCodePresence(locations, SharedCim10.findCodes(firstCode, lastCode, includeLast))</code>
     * cela accélèrera la recherche.
     */
    CimCodePresence(String locations, String firstCode, String lastCode, boolean includeLast) {
        searchType = 1
        this.locations = locations
        this.includeLast = includeLast
        this.firstCode = firstCode
        this.lastCode = lastCode
    }
    
    /**
     * Rechercher les codes qui sont compris entre le premier code (inclus) et le dernier code (inclus)
     */
    CimCodePresence(String locations, String firstCode, String lastCode) {
        this(locations, firstCode, lastCode, true)
    }
    
    /**
     * Rechercher une ou plusieurs expressions
     * @param locations Les endroits où chercher
     * @param exprsList Les expressions à rechercher
     */
    CimCodePresence(String locations, List<String> exprsList) {
        searchType = 0
        this.locations = locations
        //codes = [] as Set
        //codes.addAll(exprsList)
        analyzeExprs(exprsList)
        //System.out.println("codeList:$codeList")
    }
    
    /**
     * Rechercher une ou plusieurs expressions
     * @param locations Les endroits où chercher
     * @param exprsList Les expressions à rechercher
     */
    CimCodePresence(String locations, String[] exprsList) {
      this(locations, Arrays.asList(exprsList));
    }
      
    /**
     * Normaliser les codes CIM-10 à l'intérieur du tableau
     * @param codes Le tableau qui doit être modifié
     */
    void normalizeCodes(codes) {
        for (int i = 0; i < codes.size(); i++) {
            codes[i] = SharedCim10.normalizeCode(codes[i])
        }
    }

    /**
     * Analyser chaque code de la liste transmise, et le ranger dans codes ou patterns ou ranges
     * @param codeList une liste ou un tableau de codes 
     * @return void
     */
    private void analyzeExprs(exprList) {
      exprList.each {expr ->
        if (expr ==~ cimRangePattern) {
          //c'est un intervalle, de type A00:B19
          def rng = expr.split(':')
          ranges << new Tuple2(rng[0], (rng[1]+"99999")[0..5])
        }
        else if (expr ==~ cimPattern) {
          //c'est un code CIM-10 normal
          codes << expr
        }
        else {
          //On suppose que c'est un motif de recherche, par ex B9[12]1\+ 
          patterns << Pattern.compile(expr)
        }
      }
    }
    
    /**
     * Rechercher un ou plusieurs codes CIM 10 avec les critères donnés, dans
     * les emplacements spécifiés
     * Les emplacements disponibles sont :
     * <ul>
     * <li> DP, DR, DAS pour les RUMs
     * <li> FPP, MMP, AE, DAS pour les RHS
     * <li> DPA, DRA, pour les RSAs
     * <li> RADP, RADR, RADAS, pour les RUMs des RSAs
     * </ul>
     */
    boolean eval(HashMap context) {
        FszGroup rum = null
        FszGroup rhs = null
        FszGroup rsa = null
        def locs = locations.split(',')
        def readcodes = [] //les codes lus. Il seront normalisés plus tard.
        //ici on utilise "rum.getChildField('DP')" plutôt que la syntaxe plus évidente "rum.DP" car cela permet de tester directement dans Eclipse, hors de fr.gpmsi.Groovy
        //on garde la syntaxe simple pour les cas "addAll" qui eux ont besoin de la syntaxe dynamique pour appliquer les méthodes aux groupes
        locs.each() {loc ->
            def locn = loc.trim().toUpperCase() //normaliser
            switch (locn) {
                case 'DP':
                  if (rum == null) { rum = context['rum'] }
                  if (rum != null) { readcodes << rum.getChildField('DP').value.trim() }
                  break
                case 'DPA':
                  if (rsa == null) { rsa = context['rsa'] }
                  if (rsa != null) { readcodes << rsa.getChildField('DP').value.trim() }
                  break
                case 'RADP': //DPs des RUMs du RSA
                  if (rsa == null) { rsa = context['rsa'] }
                  if (rsa != null) { readcodes.addAll(rsa.RU.txtDP) }
                break
                case 'DR':
                  if (rum == null) { rum = context['rum'] }
                  if (rum != null) { readcodes << rum.getChildField('DR').value.trim() ; println "readcodes:$readcodes" }
                  break
                case 'DRA':
                  if (rsa == null) { rsa = context['rsa'] }
                  if (rsa != null) { readcodes << rsa.getChildField('DR').value.trim() }
                  break
                case 'RADR': //DRs des RUMSs du RSA
                  if (rsa == null) { rsa = context['rsa'] }
                  if (rsa != null) { readcodes.addAll(rsa.RU.txtDR) }
                  break
                case 'DAS':
                  if (rum == null) { rum = context['rum'] }
                  if (rhs == null) { rhs = context['rhs'] }
                  if (rum != null) { readcodes.addAll(rum.DA.txtTDA) }
                  if (rhs != null) { readcodes.addAll(rhs.DA.txtTDA) }
                  break
                case 'RADAS': //DAs des RUMs du RSA
                  if (rsa == null) { rsa = context['rsa'] }
                  if (rsa != null) { 
                    def radasCodes = (rsa.RU.DA.txtTDA).flatten()
                    //println("radasCodes:$radasCodes")
                    readcodes.addAll(radasCodes)                     
                  }
                  break
                case 'FPP':
                  if (rhs == null) { rhs = context['rhs'] }
                  if (rhs != null) { readcodes.addAll(rhs.txtFPP) }
                  break
                case 'AE':
                  if (rhs == null) { rhs = context['rhs'] }
                  if (rhs != null) { readcodes.addAll(rhs.txtAE) }
                  break
                case 'MMP':
                  if (rhs == null) { rhs = context['rhs'] }
                  if (rhs != null) { readcodes.addAll(rhs.txtMMP) }
                  break
            }
        }
        if (lg.debugEnabled) lg.debug("usercodes: $readcodes");
        normalizeCodes(readcodes)
        if (searchType == 0) { //liste de codes
            //regarder si au moins un code en commun avec la liste des codes à chercher
            if (!Collections.disjoint(codes, readcodes)) return true
            //regarder si des codes sont parmis les intervalles permis
            if (ranges.any {r -> readcodes.any {c -> r[0] <= c && c <= r[1]}}) return true
            //regarder si des codes répondent à au moins une des expressions régulières (on fait ce test en dernier car c'est le plus coûteux)
            if (patterns.any { p -> readcodes.any { c -> c =~ p }}) return true
        }
        else if (searchType == 1) { //intervalle individuel, avec possibilité de ne pas inclure la borne sup
            codes.any { code ->
                if (includeLast) firstCode <= code && code <= lastCode
                else firstCode <= code && code < lastCode
            }
        }
        else throw new Exception("Erreur interne type de recherche non prevu : "+searchType)
    }
    
}