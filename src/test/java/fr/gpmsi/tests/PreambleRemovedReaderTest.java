package fr.gpmsi.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import fr.gpmsi.PreambleRemovedReader;

/**
 * Tests pour {@link PreambleRemovedReader}
 */
public class PreambleRemovedReaderTest {

  /**
   * Test avec deux lignes de préambule et une ligne de données :
   * <pre>
   * a Line A
   * b LineB
   * 
   * Line C
   * </pre>
   * Lit le premier caractère qui doit être "L"
   * @throws IOException
   */
  @Test
  public void testRead1()
    throws IOException 
  {
    StringReader sr = new StringReader("a Line A\nb LineB\n\nLine C\n");
    PreambleRemovedReader r = new PreambleRemovedReader(sr);
    int c = r.read();
    //S ystem.out.println("c:"+c);
    r.close();
    assertEquals("Attendu : 'L'", 'L', c);
  }

  /**
   * Tester si ça fonctionne encore si il y a une fin de ligne pas comme les autres et une ligne non vide
   * mais avec juste des espaces
   * @exception IOException _
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
   * @exception IOException _
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
   * @exception IOException _
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

  /**
   * Test de lecture dans un tableau de 2 caractères.
   * @throws IOException _
   */
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

  /**
   * Test de lecture dans tableau de 4 caractères.
   * Il y a 3 caractères de lus.
   * @throws IOException _
   */
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

  /**
   * Test de lecture avec {@link PreambleRemovedReader}.
   * @throws IOException _
   */
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
