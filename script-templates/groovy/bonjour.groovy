//☺:encoding=UTF-8:

/**
 * Dire bonjour, histoire de montrer que l'environnement de script fonctionne.
 * Si on appelle le script avec le flag 'details', on a des informations
 * supplementaires sur l'environnement et les variables prédéfinies dans
 * l'environnement gpmsi.
 */
gpmsi_home = System.getenv('GPMSI_HOME') ?: 'Non défini'
groovy_ver = GroovySystem.version
java_ver = System.getProperty('java.version')
//le "à" s'affiche correctement dans la fenêtre CMD, mais pas le smiley ☺ car la
//fenêtre de terminal ne gère pas l'Unicode
println ""
println "Un petit bonjour à tous ☺ depuis le script bonjour.groovy, dans l'environnement GPMSI / Groovy."
println ""
if (flags.contains('details')) {
    println "Détails d'exécution :"
    println ""
    println "Variable d'environnement GPMSI_HOME : $gpmsi_home"
    println "Version de Groovy : $groovy_ver"
    println "Version de Java : $java_ver"
    println "Variables prédéfinies :"
    println "  args : $args"
    println "  flags : $flags"
    println "  lg : $lg"
    println "  scriptPath : $scriptPath"
    println "  nl : $nl"
    println "  userHome : $userHome"
    println "  frenchDateFormat : ${frenchDateFormat.toPattern()}"
    println "  pmsiDateFormat : ${pmsiDateFormat.toPattern()}"
    println "  isoDateFormat : ${isoDateFormat.toPattern()}"
}
