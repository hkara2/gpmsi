/**
 * Script qui est dans un package ; doit être appelé pour fonctionner avec une option qui indique le package à utiliser,
 * ou bien doit être appelé alors qu'on est dans le répertoire %GPMSI_BASE%\v@PROJECT_VERSION@\scripts
 * Lorsqu'il fonctionne, le script dit juste Bonjour.
 * Il permet de montrer le fonctionnement de l'appel d'un script qui est dans un package.
 * 
 * <p>Exemple 1 :
 * <pre>
 * cd %GPMSI_BASE%\v@PROJECT_VERSION@\scripts\groovy
 * ..\..\gpmsi -scripturi fr/gpmsi/Bonjour2.groovy
 * 
 * </pre>
 * Bonjour depuis le script fr/gpmsi/Bonjour2.groovy !
 * </p>
 * 
 * <p>Exemple 2 :
 * <pre>
 * %GPMSI_BASE%\v@PROJECT_VERSION@\gpmsi -extracp %GPMSI_BASE%\v@PROJECT_VERSION@\scripts\groovy -scripturi fr/gpmsi/Bonjour2.groovy
 * </pre>
 * Bonjour depuis le script fr/gpmsi/Bonjour2.groovy !
 * </p>
 */
package fr.gpmsi
println "Bonjour depuis le script fr/gpmsi/Bonjour2.groovy !"
