package fr.karadimas.gpmsi.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import fr.karadimas.gpmsi.PreambleRemovedReader;

public class PreambleRemovedReaderTest {

  @Test
  public void testRead1() throws IOException {
    StringReader sr = new StringReader("Line A\nLineB\n\nLine C\n");
    PreambleRemovedReader r = new PreambleRemovedReader(sr);
    int c = r.read();
    //S ystem.out.println("c:"+c);
    r.close();
    assertEquals("Attendu : 'L'", 'L', c);
  }

  /**
   * Tester si ça fonctionne encore si il y a une fin de ligne pas comme les autres et une ligne non vide
   * mais avec juste des espaces
   */
  @Test
  public void testRead2()
      throws IOException 
  {
    StringReader sr = new StringReader("Line A\nLineB\r\n    \nZLine C\n");
    PreambleRemovedReader r = new PreambleRemovedReader(sr);
    int c = r.read();
    //S ystem.out.println("c:"+c);
    r.close();
    assertEquals("Attendu : 'Z'", 'Z', c);
  }

  /**
   * Tester si rien n'est lu lorsqu'il n'y a pas de ligne vide
   */
  @Test
  public void testRead3()
      throws IOException 
  {
    StringReader sr = new StringReader("Line A\nLineB\r\nZLine C");
    PreambleRemovedReader r = new PreambleRemovedReader(sr);
    int c = r.read();
    //S ystem.out.println("c:"+c);
    r.close();
    assertEquals("Attendu : -1", -1, c);
  }

  /**
   * Tester Le cas où la ligne vide est la dernière ligne (doit ramener -1, fin de fichier)
   */
  @Test
  public void testRead4()
      throws IOException 
  {
    StringReader sr = new StringReader("Line A\nLineB\r\nZLine C\r\n     \r\n");
    PreambleRemovedReader r = new PreambleRemovedReader(sr);
    int c = r.read();
    //S ystem.out.println("c:"+c);
    r.close();
    assertEquals("Attendu : -1", -1, c);
  }

  @Test
  public void testReadCharArray1()
      throws IOException 
  {
    StringReader sr = new StringReader("Line A\nLineB\r\nZLine C\r\n     \r\nxyz");
    PreambleRemovedReader r = new PreambleRemovedReader(sr);
    char[] buf = new char[2];
    int rc = r.read(buf);
    //S ystem.out.println("rc:"+rc);
    r.close();
    assertEquals("Attendu : 2", 2, rc);
  }

  @Test
  public void testReadCharArray2()
      throws IOException 
  {
    StringReader sr = new StringReader("Line A\nLineB\r\nZLine C\r\n     \r\nxyz");
    PreambleRemovedReader r = new PreambleRemovedReader(sr);
    char[] buf = new char[4];
    int rc = r.read(buf);
    //S ystem.out.println("rc:"+rc);
    r.close();
    assertEquals("Attendu : 3", 3, rc);
  }

  @Test
  public void testReadCharArrayIntInt()
      throws IOException 
  {
    StringReader sr = new StringReader("Line A\nLineB\r\nZLine C\r\n     \r\nxyz");
    PreambleRemovedReader r = new PreambleRemovedReader(sr);
    char[] buf = new char[4];
    int rc = r.read(buf, 2, 2);
    //S ystem.out.println("rc:"+rc);
    r.close();
    assertEquals("Attendu : 2", 2, rc);
  }

}
