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
    Dao d1 = new Dao('address')
    d1.pkcol(new CInteger('address_id'))
    d1.col(new CVarchar('city', 64))
    def str = d1.makeTableDdl(nl+'/* extra SQL */'+nl, 'H2')
    //println str
    assertEquals("CREATE TABLE address (${nl}address_id BIGINT PRIMARY KEY,${nl}city VARCHAR(64)$nl/* extra SQL */$nl)" as String, str)
  }

  @Test
  public void testDdlGen2() {
    Dao d1 = new Dao('person')
    d1.pkcol(new CInteger(  'person_id'     ))
    d1.pkcol(new CVarchar(  'last_name',  64))
    d1.col(new CVarchar(    'first_name', 64))
    d1.col(new CDate(       'birthdate'     ))
    def str = d1.makeTableDdl(null, 'H2')
    println str
    //assertEquals("CREATE TABLE address (${nl}address_id BIGINT PRIMARY KEY,${nl}city VARCHAR(64)$nl/* extra SQL */$nl)" as String, str)
  }

  @Test
  public void testDdlGen3() {
    Dao d1 = new Dao('person')
    d1.pkcol(new CInteger(  'person_id', true))
    d1.pkcol(new CVarchar(  'last_name',   64))
    d1.col(new CVarchar(    'first_name',  64))
    d1.col(new CDate(       'birthdate'      ))
    def str = d1.makeTableDdl(null, 'H2')
    println str
    //assertEquals("CREATE TABLE address (${nl}address_id BIGINT PRIMARY KEY,${nl}city VARCHAR(64)$nl/* extra SQL */$nl)" as String, str)
  }

  @Test
  public void testDdlGen4() {
    Dao d1 = new Dao('person')
    d1.pkcol(new CInteger(  'person_id', true))
    d1.col(new CVarchar(    'last_name',   64))
    d1.col(new CVarchar(    'first_name',  64))
    d1.col(new CDate(       'birthdate'      ))
    def str = d1.makeTableDdl(null, 'H2')
    //println str
    def expected = """CREATE TABLE person (
person_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
last_name VARCHAR(64),
first_name VARCHAR(64),
birthdate DATE
)"""
    //println expected
    //assertEquals(expected, str) //ceci ne fonctionne pas à cause des fins de ligne
    assertEquals(StringUtils.normalizeNewlines(expected, System.lineSeparator()), str)
  }

  @Test
  public void testDdlGen5() {
    Dao d1 = new Dao('person')
    d1.col(new CInteger(  'person_id'      ))
    d1.col(new CVarchar(    'last_name',   64))
    d1.col(new CVarchar(    'first_name',  64))
    d1.col(new CDate(       'birthdate'      ))
    def str = d1.makeTableDdl(null, 'H2')
    println str
    def expected = """CREATE TABLE person (
person_id BIGINT,
last_name VARCHAR(64),
first_name VARCHAR(64),
birthdate DATE
)"""
    //println expected
    //assertEquals(expected, str) //ceci ne fonctionne pas à cause des fins de ligne
    assertEquals(StringUtils.normalizeNewlines(expected, System.lineSeparator()), str)
  }

}
