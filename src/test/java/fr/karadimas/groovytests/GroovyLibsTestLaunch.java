package fr.karadimas.groovytests;

import java.lang.reflect.Method;

public class GroovyLibsTestLaunch {

  public GroovyLibsTestLaunch() {
  }

  public static final void run() {
    //invocation dynamique car Gradle ne "voit" pas LibsTest depuis java pour l'instant, il faut que je trouve le réglage à faire.
    try {
      Class clazz = GroovyLibsTestLaunch.class.forName("fr.karadimas.groovytests.LibsTest");
      if (clazz == null) throw new ClassNotFoundException("Impossible de charger fr.karadimas.groovytests.LibsTest");
      Method m = (Method) clazz.getMethod("runAllTests");
      if (m == null) throw new NoSuchMethodError("Ne trouve pas la methode runAllTests");
      m.invoke(null);
    } catch (Exception e) {
      e.printStackTrace();
    }
    //LibsTest tst = new LibsTest();
    //tst.runAllTests();
    
  }
  public static void main(String[] args)
      throws Exception 
  {
    run();
  }

}
