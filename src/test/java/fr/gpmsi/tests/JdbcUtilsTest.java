package fr.gpmsi.tests;

import static org.junit.Assert.*;

import java.sql.Types;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.HashMap;

import org.junit.Test;

import fr.gpmsi.JdbcUtils;
import junit.framework.TestFailure;

public class JdbcUtilsTest {

  @Test
  public void testParseText()
      throws ParseException 
  {
    HashMap<String, Object> formatterCache = new HashMap<String, Object>();
    Object obj1 = JdbcUtils.parseText("2025-03-21T15:59:03.5", "TIMESTAMP", null, "dummycol", formatterCache, null);
    System.out.println("Object is " + obj1);
    try {
      Object obj2 = JdbcUtils.parseText("2025-03-21 16:00", "TIMESTAMP", null, "dummycol", formatterCache, null);
      System.out.println("Object is " + obj2);
      fail("Il y aurait dû y avoir une exception ici");
    }
    catch (DateTimeParseException dtpex) {
      //ok, c'est ce qui était prévu
    }
  }

  @Test
  public void testGetTypeNumber() {
    int varcharTypeNr = JdbcUtils.getTypeNumber("VARCHAR");
    assertEquals("Varchar lookup error for VARCHAR", Types.VARCHAR, varcharTypeNr);
    int timestampTypeNr = JdbcUtils.getTypeNumber("TIMESTAMP");
    assertEquals("Varchar lookup error for TIMESTAMP", Types.TIMESTAMP, timestampTypeNr);
    int nonExistentTypeNr = JdbcUtils.getTypeNumber("NONEXISTENTTYPE");
    assertEquals("Nonexistent type mismatch error", Integer.MIN_VALUE, nonExistentTypeNr);
  }

}
