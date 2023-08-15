package fr.karadimas.gpmsi;

public class ObjectUtils {

	public ObjectUtils() {
	}

	public static boolean safeEquals(Object a, Object b) {
		if (a == null) return b == null;
		if (b == null) return false; else return a.equals(b);
	}
	
	public static <T extends Comparable<T>> int safeCompare(T a, T b) {
		if (a == null) {
			if (b == null) return 0;
			else return -1;
		}
		if (b == null) return 1;
		return a.compareTo(b);
	}
	
}
