package fr.karadimas.gpmsi.tests;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import fr.karadimas.gpmsi.Groovy;

/**
 * Tests simples de traitement d'une feuille Excel 'xl1.xlsx'.
 * Voici le contenu de la feuille Excel tel qu'affiché par Excel 2019 :
 * <pre>
 * |a           |B           |c         |d with space     |é        |f |     |            |
 * |1           |2           |3         |4                |5        |6 |     |            |
 * |01/01/1900  |24/08/2023  |14:54     |24/08/2023 13:52 |un texte |  |     |            |
 * |            |autre texte |3,1415116 |   1,29E+130     |         |  |     |            |
 * |            |            |          |                 |         |  |     |            |
 * |            |            |          |                 |         |  |     |it's outside|
 * </pre>
 * Ne rien modifier dans cette feuille Excel sinon les tests risqueraient de casser !
 * A noter que la valeur qui a été entrée à la main est 1,285E130, et c'est Excel qui affiche 1,29E+130.
 * De même la date qui a été saisie est 24/08/2023 13:52:59 mais c'est Excel qui affiche 24/08/2023 13:52.
 * @author hkaradimas
 *
 */
public class XlBasicTests {

  /**
   * Tester qu'on a bien 6 lignes retrouvées dans le fichier Excel de test.
   * @throws Exception
   */
  @Test
  public void testXlpoi1()
      throws Exception
  {
    String[] args = {"-script", "test-files/test_scripts/Xlpoi1Test.groovy"}; 
    Groovy g = new Groovy();
    g.init(args);
    g.run();
    assertEquals("Il y a 6 lignes dans le fichier de test", 6, g.getReturnedObject());
  }

  /**
   * Tester qu'en ligne 4 colonne B on a bien la valeur "autre texte"
   * @throws Exception
   */
  @Test
  public void testXlpoi2()
      throws Exception
  {
    String[] args = {"-script", "test-files/test_scripts/Xlpoi2Test.groovy"}; 
    Groovy g = new Groovy();
    g.init(args);
    g.run();
    assertEquals("La valeur doit etre 'autre texte'", "autre texte", g.getReturnedObject());
  }

  /**
   * Tester qu'en ligne 3 colonne "é" on a bien la valeur "un texte"
   * @throws Exception
   */
  @Test
  public void testXlpoi3()
      throws Exception
  {
    String[] args = {"-script", "test-files/test_scripts/Xlpoi3Test.groovy"}; 
    Groovy g = new Groovy();
    g.init(args);
    g.run();
    assertEquals("La valeur doit etre 'autre texte'", "un texte", g.getReturnedObject());
  }

  /**
   * Tester qu'en ligne 4 colonne "d with space" on a bien un objet de type Double et qui
   * a pour valeur 1.285E130
   * @throws Exception
   */
  @Test
  public void testXlpoi4()
      throws Exception
  {
    String[] args = {"-script", "test-files/test_scripts/Xlpoi4Test.groovy"}; 
    Groovy g = new Groovy();
    g.init(args);
    g.run();
    assertTrue("L'objet retourné doit être un Double", g.getReturnedObject() instanceof Double);
    assertEquals("La valeur doit etre 1.285E130", 1.285E130, g.getReturnedObject());
  }

  /**
   * Tester que le nombre de colonnes retourné est bien 6
   * @throws Exception
   */
  @Test
  public void testXlpoi5()
      throws Exception
  {
    String[] args = {"-script", "test-files/test_scripts/Xlpoi5Test.groovy"}; 
    Groovy g = new Groovy();
    g.init(args);
    g.run();
    assertEquals("La valeur doit etre 6", 6, g.getReturnedObject());
  }

  /**
   * Tester qu'on accède quand même à la valeur qui est hors de la plage qui a des titres.
   * Cette valeur est en ligne 6 colonne 8
   * @throws Exception
   */
  @Test
  public void testXlpoi6()
      throws Exception
  {
    String[] args = {"-script", "test-files/test_scripts/Xlpoi6Test.groovy"}; 
    Groovy g = new Groovy();
    g.init(args);
    g.run();
    assertEquals("La valeur doit etre 'it's outside'", "it's outside", g.getReturnedObject());
  }

