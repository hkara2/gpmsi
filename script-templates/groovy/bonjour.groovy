//:encoding=UTF-8:
gpmsi_home = System.getenv('GPMSI_HOME') ?: 'Non défini'
groovy_ver = GroovySystem.version
java_ver = System.getProperty('java.version')
println "Bonjour, vous êtes dans le script bonjour.groovy sur l'environnement GPMSI / Groovy."
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
println "  frenchDateFormat : $frenchDateFormat"
println "  pmsiDateFormat : $pmsiDateFormat"
println "  isoDateFormat : $isoDateFormat"
