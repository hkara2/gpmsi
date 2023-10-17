package fr.karadimas.gpmsi;

/**
 * Petites fonctions utilitaires d'objet
 * @author hkaradimas
 *
 */
public class ObjectUtils {

  /** Constructeur par défaut, ne sert à rien ici */
	public ObjectUtils() {
	}

	/**
	 * Comparaison sûre entre deux objets
	 * @param a 1er objet (peut être null)
	 * @param b 2ème objet (peut être null)
	 * @return true si a et b sont tous deux null ou s'ils sont tous deux égaux
	 */
	public static boolean safeEquals(Object a, Object b) {
		if (a == null) return b == null;
		if (b == null) return false; else return a.equals(b);
	}
	
	/**
	 * Comparaison sûre entre deux objets
	 * @param <T> Le type des deux objets à utiliser (ils doivent être du même type)
	 * @param a 1er objet (peut être null)
	 * @param b 2ème objet (peut être null)
	 * @return 0 si a est null et b est null. -1 si a est null mais pas b. 1 si b est null mais pas a.
	 *   Sinon retourne a.compareTo(b)
	 */
	public static <T extends Comparable<T>> int safeCompare(T a, T b) {
		if (a == null) {
			if (b == null) return 0;
			else return -1;
		}
		if (b == null) return 1;
		return a.compareTo(b);
	}
	
}
