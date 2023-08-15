package fr.karadimas.gpmsi.da

import groovy.transform.InheritConstructors

/**
 * Exception envoy�e si la taille d�passe la taille maximum autoris�e 
 */
@InheritConstructors
class MaximumSizeExceededException extends Exception {
    
}
