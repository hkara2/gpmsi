/**
 * Exemple basique d'utilisation d'un log reli� � ce script
 * Noter que le contexte ici est :
 * log_example
 * C'est � dire le nom de notre script. C'est plus simple pour trouver les
 * erreurs.
 */

import org.apache.logging.log4j.LogManager;

log = LogManager.getLogger(this.class.name)

log.info "Hello log"
