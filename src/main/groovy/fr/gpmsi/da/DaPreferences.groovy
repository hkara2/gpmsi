package fr.gpmsi.da

import java.text.DateFormat

/**
 * Préférences pour le DA (Database Access)
 */
class DaPreferences {
    private static defprefs = new DaPreferences()
    
    static DaPreferences getDefaultPreferences() { defprefs }
    
    /**
     * Format de date particulier à utiliser. Le défaut est null. N'est utilisé que si la définition de colonne
     * n'a pas de format déclaré.
     */
    DateFormat dateFormat = null 
    
    /**
     * Lorsque cutWhenOverflow est true, une valeur qui dépasse sa longueur maximale autorisée est tronquée
     * silencieusement. Lorsqu'elle est false, une exception (@see MaximumSizeExceededException)
     * est envoyée lorsqu'une valeur dépasse sa longueur maximale autorisée.
     * Par défaut : false
     */
    boolean cutWhenOverflow = false
    
    /**
     * Que fait-on des dates qui ont un format illégal ?
     * Si illegalDatesToNull est true, on les tranforme en null. Sinon on envoie une exception.
     */
    boolean illegalDatesToNull = false
    
}