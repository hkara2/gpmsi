/**:encoding=UTF-8:
 * Exemple basique d'utilisation d'un log relié à ce script, avec sortie
 * dans un fichier
 * Noter que le contexte ici est :
 * log_example
 * C'est à dire le nom de notre script. C'est plus simple pour trouver les
 * erreurs.
 * A essayer avec et sans -debug.
 * Exemple :
 * C:\hkchse\dev\pmsixml\exemples-groovy>gpmsixml -debug -script logfile_example.groovy
 * C:\hkchse\dev\pmsixml\exemples-groovy>gpmsixml -script logfile_example.groovy
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Level;
import fr.gpmsi.Log4j2Utils

log = LogManager.getLogger(this.class.name) //le niveau par défaut des nouveaux Loggers est ERROR
aa = new Log4j2Utils.FileAppenderAttributes(name: this.class.name, fileName: 'tmp/mylogfile.txt')
Log4j2Utils.attachFileAppender(this.class.name, aa)

//quelques exemples de sorties dans notre Logger local
log.error "Hello log"
log.debug "Ceci ne sera pas visible (on n'est pas en DEBUG ici)"

Log4j2Utils.changeLoggerLevel(this.class.name, Level.DEBUG)
log.debug "Ceci par contre va se voir"
