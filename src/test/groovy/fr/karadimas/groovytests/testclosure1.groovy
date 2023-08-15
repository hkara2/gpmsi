package fr.karadimas.groovytests
import fr.karadimas.groovytests.Closure1Tester

Closure1Tester t = new Closure1Tester()

t.foo = "mary"
t.bar = "girl"
t.baz = 23

println t.testClosure {a, b, c ->
  a + " is a " + b + " aged " + c
}