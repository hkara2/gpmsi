/**:encoding=UTF-8:
 */
package fr.gpmsi.pmsi_rules.cim

class CimListHelper {
    
    /**
     * Méthode utilitaire pour transformer une liste de codes séparée par des
     * espaces ou des retours à la ligne en liste de codes.
     * Equivalent à .split() mais plus clair dans l'intention
     * Ex :
     * <pre>
     *   import static fr.gpmsi.pmsi_rules.cim.CimListHelper.makeCimCodesList
     *
     *   codes = makeCimCodesList("""A021 A227 A267 A327 A40 A400 A401 A402 A403 A408 A409 A41 A410
     * A411 A412 A413  A414 A415 A418 A419 A427 B377 O85 P3600 P3610 P3620 P3630 P3640
     * P3650 P3680 P3690""")
     *
     *   assert codes[25] == "P3690"
     * </pre>
     */
    static List makeCimCodesList(String spaceSeparatedCodes) {
        if (spaceSeparatedCodes == null) return []
        return spaceSeparatedCodes.split() as List //.split() agit sur les séparateur "whitespace" par défaut [ \t\n\r\f]
    }
}