package fr.gpmsi.groovytests;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.codehaus.groovy.control.CompilerConfiguration;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Application de tests de lecture et configuration de {@link CompilerConfiguration}
 */
public class ConfRead {

	private void test1() {
		GroovyShell sh = new GroovyShell();
		Object result = sh.evaluate("2+2");
		System.out.println("result : "+result);
		result = sh.evaluate("");
		System.out.println("result : "+result);
	}
	
	private void test2() {
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.setScriptBaseClass("fr.gpmsi.groovytests.MyRecall");
		GroovyShell sh = new GroovyShell(cc);
		sh.evaluate("myCall \"my arg\" "); //will print : called with my arg
	}
	
	private void test3() {
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.setScriptBaseClass("fr.gpmsi.groovytests.MyAddresses");
		GroovyShell sh = new GroovyShell(cc);
		sh.evaluate("addresses new fr.gpmsi.groovytests.MyAddress[0] ");
	}
	
	private void test4() {
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.setScriptBaseClass("fr.gpmsi.groovytests.MyAddresses");
		GroovyShell sh = new GroovyShell(cc);
		Object allAddresses = sh.evaluate("address(\"Obama\",\"White House\",\"Washington\") \n"
				+ "address(\"Trump\",\"White House\",\"Washington\") \n"
				+ "allAddresses");
		System.out.println("allAddresses : "+allAddresses);
	}
	
	private void test5() {
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.setScriptBaseClass("fr.gpmsi.groovytests.MyAddresses");
		Binding bnd = new Binding();
		GroovyShell sh = new GroovyShell(bnd, cc);
		Object allAddresses = sh.evaluate("address(\"Obama\",\"White House\",\"Washington\") \n"
				+ "address(\"Trump\",\"White House\",\"Washington\") \n"
				+ "");
		System.out.println("All addresses : " + allAddresses);
		@SuppressWarnings("unchecked")
        Map<String,Object> vars = (Map<String,Object>)bnd.getVariables();
		System.out.println("vars : "+vars);
	}
	
	/**
	 * Test execution of simple closure, just println 'Hello, world !'
	 */
	private void test6() {
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.setScriptBaseClass("fr.gpmsi.groovytests.MyAddresses");
		Binding bnd = new Binding();
		GroovyShell sh = new GroovyShell(bnd, cc);
		sh.evaluate("doClosure { System.out.println('Hello, world !') }"); 
	}
	
	/**
	 * Test execution of a closure to enter a new address
	 */
	private void test7() {
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.setScriptBaseClass("fr.gpmsi.groovytests.MyAddresses");
		Binding bnd = new Binding();
		GroovyShell sh = new GroovyShell(bnd, cc);
		sh.evaluate("declareAddress { \n"
				+ "  setName 'Obama' /* directly calls 'setName' */ \n"
				+ "  street = \"White House\" /* assignment indirectly calls 'setStreet' */ \n"
				+ "  town 'Washington' /* works because there is a method named 'town' */ \n"
				+ "} \n");
		@SuppressWarnings("unchecked")
        Map<String,Object> vars = bnd.getVariables();
		System.out.println("vars : "+vars);
	}
	
	private void test8() {
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.setScriptBaseClass("fr.gpmsi.groovytests.MyAddresses");
		Binding bnd = new Binding();
		GroovyShell sh = new GroovyShell(bnd, cc);
		sh.evaluate("address('Obama', 'White House', 'Washington') \n"
				+ "address('Trump', 'White House', 'Washington') \n"
				+ "printAddress {addr -> System.out.println(\"${addr.name}@${addr.street} in ${addr.town}\") } \n"
				+ "printAllAddresses()");
		//sh.evaluate("printAllAddresses()"); //will not work
	}
	
	/**
	 * Test 9 : execute a script, with a fixed root URL. Not reliable.
	 * @throws IOException
	 * @throws ResourceException
	 * @throws ScriptException
	 */
	private void test9() throws IOException, ResourceException, ScriptException {
		URL rootURL = getClass().getClassLoader().getResource("root.txt");
		System.out.println("rootURL is "+rootURL);
		String root = rootURL.toString();
		root = root.substring(0, root.indexOf("/root.txt"));
		//doesn't work : file:/C:/hkchse/dev/pmsixml/ec-classes/
		//works : file:///C:/hkchse/dev/pmsixml/ec-classes/
		GroovyScriptEngine gse = new GroovyScriptEngine("file:///C:/hkchse/dev/pmsixml/ec-classes/");
		Binding bnd = new Binding();
		gse.run("fr/gpmsi/groovytests/mygroovyscript.groovy", bnd);
	}
	
	/**
	 * Test 10 : execute a script, but using a more reliable way to get root URL
	 * @throws IOException
	 * @throws ResourceException
	 * @throws ScriptException
	 */
	private void test10() 
			throws IOException, ResourceException, ScriptException 
	{
		URL rootURL = getClass().getClassLoader().getResource(""); //this is the root of class loader !
		System.out.println("rootURL is "+rootURL);
		GroovyScriptEngine gse = new GroovyScriptEngine(new URL[]{rootURL});
		Binding bnd = new Binding();
		gse.run("fr/gpmsi/groovytests/mygroovyscript.groovy", bnd);
	}
	
	/**
	 * Lance le test en tant qu'application
	 * @param args Arguments
	 * @throws Exception _
	 */
	public static void main(String[] args) throws Exception {
		ConfRead app = new ConfRead();
		int tn = 10;
		switch (tn) {
        case 1:
          app.test1();
          break;
        case 2:
          app.test2();
          break;
        case 3:
          app.test3();
          break;
        case 4:
          app.test4();
          break;
        case 5:
          app.test5();
          break;
        case 6:
          app.test6();
          break;
        case 7:
          app.test7();
          break;
        case 8:
          app.test8();
          break;
        case 9:
          app.test9();
          break;
        case 10:
          app.test10();
		}
	}
	
}
