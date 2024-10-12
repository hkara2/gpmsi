package fr.gpmsi.ccam.tests;

import static org.junit.Assert.*;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import fr.gpmsi.ccam.SharedCcam;

/**
 * Tests sur codes Ccam
 */
public class CcamTests {

  /** Constructeur simple */
  public CcamTests() {
    // TODO Auto-generated constructor stub
  }
  /**
   * Recherche simple d'un code "LCQ.001" qui doit ramener les codes "LCQK001", "LCQH001", "LCQN001", "LCQJ001"
   * @throws Exception _
   */
  @Test
  public void testCcamSearch1()
      throws Exception 
  {
    SharedCcam.PROFILING = true; //mettre à true pour avoir les temps de chargement
    SharedCcam.getOrLoadCcam(); //charger CCAM pour que le temps de chargement ne nous parasite pas
    Pattern p = Pattern.compile("LCQ.001");
    long tic = System.currentTimeMillis();
    List<String> codes = SharedCcam.findCodes7(p);
    long tac = System.currentTimeMillis();
    System.err.println("Temps de recherche : "+(tac-tic)+"ms");
    //for (String code : codes) {
      //System.out.println(code);
    //}
    String[] expecteds = {"LCQK001", "LCQH001", "LCQN001", "LCQJ001",};
    
    assertArrayEquals("6 codes attendus", expecteds, codes.toArray());
  }
  

  /**
   * Test recherche de codes "FCFC003-.+". Doit ramener "FCFC003-30", "FCFC003-40"
   * @throws Exception
   */
  @Test
  public void testCcamSearch2() 
      throws Exception
  {
    //pas besoin d'appeler SharedCcam.getOrLoadCcam()
    Pattern p = Pattern.compile("FCFC003-.+");
    List<String> codes = SharedCcam.findCodesp(p);
    String[] expected = {"FCFC003-30", "FCFC003-40",};
    //System.out.println(codes);
    assertArrayEquals("codes attendus "+expected, expected, codes.toArray());
  }

  /**
   * Teste si le code "FCFC003-40" qui doit exister.
   * @throws Exception _
   */
  @Test
  public void testCcamCodeExists1() throws Exception {
    boolean FCFC003_40_exists = SharedCcam.codeExists("FCFC003-40");
    assertTrue("FCFC003-40 should exist", FCFC003_40_exists);
  }
  
  /**
   * Teste le code "FCFC003-50" qui ne doit pas exister.
   * @throws Exception _
   */
  @Test
  public void testCcamCodeExists2()
      throws Exception 
  {
    boolean FCFC003_50_exists = SharedCcam.codeExists("FCFC003-50");
    assertFalse("FCFC003-50 should not exist", FCFC003_50_exists);
  }
  
  /**
   * Teste le code "FCFC003" qui doit exister.
   * @throws Exception _
   */
  @Test
  public void testCcamCodeExists3()
      throws Exception
  {
    boolean FCFC003_exists = SharedCcam.codeExists("FCFC003");
    assertTrue("FCFC003 should exist", FCFC003_exists);
  }
  
  /**
   * Teste le code "FCFC00" qui ne doit pas exister.
   * @throws Exception _
   */
  @Test
  public void testCcamCodeExists4()
      throws Exception
  {
    boolean FCFC00_exists = SharedCcam.codeExists("FCFC00");
    assertFalse("FCFC00 should not exist", FCFC00_exists);
  }
  
  /**
   * Tester que la recherche de code "Fcfc003" fonctionne bien même en minuscules
   * @exception Exception Si erreur
   */
  @Test
  public void testCcamCodeExists5()
      throws Exception
  {
    boolean FCFC003_exists = SharedCcam.codeExists("Fcfc003");
    assertTrue("FCFC003 should exist", FCFC003_exists);
  }
  
}
