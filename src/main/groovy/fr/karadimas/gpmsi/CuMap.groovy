package fr.karadimas.gpmsi

/**
 * Une Map qui sera mise a jour seulement si la nouvelle valeur repond aux criteres
 * de comparaison. (Conditional Update Map)
 * Exemple d'utilisation :
 * <pre>
 * def mapHauteurMax = new CondMap(compare: {a, n-> n > a})
 * 
 * mapHauteurMax.update("paul", 180) //met la valeur 180 pour paul
 * mapHauteurMax.update("jean", 160) //met la valeur 160 pour jean
 * mapHauteurMax.update("paul", 179) //ne fait rien pour paul
 * mapHauteurMax.update("paul", 181) //remplace la valeur pour paul qui devient 181
 * </pre>
 * @author hkaradimas
 *
 */
class CuMap extends HashMap {
  
  /**
   * Une fonction de comparaison qui doit gerer deux valeurs : l'ancienne
   * valeur (qui est dans la Map) et la nouvelle valeur, candidate pour
   * remplacer l'ancienne valeur. Doit retourner true si la nouvelle valeur
   * doit remplacer l'ancienne dans la Map.
   */
  Closure compare
  
  CuMap(Closure comparisonClosure) { compare = comparisonClosure }
  
  void update(Object key, Object value) {
    if (key == null || value == null) return //pas de maj sur cle ou valeur nulle
    def oldVal = get(key)
    if (oldVal == null) put(key, value)
    else if (compare(oldVal, value)) { put(key, value) }
  }
}