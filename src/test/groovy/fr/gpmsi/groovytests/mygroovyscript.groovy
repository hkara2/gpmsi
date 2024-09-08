package fr.gpmsi.groovytests
import fr.gpmsi.groovytests.MyGroovyGreeter

//Start by saying hello
println "Hello from script"

//Load the greeter that will issue an additional greeting !
MyGroovyGreeter g = new MyGroovyGreeter()
g.greet()
