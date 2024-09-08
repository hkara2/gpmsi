package fr.gpmsi.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.gpmsi.Groovy;

public class StringTable1TestJ {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() throws Exception {
    Groovy g = new Groovy();
    System.out.println("Current dir : " + System.getProperty("user.dir"));
    String[] args = {"-debug", "-script", "src\\test\\groovy\\fr\\gpmsi\\tests\\StringTable1Test.groovy"};
    g.init(args);
    g.run();
  }

}
