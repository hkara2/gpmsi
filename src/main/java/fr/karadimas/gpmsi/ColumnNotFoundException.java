package fr.karadimas.gpmsi;

/**
 * Exception envoyée lorsqu'une colonne qui n'existe pas a été référencée
 * @author hkaradimas
 *
 */
public class ColumnNotFoundException extends Exception {

  public ColumnNotFoundException() {
  }

  public ColumnNotFoundException(String message) {
    super(message);
  }

  public ColumnNotFoundException(Throwable cause) {
    super(cause);
  }

  public ColumnNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ColumnNotFoundException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
