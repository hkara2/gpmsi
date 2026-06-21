package fr.gpmsi.da;

/**
 * Implémenté par les objets qui admettent de remplacer une valeur null par un zéro
 * @author hkaradimas
 *
 */
public interface IZeroForNullAllowed {
  
    /** 
     * @return true si il est autorisé de retourner un objet représentant zéro à la place de null
     */
    boolean isZeroForNullAllowed()
    
    /**
     * Dire si on peut retourner un objet représentant zéro à la place d'une valeur null
     * @param zfna true si c'est le cas
     */
    void setZeroForNullAllowed(boolean zfna)
    
    /**
     * Retourner un objet qui représente zéro
     * @return un objet qui représente zéro
     */
    Object getObjectForZero()
}
