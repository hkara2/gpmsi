package fr.karadimas.groovytests;

import java.net.URL;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

/**
 * Exécuter un script Groovy qui se trouve dans le classpath.
 * Exemple :
 * <code>
 * GroovyExec fr/karadimas/groovytests/mygroovyscript.groovy
 * </code>
 * <br>
 * Other example :
 * <code>
 * GroovyExec fr/karadimas/groovytests/adder.groovy 2 2
 * </code>
 * 
 * @author hkaradimas
 *
 */
public class GroovyExec {

	public static void main(String[] args)
			throws Exception
	{
		if (args.length < 1) {
			System.err.println("Usage : GroovyExec scriptpath [arg [arg ...]]");
			return;
		}
		String scriptPath = args[0];
		URL rootURL = GroovyExec.class.getClassLoader().getResource(""); //this is the root of class loader !
		//S ystem.out.println("rootURL is "+rootURL);
		GroovyScriptEngine gse = new GroovyScriptEngine(new URL[]{rootURL});
		Binding bnd = new Binding();
		String[] scriptArgs = new String[args.length - 1];
		System.arraycopy(args, 1, scriptArgs, 0, scriptArgs.length);
		bnd.setVariable("args", scriptArgs);
		gse.run(scriptPath, bnd);
	}

}
