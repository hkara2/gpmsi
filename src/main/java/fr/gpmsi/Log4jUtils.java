package fr.gpmsi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;


/**
 * Utilitaires locaux à gpmsi
 * @author hkaradimas
 *
 */
public class Log4jUtils {

  /**
   * Constructeur par défaut
   */
  public Log4jUtils() {
  }

  /**
   * Attribuer le niveau de log par défaut, en utilisant le contexte de log par défaut.
   * Attention utiliser log4j version 2 (package org.apache.logging.log4j) qui ne marche pa
   * très bien avec slf4j, qu'il vaut mieux éviter avec cette méthode.
   * 
   * @param logLevel Le niveau par défaut à attribuer.
   */
  public static void setRootLogLevel(Level logLevel) {
    final LoggerContext context = (LoggerContext) LogManager.getContext(false);
    final org.apache.logging.log4j.core.config.Configuration config = context.getConfiguration();
    config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(logLevel);
    config.getLoggerConfig(Groovy.class.getPackage().getName()).setLevel(logLevel);
    context.updateLoggers(config);
  }
  
}
