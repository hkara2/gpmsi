package fr.gpmsi;

/**
 * Utilitaires de String très fréquemment utilisés pour éviter d'avoir à 
 * les redéclarer à chaque fois.
 * @author hkaradimas
 *
 */
public class StringUtils {

  private StringUtils() {}
  
  /**
   * Retourne vrai si str est null ou de longueur 0
   * @param str Une chaîne de caractères (peut être null)
   * @return vrai si str est null ou de longueur 0
   */
  public static final boolean isEmpty(String str) {
    if (str == null) return true;
    return str.isEmpty();
  }
  
  /**
   * Retourne vrai si str est null ou si après appel a trim() est de longueur 0
   * @param str Une chaîne de caractères (peut être null)
   * @return vrai si str est null ou si après appel a trim() est de longueur 0
   */
  public static final boolean isTrimEmpty(String str) {
    if (str == null) return true;
    return str.trim().isEmpty();    
  }
  
  /**
   * Retourne str sans les caractères blanc finaux (espaces et tabulations).
   * Appelle juste {@link org.apache.commons.lang3.StringUtils#stripEnd(String, String)}
   * Utile si on veut garder les caractères de départ, qui peuvent être utilisés
   * pour un formatage visuel du texte.
   * @param str la String à laquelle on veut enlever les caractères blancs de la fin
   * @return la String transformée
   */
  public static final String endTrim(String str) {
    return org.apache.commons.lang3.StringUtils.stripEnd(str, null);
  }

  /**
   * Retourne vrai si str est null ou si après appel a endTrim() est de longueur 0
   * @param str Une chaîne de caractères (peut être null)
   * @return vrai si str est null ou si après appel a endTrim() est de longueur 0
   */
  public static final boolean isEndTrimEmpty(String str) {
    if (str == null) return true;
    return endTrim(str).isEmpty();    
  }
  
  
  /**
   * Retirer l'extension du fichier et la retourner. S'il n'y a pas d'extension, retourne une
   * chaîne vide.
   * <br>
   * Ex 1 : <code>getExtenstion("toto.txt")</code> : ".txt"
   * <br>
   * Ex 2 : <code>getExtenstion("bonjour")</code> : ""
   * @param str Le nom pour lequel on veut l'extension (si null retourne une chaine vide)
   * @return l'extension du nom
   */
  public static final String getExtension(String str) {
    if (str == null) return "";
    int pos = str.lastIndexOf('.');
    if (pos < 0) return "";
    return str.substring(pos);
  }
  
  /**
   * Enleve l'extension de la chaîne de caractère. Par sécurité, si la chaîne ne se termine pas par
   * l'extension, rien n'est enlevé.
   * @param str La chaîne dont on veut enlever l'extension
   * @param ext L'extension à enlever
   * @return La chaîne transformée ou null si str est null
   */
  public static final String removeExtension(String str, String ext) {
    if (str == null) return null;
    if (str.endsWith(ext)) return str.substring(0, str.length()-ext.length());
    else return str;
  }
  
  /**
   * Enlever l'extension (quelle qu'elle soit) de la chaîne de caractères.
   * @param str La chaîne dont on veut enlever l'extension
   * @return La chaîne transformée
   */
  public static final String removeExtension(String str) {
    return removeExtension(str, getExtension(str));
  }
  
  /**
   * Normalisation de chaîne de caractère générale.
   * Si null, retourne chaîne vide.
   * Sinon, retourne la chaîne sans les espaces de début et de fin, et transformée
   * en majuscules.
   * Utile pour les codes en général (GHM, etc), sauf pour la CIM10 où il faut
   * en plus enlever les points.
   * @param str La chaîne à normaliser
   * @return La chaîne normalisée
   */
  public static final String normalizeCode(String str) {
    if (str == null) return "";
    else return str.trim().toUpperCase();
  }
  
}
