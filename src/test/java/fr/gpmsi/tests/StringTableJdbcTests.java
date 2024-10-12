package fr.gpmsi.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.gpmsi.StringTable;
import fr.gpmsi.poi.XlsxHelper;

/**
 * Test de remplissage d'une StringTable à partir d'une connexion jdbc.
 */
public class StringTableJdbcTests {
  static Connection dbcxn;
  static final String TEST_URL = "jdbc:h2:mem:test_mem";
  
  static Date makeDate(int year, int month, int day) {
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(year, month, day);
    return new Date( cal.getTimeInMillis() );
  }
  
  static Time makeTime(int h, int m, int s) {
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(Calendar.HOUR_OF_DAY, h);
    cal.set(Calendar.MINUTE, m);
    cal.set(Calendar.SECOND, s);
    return new Time( cal.getTimeInMillis() );    
  }
  
  static Timestamp makeTimestamp(int year, int month, int day, int h, int m, int s) {
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(year, month, day);
    cal.set(Calendar.HOUR_OF_DAY, h);
    cal.set(Calendar.MINUTE, m);
    cal.set(Calendar.SECOND, s);
    return new Timestamp( cal.getTimeInMillis() );    
  }
  
  /**
   * Préparation d'une base H2 en mémoire
   * @throws Exception _
   */
  @Before
  public void setUp() throws Exception {
    if (dbcxn != null) return;
    try {
      dbcxn = DriverManager.getConnection(TEST_URL, "sa","");
      System.out.println("Connected to test_mem");
      Statement stmnt = dbcxn.createStatement();
      stmnt.execute("CREATE TABLE FOO(BAR VARCHAR(20),DAT DATE,TIM TIME, DT DATETIME, AMNT INTEGER, FLT FLOAT, BD NUMERIC(20,5))");
      stmnt.execute("INSERT INTO FOO(BAR,DAT,TIM,DT,AMNT,FLT,BD) VALUES ('abc', {d '2023-01-02'}, {t '00:01:02'}, {ts '2023-01-03 01:02:03'}, 321, 3.1415, 23242526272829.98765)");
      stmnt.execute("INSERT INTO FOO(BAR,DAT,TIM,DT,AMNT,FLT,BD) VALUES ('bcd', {d '2023-01-03'}, {t '00:01:03'}, {ts '2023-01-03 01:02:04'}, 322, 3.1416, 23242526272829.98766)");
      stmnt.execute("INSERT INTO FOO(BAR,DAT,TIM,DT,AMNT,FLT,BD) VALUES ('cde', {d '2023-01-04'}, {t '00:01:04'}, {ts '2023-01-03 01:02:05'}, 323, 3.1417, 23242526272829.98767)");
      stmnt.execute("INSERT INTO FOO(BAR,DAT,TIM,DT,AMNT,FLT,BD) VALUES ('def', {d '2023-01-05'}, {t '00:01:05'}, {ts '2023-01-03 01:02:06'}, 324, 3.1418, 23242526272829.98768)");
      dbcxn.commit();
    }
    finally {
      //conn.close(); //non, laisser la connexion ouverte
    }
  }
  
  /**
   * Démontage (ne fait rien)
   */
  @After
  public void tearDown() {
    
  }
  
  /**
   * Constructeur simple (ne fait rien)
   */
  public StringTableJdbcTests() {
  }

  /**
   * Requête basique pour vérifier les valeurs
   * @throws Exception
   */
  @Test
  public void testQuery1()
      throws Exception 
  {
    PreparedStatement ps = dbcxn.prepareStatement("SELECT DAT,TIM,DT,AMNT,FLT,BD FROM FOO WHERE BAR=?");
    ps.setString(1, "abc");
    ResultSet rs = ps.executeQuery();
    assertTrue(rs.first());
    Date dat = rs.getDate("DAT");
    Time tim = rs.getTime("TIM");
    Timestamp dt = rs.getTimestamp("DT");
    Integer amnt = rs.getInt("AMNT");
    if (rs.wasNull()) amnt = null;
    Double flt = rs.getDouble("FLT");
    if (rs.wasNull()) flt = null;
    BigDecimal bd = rs.getBigDecimal("BD");    
    assertEquals(makeDate(2023, 0, 2), dat); //0 est le mois de janvier dans les dates java ...
    assertEquals(makeTime(0, 1, 2), tim);
    //3.1415, )");
    assertEquals(makeTimestamp(2023, 0, 3, 1, 2, 3), dt);
    assertNotNull(amnt);
    assertEquals(321, (int)amnt);
    assertEquals(3.1415, (double)flt, 4.9E-324d);
    assertEquals(new BigDecimal("23242526272829.98765"), bd);
  }
  
  /**
   * Test du résultat formaté par défaut. Marche sur Windows, non testé sur Linux ou Mac (mais ça devrait)
   * @throws Exception _
   */
  @Test
  public void testJdbcRead1()
		  throws Exception 
  {
    PreparedStatement ps = dbcxn.prepareStatement("SELECT BAR as baz,DAT,TIM,DT,AMNT,FLT,BD FROM FOO");
    ResultSet rs = ps.executeQuery();
    StringTable st = new StringTable("mytest");
    st.readFrom(rs);
    StringBuffer sb = new StringBuffer();
    st.prettyPrintTo(sb);
    System.out.println(""+sb);
    String NL = System.lineSeparator();
    String expected = "BAZ DAT        TIM      DT                  AMN FLT    BD                  " + NL
        + "abc|02/01/2023|00:01:02|2023-01-03 01:02:03|321|3,1415|23242526272829,98765" + NL
        + "bcd|03/01/2023|00:01:03|2023-01-03 01:02:04|322|3,1416|23242526272829,98766" + NL
        + "cde|04/01/2023|00:01:04|2023-01-03 01:02:05|323|3,1417|23242526272829,98767" + NL
        + "def|05/01/2023|00:01:05|2023-01-03 01:02:06|324|3,1418|23242526272829,98768" + NL;
    assertEquals(expected, sb.toString());
  }
  
  /**
   * Ce test fait juste le fichier, rien d'autre.
   * A noter que les valeurs de BD sont arrondies et ont perdu leurs décimales : 23242526272830
   * Donc se méfier des colonnes avec beaucoup de chiffres.
   * Dans le futur il faudrait les convertir en String.
   * @throws Exception
   */
  @Test
  public void testXlsxWithJdbc1()
      throws Exception 
  {
    XlsxHelper xh = new XlsxHelper("onglettest");
    xh.setOutput(new File("test-files/tmp-out/test_jdbc_read1.xlsx"));
    PreparedStatement ps = dbcxn.prepareStatement("SELECT BAR as baz,DAT,TIM,DT,AMNT,FLT,BD FROM FOO");
    ResultSet rs = ps.executeQuery();
    xh.addFrom(rs);
    xh.writeFileAndClose();
  }
  
}
