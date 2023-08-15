package fr.karadimas.groovytests
import fr.karadimas.groovytests.DynProp

p = new DynProp()
println p.FOO    //will print 2
println p.BAR    //will print 4
println p.BAZ    //will print 6
println p.RANT    //will print 20
println p.FROTZ    //will print Not found : FROTZ

