package fr.gpmsi;
//:encoding:UTF-8:
import java.util.Locale;
import java.text.Normalizer; //normalise notamment en ASCII (enlève accents du français)

/**
 * Phonémisation SOUNDEX adaptée au français.
 * <br>
 * Cf. <a href="https://github.com/NicolasGeraud/solr-phonex/blob/master/src/main/java/org/apache/commons/codec/language/Phonex.java">https://github.com/NicolasGeraud/solr-phonex/blob/master/src/main/java/org/apache/commons/codec/language/Phonex.java</a>
 * <br>
 * Cf. <a href="http://info.univ-lemans.fr/~carlier/recherche/soundex.html">http://info.univ-lemans.fr/~carlier/recherche/soundex.html</a> (ancien lien ne fonctionne plus depuis 2020)
 * <br>
 * Cf. <a href="https://sqlpro.developpez.com/cours/soundex/">https://sqlpro.developpez.com/cours/soundex/</a>
 *
 * @author hkaradimas
 *
 */
public class Phonex {

  private Phonex() {
  }

  /**
   * Calculer le texte réduit selon l'algorithme du Phonex.
   * Pour de meilleurs résultats, standardiser auparavant le texte avec la
   * méthode #standardize1(String) ou bien utiliser toPhonexStandard1(String)
   * @param txt Le texte à phonémiser
   * @return Le résultat phonex
   */
  public static String toPhonex(String txt) {
    // Si la chaîne est vide, retourner un phonex vide
    if ((txt == null) || (txt.length() == 0)) {
        return "";
    }

    String chaine = txt.toUpperCase(Locale.FRENCH);

    chaine = chaine.replaceAll("\\*", "");
    chaine = chaine.replaceAll("\\?", "");
    
    // 1. Remplacement des 'y' par des 'i'
    chaine = chaine.replace('Y', 'I');

    // 2. Suppression des 'h' qui ne sont pas précédés de 'c', 's' ou 'p'
    chaine = chaine.replaceAll("([^CSP])H", "$1");

    //
    if (chaine.startsWith("H"))
        chaine = chaine.substring(1, chaine.length());
    
    // 3. Remplacement de 'ph' par 'f'
    chaine = chaine.replaceAll("PH", "F");

    // 4. Remplacement des groupes de lettres gan / gam / gain / gaim (g par k)
    chaine = chaine.replaceAll("G(AN|AM|AIN|AIM)", "K$1");

    // 5. Remplacement des groupes de lettres ain / ein / aim / eim par 'yn' si suivi de 'a', 'e', 'i', 'o' ou 'u'
    chaine = chaine.replaceAll("((A|E)I(N|M))([AEIOU]{1})", "YN$4");

    // 6. Remplacement des groupes de 3 lettres (sons 'eau', 'oua', 'ein', 'eim', 'ain', 'aim')
    chaine = chaine.replaceAll("EAU", "O");
    chaine = chaine.replaceAll("OUA", "2");
    chaine = chaine.replaceAll("(A|E)I(N|M)", "4");

    // 7. Remplacement du son 'é'    
    chaine = chaine.replaceAll("(\u00C9|\u00C8|\u00CA|EI|AI)", "Y"); //chaine.replaceAll("(É|È|Ê|EI|AI)", "Y");
    
    chaine = chaine.replaceAll("E(R|SS|T)", "Y$1");

    chaine = chaine.replaceAll("(M)+M", "$1");
    chaine = chaine.replaceAll("(N)+N", "$1");
        
    // 8. Remplacement de 'ean' par 1 (sauf si suivi par 'a', 'e', 'i', 'o' ,'u', '1', '2', '3', '4')
    chaine = chaine.replaceAll("(?:A|E)(?:N|M)([^AEIOU1-4]|[ ]|\\Z)", "1$1");
    
    // 9. Remplacement de 'in' par 4
    chaine = chaine.replaceAll("IN", "4");

    // 9.
    chaine = chaine.replaceAll("([AEIOU1-4]{1})S([AEIOU1-4]{1})", "$1Z$2");

    // 10.
    chaine = chaine.replaceAll("(OE|EU)", "E");
    chaine = chaine.replaceAll("AU", "O");
    chaine = chaine.replaceAll("O(I|Y)", "2");
    chaine = chaine.replaceAll("OU", "3");

    // Remplacement de 'sh', 'ch', 'sch' par 5
    chaine = chaine.replaceAll("(S|C|SC)H", "5");
    
    // Remplacement de 'ss', 'sc' par 's'
    chaine = chaine.replaceAll("S(S|C)", "S");

    // 12.
    chaine = chaine.replaceAll("C(E|I)", "S");

    // 13.
    chaine = chaine.replaceAll("(C|QU|GU|Q)", "K");
    chaine = chaine.replaceAll("G(A|O|Y)", "K$1");

    // 14.
    chaine = chaine.replace('A', 'O');
    chaine = chaine.replace('D', 'T');
    chaine = chaine.replace('P', 'T');
    chaine = chaine.replace('J', 'G');
    chaine = chaine.replace('B', 'F');
    chaine = chaine.replace('V', 'F');
    chaine = chaine.replace('M', 'N');

    // 15.
    chaine = chaine.replaceAll("(.)\\1*", "$1");

    // 16.
    if (chaine.endsWith("T") || chaine.endsWith("X") || chaine.endsWith("E") || chaine.endsWith("S") || chaine.endsWith("Z"))
        chaine = chaine.substring(0, chaine.length() - 1);
    
    return chaine;
  }
  
  /**
   * Combine une standardisation supplémentaire et la réduction par phonex.
   * @param txt le texte à standardiser
   * @return toPhonex(standardize1(txt))
   */
  public static String toPhonexStandard1(String txt) {
      return toPhonex(standardize1(txt));
  }

  /**
   * Renvoyer la chaîne phonex avec tous les espaces (tabulations et FF comprises) enlevées
   * @param txt Le texte pour lequel on veut le Phonex
   * @return le résultat
   */
  public static String toPhonexWithoutSpaces(String txt) {
    return toPhonex(txt).replaceAll(" |\t|\f",  "");
  }
  

  /**
   * Méthode 1 de standardisation pour des noms :
   * <ul>
   * <li>Tout convertir en majuscules
   * <li>Normaliser en caractères ASCII
   * <li>Enlever tout ce qui n'est pas ASCII
   * </ul>
   * Exemple 1 : 
   * <code>standardize1("carrère d'encausse")</code> renverra "CARREREDENCAUSSE".
   * <br>
   * Exemple 2 :
   * <code>standardize1("jean-philippe")</code> renverra "JEANPHILIPPE".
   * @param str la chaîne à standardiser
   * @return la chaîne de caractères standardisée
   */
  public static String standardize1(String str) {
    String result = str;
    if (result == null) result = "";
    //normaliser en ASCII, et enlever tout ce qui n'est pas alphanumerique
    result = Normalizer.normalize(result.toUpperCase(), Normalizer.Form.NFD).replaceAll("[^\\p{Alnum}]", ""); //normalisation de texte ASCII
    return result;
  }

}

