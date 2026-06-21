package fr.gpmsi.da;

/**
 * Exception envoyée lorsqu'une valeur NULL est interdite dans une colonne, mais qu'on a essayé d'y ranger une valeur null.
 * @author hkaradimas
 *
 */
public class NullNotAllowedException
  extends Exception 
{
  /** @see Exception */
  public NullNotAllowedException() { }

  /** @see Exception */
  public NullNotAllowedException(String message) { super(message) }

  /** @see Exception */
  public NullNotAllowedException(Throwable cause) { super(cause) }

  /** @see Exception */
  public NullNotAllowedException(String message, Throwable cause) { super(message, cause) }

  /** @see Exception */
  public NullNotAllowedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