  /**
   * Tester que la ligne 5 est bien reconnue comme vide
   * @throws Exception
   */
  @Test
  public void testXlpoi7()
      throws Exception
  {
    String[] args = {"-script", "test-files/test_scripts/Xlpoi7Test.groovy"}; 
    Groovy g = new Groovy();
    g.init(args);
    g.run();
    assertEquals("La valeur de test de ligne vide doit etre true", true, g.getReturnedObject());
  }

  /**
   * Tester qu'en ligne 3 de la colonne 'd with space' on a bien une date.
   * Tester aussi avec la conversion en LocalDateTime, avec les objets Date traditionnels.
   * @throws Exception
   */
  @Test
  public void testXlpoi8()
      throws Exception
  {
    String[] args = {"-script", "test-files/test_scripts/Xlpoi8Test.groovy"}; 
    Groovy g = new Groovy();
    g.init(args);
    g.run();
    Calendar cal = Calendar.getInstance();
    cal.clear(); //important !
    cal.set(2023, 7, 24, 13, 52, 59);
    Date expectedDate = cal.getTime();
    //System.out.println("Returned object : " + g.getReturnedObject());
    //System.out.println("expectedDate:" + expectedDate);
    //System.out.println("dates comparison test : " + expectedDate.equals(g.getReturnedObject()));
    Date returnedDate = (Date) g.getReturnedObject();
    //System.out.println("Returned ms : " + returnedDate.getTime());
    //System.out.println("exected ms  : " + expectedDate.getTime());
    assertEquals("La valeur 'd with space' en ligne 3 doit être la date du 24 aout 2023 13h52", expectedDate, g.getReturnedObject());
    //le même test, mais avec conversion en LocalDateTime, qui est compliquée à partir des 'Date'
    LocalDateTime expectedDt = LocalDateTime.of(2023, 8, 24, 13, 52, 59);
    System.out.println("expectedDt ms : " + expectedDt.getLong(ChronoField.MILLI_OF_DAY));
    LocalDateTime returnedDt = LocalDateTime.ofInstant(returnedDate.toInstant(), ZoneOffset.systemDefault()); //ZoneOffset.systemDefault() donne la même Time Zone que celle que le test a utilisé pour créer expectedDt
    System.out.println("returned ms   : " + returnedDt.getLong(ChronoField.MILLI_OF_DAY));
    System.out.println("Default Zone : " + ZoneOffset.systemDefault()); //Europe/Paris
    assertEquals("La valeur 'd with space' en ligne 3 doit être la date du 24 aout 2023 13h52 (y compris en utilisant la nouvelle API java.time)", 
                 expectedDt, 
                 returnedDt);
  }

  /**
   * Tester qu'en ligne 3 de la colonne 'd with space' on a bien une LocalDateTime, avec la nouvelle
   * API java.time
   * @throws Exception
   */
  @Test
  public void testXlpoi9()
      throws Exception
  {
    String[] args = {"-script", "test-files/test_scripts/Xlpoi9Test.groovy"}; 
    Groovy g = new Groovy();
    g.init(args);
    g.run();
    //le même test, mais avec LocalDateTime, plus simple ici
    LocalDateTime expectedDt = LocalDateTime.of(2023, 8, 24, 13, 52, 59);
    System.out.println("expectedDt ms : " + expectedDt.getLong(ChronoField.MILLI_OF_DAY));
    LocalDateTime returnedDt = (LocalDateTime) g.getReturnedObject();
    System.out.println("returned ms   : " + returnedDt.getLong(ChronoField.MILLI_OF_DAY));
    assertEquals("La valeur 'd with space' en ligne 3 doit être la date du 24 aout 2023 13h52 (avec la nouvelle API java.time)", 
                 expectedDt, 
                 returnedDt);
  }

}
