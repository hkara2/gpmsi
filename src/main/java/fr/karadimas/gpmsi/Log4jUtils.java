package fr.karadimas.gpmsi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;


/**
 * Utilitaires locaux à gpmsi
 * @author hkaradimas
 *
 */
public class Log4jUtils {

  public Log4jUtils() {
  }

  public static void setRootLogLevel(Level logLevel) {
    final LoggerContext context = (LoggerContext) LogManager.getContext(false);
    final org.apache.logging.log4j.core.config.Configuration config = context.getConfiguration();
    config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(logLevel);
    config.getLoggerConfig(Groovy.class.getPackage().getName()).setLevel(logLevel);
    context.updateLoggers(config);
  }
  
}
