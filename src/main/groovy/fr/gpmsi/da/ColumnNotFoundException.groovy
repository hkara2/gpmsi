package fr.gpmsi.da;

/**
 * Exception envoyée quand une colonne n'a pas été trouvée pour le nom donné. 
 * @author hkaradimas
 *
 */
public class ColumnNotFoundException extends Exception {

  /** @see Exception */
  public ColumnNotFoundException() {
  }

  /** @see Exception */
  public ColumnNotFoundException(String message) {
    super(message);
  }

  /** @see Exception */
  public ColumnNotFoundException(Throwable cause) {
    super(cause);
  }

  /** @see Exception */
  public ColumnNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  /** @see Exception */
  public ColumnNotFoundException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
