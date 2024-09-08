package fr.gpmsi.tests;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

import org.junit.Test;

import fr.gpmsi.ObjectFormatter;

public class ObjectFormatterTests {

  @Test
  public void testDateFormat() {
    Calendar cal = Calendar.getInstance();
    cal.clear();
    ObjectFormatter of = new ObjectFormatter();
    cal.set(2023, 11, 31);
    String d31_12_2023 = of.format(cal.getTime());
    assertEquals("31/12/2023", d31_12_2023);
  }

  @Test
  public void testTimeFormat() {
    Calendar cal = Calendar.getInstance();
    cal.clear();
    ObjectFormatter of = new ObjectFormatter();
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 58);
    cal.set(Calendar.SECOND, 59);
    String h23_58_59 = of.format(new Time(cal.getTimeInMillis()));
    assertEquals("23:58:59", h23_58_59);
  }

  @Test
  public void testDateTimeFormat() {
    ObjectFormatter of = new ObjectFormatter();
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(2023, 11, 31);
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 58);
    cal.set(Calendar.SECOND, 59);
    String h2023_12_31_23_58_59 = of.format(new Timestamp(cal.getTimeInMillis()));
    assertEquals("2023-12-31 23:58:59", h2023_12_31_23_58_59);
  }

  @Test
  public void testIntegerNumberFormat() {
    ObjectFormatter of = new ObjectFormatter();
    String n123456 = of.format(123456);
    assertEquals("123456", n123456);
  }
  
  @Test
  public void testFloatNumberFormat() {
    ObjectFormatter of = new ObjectFormatter();
    String f = of.format(3.1415116f);
    //System.out.println(3.1415116f);
    //System.out.println("f:"+f);
    assertEquals("3,1415116", f.substring(0, 9)); //a cause de la représentation interne, la valeur est 3.1415117 (float simple et arrondi) ou 3.1415116786956787 (reconversion en double)
  }
  
  @Test
  public void testDoubleNumberFormat() {
    ObjectFormatter of = new ObjectFormatter();
    String f = of.format(3.1415116d);
    //System.out.println(3.1415116d);
    //System.out.println("f:"+f);
    assertEquals("3,1415116", f); //en double, la représentation est suffisante et on a ce qu'il faut
  }
  
  @Test
  public void testBigDecimalNumberFormat() {
    String bn = "1247441258965647458425885215855125645889554673210145786.115875412178998452467894654654987894E12";
    //           1247441258965647458425885215855125645889554673210145786115875412178,998452467894654654987894
    ObjectFormatter of = new ObjectFormatter();
    String n = of.format(new BigDecimal(bn));
    //System.out.println("n:"+n);
    assertEquals("1247441258965647458425885215855125645889554673210145786115875412178,998452467894654654987894", n); //en double, la représentation est suffisante et on a ce qu'il faut
  }
  
  @Test
  public void testFormatSql() {
    ObjectFormatter of = new ObjectFormatter();
    String i123 = of.formatSql(Integer.valueOf(123), Types.INTEGER);
    assertEquals("123", i123);
  }

}
