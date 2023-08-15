package fr.karadimas.gpmsi.cim.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import fr.karadimas.gpmsi.cim.SharedCim10;

public class Cim10Tests {

  @Test
  public void testCim10Search1()
      throws Exception 
  {
    SharedCim10.PROFILING = true; //mettre à true pour avoir les temps de chargement
    SharedCim10.getOrLoadCim10(); //charger CIM10 pour que le temps de chargement ne nous parasite pas
    Pattern p = Pattern.compile("A03.+");
    long tic = System.currentTimeMillis();
    List<String> codes = SharedCim10.findCodes(p);
    long tac = System.currentTimeMillis();
    System.err.println("Temps de recherche : "+(tac-tic)+"ms");
    //for (String code : codes) {
      //System.out.println(code);
    //}
    String[] expecteds = {"A030", "A031", "A032", "A033", "A038", "A039",};
    
    assertArrayEquals("6 codes attendus", expecteds, codes.toArray());
  }

  @Test
  public void testCodeExists1() throws Exception {
    //System.err.println("A035 existe : "+r);
    assertFalse("A035 n'existe pas", SharedCim10.codeExists("A035"));
    assertTrue("A032 existe", SharedCim10.codeExists("A032"));
  }
  
  @Test
  public void testCim10Search2()
      throws Exception 
  {
    List<String> codes = SharedCim10.findCodes("a03", "A04", false);
    //for (String code : codes) {
      //System.out.println(code);
    //}
    String[] expecteds = {"A03","A030","A031","A032","A033","A038","A039"};
    assertArrayEquals("7 codes attendus", expecteds, codes.toArray());
  }
    
  @Test
  public void testCim10Search3()
      throws Exception 
  {
    List<String> codes = SharedCim10.findCodes("a03", "A03.8", true);
    for (@SuppressWarnings("unused") String code : codes) {
      //System.out.println(code);
    }
    String[] expecteds = {"A03","A030","A031","A032","A033","A038"};
    assertArrayEquals("6 codes attendus", expecteds, codes.toArray());
  }
    
}
