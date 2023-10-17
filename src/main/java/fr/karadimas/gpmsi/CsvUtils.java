package fr.karadimas.gpmsi;
/**
 * Fonctions utilitaires csv.
 * @author hkaradimas
 *
 */
public class CsvUtils {

  /** constructeur privé car ce ne sont que des méthodes statiques */
  private CsvUtils() {
  }

  /**
   * Remplacer les séquences de fin de ligne (\n, \r\n, ou \r tout seul) par un texte
   * spécial. C'est nécessaire avant l'export CSV pour éviter les erreurs dans Excel, qui ne sait pas importer du
   * CSV multiligne.
   * Exemple d'utilisation : <code>replaceNewlines(texte, "\\n")</code>
   * @param str Le texte pour lequel on veut remplacer les séquences.
   * @param newlineSeq La séquence à remplacer
   * @return Le nouveau texte
   */
  public static final String replaceNewlines(String str, String newlineSeq) 
  {
    StringBuilder sb = null; //on n'alloue rien pour l'instant, on allouera si nécessaire (plus rapide pour les 99% du temps où il n'y a pas de fins de ligne dans la valeur)
    if (str == null) return null; //null est toléré, et on le ramène tel quel
    int strlen = str.length();
    int p = 0;
    char c;
    while (p < strlen) {
      c = str.charAt(p);
      if (c == '\n' || c == '\r') {
        if (sb == null) sb = new StringBuilder(str.substring(0, p)); //rattraper l'initialisation de sb
        p++;
        //voir si \r est suivi de \n, auquel cas avancer p pour sauter ce \n
        if (p < strlen && c == '\r' && str.charAt(p) == '\n') p++;
        //mettre maintenant la séquence spéciale
        sb.append(newlineSeq);
      }
      else {
        if (sb != null) sb.append(c);
        p++;
      }
    }
    if (sb == null) return str; //on n'a rencontré aucune séquence de fin de ligne, on peut retourner la même chaîne qu'en entrée
    else return sb.toString(); //sinon on retourne le nouveau texte qui contient les substitutions
  }
  
  /**
   * Remplacer les fins de lignes. Cf. 
   * {@link CsvUtils#replaceNewlines(String, String)}
   * @param row valeurs de la rangée
   * @param newlineSeq Séquence de remplacement
   */
  public static final void replaceNewlines(String[] row, String newlineSeq) {
    for (int i = 0; i < row.length; i++) {
      row[i] = CsvUtils.replaceNewlines(row[i], newlineSeq);
    } 
  }
  
}
