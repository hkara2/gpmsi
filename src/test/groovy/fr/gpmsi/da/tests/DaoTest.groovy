package fr.gpmsi.da.tests

import static org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

import fr.gpmsi.da.CDate
import fr.gpmsi.da.CInteger
import fr.gpmsi.da.CVarchar
import fr.gpmsi.da.Dao
import fr.gpmsi.StringUtils

class DaoTest {
  static String nl = System.lineSeparator();
  
  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testDdlGen1() {
    Dao d1 = new Dao('ADDRESS')
    d1.pkcol(new CInteger('ADDRESS_ID'))
    d1.col(new CVarchar('CITY', 64))
    def str = d1.makeTableDdl(nl+'/* extra SQL */', 'H2')
    //println str
    assertEquals("CREATE TABLE IF NOT EXISTS ADDRESS (${nl}ADDRESS_ID BIGINT PRIMARY KEY,${nl}CITY VARCHAR(64)$nl/* extra SQL */$nl)" as String, str)
  }

  @Test
  public void testDdlGen2() {
    Dao d1 = new Dao('PERSON')
    d1.pkcol(new CInteger(  'PERSON_ID'     ))
    d1.pkcol(new CVarchar(  'LAST_NAME',  64))
    d1.col(new CVarchar(    'FIRST_NAME', 64))
    d1.col(new CDate(       'BIRTHDATE'     ))
    def str = d1.makeTableDdl(null, 'H2')
    //println str
    //assertEquals("CREATE TABLE address (${nl}address_id BIGINT PRIMARY KEY,${nl}city VARCHAR(64)$nl/* extra SQL */$nl)" as String, str)
  }

  @Test
  public void testDdlGen3() {
    Dao d1 = new Dao('PERSON')
    d1.pkcol(new CInteger(  'PERSON_ID', true))
    d1.pkcol(new CVarchar(  'LAST_NAME',   64))
    d1.col(new CVarchar(    'FIRST_NAME',  64))
    d1.col(new CDate(       'BIRTHDATE'      ))
    def str = d1.makeTableDdl(null, 'H2')
    //println str
    //assertEquals("CREATE TABLE address (${nl}address_id BIGINT PRIMARY KEY,${nl}city VARCHAR(64)$nl/* extra SQL */$nl)" as String, str)
  }

  @Test
  public void testDdlGen4() {
    Dao d1 = new Dao('PERSON')
    d1.pkcol(new CInteger(  'PERSON_ID', true))
    d1.col(new CVarchar(    'LAST_NAME',   64))
    d1.col(new CVarchar(    'FIRST_NAME',  64))
    d1.col(new CDate(       'BIRTHDATE'      ))
    def str = d1.makeTableDdl(null, 'H2')
    //println str
    def expected = """CREATE TABLE IF NOT EXISTS PERSON (
PERSON_ID BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
LAST_NAME VARCHAR(64),
FIRST_NAME VARCHAR(64),
BIRTHDATE DATE
)"""
    //println expected
    //assertEquals(expected, str) //ceci ne fonctionne pas à cause des fins de ligne
    assertEquals(StringUtils.normalizeLineSeparators(expected, System.lineSeparator()), str)
  }

  @Test
  public void testDdlGen5() {
    Dao d1 = new Dao('PERSON')
    d1.col(new CInteger(  'PERSON_ID'      ))
    d1.col(new CVarchar(    'LAST_NAME',   64))
    d1.col(new CVarchar(    'FIRST_NAME',  64))
    d1.col(new CDate(       'BIRTHDATE'      ))
    def str = d1.makeTableDdl(null, 'H2')
    //println str
    def expected = """CREATE TABLE IF NOT EXISTS PERSON (
PERSON_ID BIGINT,
LAST_NAME VARCHAR(64),
FIRST_NAME VARCHAR(64),
BIRTHDATE DATE
)"""
    //println expected
    //assertEquals(expected, str) //ceci ne fonctionne pas à cause des fins de ligne
    assertEquals(StringUtils.normalizeLineSeparators(expected, System.lineSeparator()), str)
  }

}
