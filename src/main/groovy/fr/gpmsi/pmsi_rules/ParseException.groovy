package fr.gpmsi.pmsi_rules;

public class ParseException extends Exception {

  public ParseException() {
  }

  public ParseException(String message) {
    super(message);
  }

  public ParseException(Throwable cause) {
    super(cause);
  }

  public ParseException(String message, Throwable cause) {
    super(message, cause);
  }

  public ParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
