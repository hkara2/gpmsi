package fr.gpmsi.da

import groovy.transform.InheritConstructors

/**
 * Exception envoyée si la taille dépasse la taille maximum autorisée 
 */
@InheritConstructors
class MaximumSizeExceededException extends Exception {
    
}
