package fr.gpmsi.da

import java.text.DateFormat

class DaPreferences {
    private static defprefs = new DaPreferences()
    
    static DaPreferences getDefaultPreferences() { defprefs }
    
    /** Particular date format to use. Default is null. Only used if column definition has no date format given */
    DateFormat dateFormat = null 
    
    /** when length exceeds maxLength, do we throw an error (the default = false)
     * or do we just cut (= true)
     */
    boolean cutWhenOverflow = false
    /**
     * when dates are of an illegal format, just convert them to null.
     * Default is false : we throw an exception
     */
    boolean illegalDatesToNull = false
}