package fr.gpmsi.poi.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.gpmsi.poi.PoiHelper;

public class PoiHelperTestsTest {

  @Test
  public void testGetColumnNumber() {
    assertEquals("colonne A devrait etre 0", 0, PoiHelper.getColumnNumber("A")); //1-1
    assertEquals("colonne AA devrait etre 26", 26, PoiHelper.getColumnNumber("AA")); //27-1
    assertEquals("colonne AAA devrait etre 702", 702, PoiHelper.getColumnNumber("AAA")); //703-1
    assertEquals("colonne AG devrait etre 32", 32, PoiHelper.getColumnNumber("AG")); //33-1
    assertEquals("colonne XFC devrait etre 16382", 16382, PoiHelper.getColumnNumber("XFC")); //16383-1

  }

}
