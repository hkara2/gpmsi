package fr.gpmsi.groovytests;

import java.lang.reflect.Method;

/**
 * Lancement dynamique des tests (pour exécution à travers Gradle)
 */
public class GroovyLibsTestLaunch {

  /**
   * Constructeur simple
   */
  public GroovyLibsTestLaunch() {
  }

  /**
   * Lancement des tests
   */
  public static final void run() {
    //invocation dynamique car Gradle ne "voit" pas LibsTest depuis java pour l'instant, il faut que je trouve le réglage à faire.
    try {
      Class<?> clazz = Class.forName("fr.gpmsi.groovytests.LibsTest");
      if (clazz == null) throw new ClassNotFoundException("Impossible de charger fr.gpmsi.groovytests.LibsTest");
      Method m = (Method) clazz.getMethod("runAllTests");
      if (m == null) throw new NoSuchMethodError("Ne trouve pas la methode runAllTests");
      m.invoke(null);
    } catch (Exception e) {
      e.printStackTrace();
    }
    //LibsTest tst = new LibsTest();
    //tst.runAllTests();
    
  }
  
  /**
   * Lancement des tests en tant qu'application.
   * Appelle juste la méthode run()
   * @param args
   * @throws Exception
   */
  public static void main(String[] args)
      throws Exception 
  {
    run();
  }

}
