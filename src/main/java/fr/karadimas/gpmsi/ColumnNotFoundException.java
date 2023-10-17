package fr.karadimas.gpmsi;

/**
 * Exception envoyée lorsqu'une colonne qui n'existe pas a été référencée
 * @author hkaradimas
 *
 */
public class ColumnNotFoundException extends Exception {

  /**
   * Constructeur
   */
  public ColumnNotFoundException() {
  }

  /**
   * Constructeur
   * @param message message
   */
  public ColumnNotFoundException(String message) {
    super(message);
  }

  /**
   * Constructeur
   * @param cause cause
   */
  public ColumnNotFoundException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructeur
   * @param message message
   * @param cause cause
   */
  public ColumnNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructeur
   * @param message -
   * @param cause -
   * @param enableSuppression -
   * @param writableStackTrace -
   */
  public ColumnNotFoundException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
