package fr.karadimas.gpmsi.pmsi_rules.cim
import fr.karadimas.gpmsi.cim.SharedCim10
import fr.karadimas.gpmsi.pmsi_rules.PmsiCriterion
import fr.karadimas.pmsixml.FszGroup
import java.util.regex.Pattern

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Recherche de codes cim 10 dans différents endroits.
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
 * Le ou les codes CIM 10 récupérés dans les endroits à rechercher seront normalisés :
 * <ul>
 * <li>Les espaces de début et de fin sont enlevés
 * <li>Les points ('.') sont enlevés
 * <li>Le code est converti en majuscules
 * </ul>
 * Le code à rechercher peut être une expression régulière (cf. <a href="https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html">Pattern</a>) :
 * Ainsi on peut rechercher 'F01[89]1.' : code qui commence par F01, puis suivi d'un 8 ou d'un 9, puis suivi d'un 1, puis de n'importe quel chiffre.
 * Les autres codes sont recherchés tels quels.
 * Par exemple A03 ne recherche que "Shigellose", et pas les sous-codes.
 * Pour rechercher A03 et ses sous-codes, il faut mettre A03.*
 * Pour ne recherche <i>que</i> les sous codes (et pas A03), il faut mettre A03.+
 * La recherche par expression régulière est forcément un peu plus lente ; si ce
 * point est important et si c'est possible mettre plutôt tous les codes possibles. 
 * Par exemple au lieu d'utiliser 'A03.+' on peut mettre à la place
 * 'A030,A031,A032,A033,A038,A039'
 */
class CimCodePresence
    implements PmsiCriterion 
{
    final static Logger lg = LoggerFactory.getLogger(CimCodePresence.class);
    
    static Pattern cimPattern =  Pattern.compile(/[A-Z][0-9\+]+/) //pattern pour un code CIM10 autorisé

    String locations
    String firstCode
    String lastCode
    boolean includeLast
    Set<String> codes
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
        searchType = 0
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
     * Rechercher un ou plusieurs codes ou expressions régulières
     */
    CimCodePresence(String locations, List<String> codeList) {
        searchType = 0
        this.locations = locations
        codes = [] as Set
        codes.addAll(codeList)
        //System.out.println("codeList:$codeList")
    }
    
    CimCodePresence(String locations, String[] codeList) {
      this(locations, Arrays.asList(codeList));
    }
      
    void normalizeCodes(codes) {
        for (int i = 0; i < codes.size(); i++) {
            codes[i] = SharedCim10.normalizeCode(codes[i])
        }
    }

    /**
     * Rechercher un ou plusieurs codes CIM 10 avec les critères donnés, dans
     * les emplacements spécifiés
     * Les emplacements disponibles sont :
     * DP, DR, DAS pour les RUMs
     * DPA, DRA, pour les RSAs
     * RADP, RADR, RADAS, pour les RUMs des RSAs
     */
    boolean eval(HashMap context) {
        FszGroup rum = null
        FszGroup rsa = null
        def rhs = null
        def locs = locations.split(',')
        def readcodes = [] //les codes lus. Il seront normalisés plus tard.
        locs.each() {loc ->
            def locn = loc.trim().toUpperCase() //normaliser
            switch (locn) {
                case 'DP':
                  if (rum == null) { rum = context['rum'] }
                  if (rum != null) { readcodes << rum.getChildField('DP').value }
                  break
                case 'DPA':
                  if (rsa == null) { rsa = context['rsa'] }
                  if (rsa != null) { readcodes << rsa.getChildField('DP').value }
                  break
                case 'RADP': //DPs des RUMs du RSA
                  if (rsa == null) { rsa = context['rsa'] }
                  if (rsa != null) { readcodes.addAll(rsa.RU.txtDP) }
                break
                case 'DR':
                  if (rum == null) { rum = context['rum'] }
                  if (rum != null) { readcodes << rum.getChildField('DR').value }
                  break
                case 'DRA':
                  if (rsa == null) { rsa = context['rsa'] }
                  if (rsa != null) { readcodes << rsa.getChildField('DR').value }
                  break
                case 'RADR': //DRs des RUMSs du RSA
                  if (rsa == null) { rsa = context['rsa'] }
                  if (rsa != null) { readcodes.addAll(rsa.RU.txtDR) }
                  break
                case 'DAS':
                  if (rum == null) { rum = context['rum'] }
                  if (rhs == null) { rhs = context['rhs'] }
                  if (rum != null) { readcodes.addAll(rum.DA.txtTDA) }
                  //TODO faire pareil pour rhs
                  break
                case 'RADAS': //DAs des RUMs du RSA
                  if (rsa == null) { rsa = context['rsa'] }
                  if (rsa != null) { 
                    def radasCodes = (rsa.RU.DA.txtTDA).flatten()
                    //println("radasCodes:$radasCodes")
                    readcodes.addAll(radasCodes)                     
                  }
                  break
            }
        }
        if (lg.debugEnabled) lg.debug("usercodes: $readcodes");
        normalizeCodes(readcodes)
        if (searchType == 0) {
            if (codeExprs == null) {
                //initialiser les expressions de recherche
                codeExprs = []
                codes.removeAll { code ->
                    def isRegular = code ==~ cimPattern //true is code est un code CIM10 normal
                    //System.out.println("code:$code,isRegular:$isRegular")
                    if (!isRegular) codeExprs << Pattern.compile(code) //si code est une regexp, on la compile
                    !isRegular //si code est une regexp elle sera enlevee des codes normaux
                }
            }
            def tmp = readcodes.any { codes.contains(it) }
            //System.out.println("codes:$codes")
            //System.out.println("codesExprs:$codeExprs")
            //System.out.println("tmp:$tmp")
            //1) rechercher parmi les codes normaux si un des codes est parmi eux
            //   Comme "codes" est un HashSet la recherche est rapide
            if (readcodes.any { codes.contains(it) }) true
            else {
                //2) si ce n'est pas le cas, rechercher parmi les regexp et retourner le resultat
                codeExprs.any { pattern ->
                    readcodes.any { it =~ pattern }
                }
            }
        }
        else if (searchType == 1) {
            codes.any { code ->
                if (includeLast) firstCode <= code && code <= lastCode
                else firstCode <= code && code < lastCode
            }
        }
        else throw new Exception("Erreur interne type de recherche non prevu : "+searchType)
    }
    
}