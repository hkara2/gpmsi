package fr.gpmsi.tests;

import java.time.temporal.ChronoUnit;

import org.junit.Test;

import fr.gpmsi.Chrono;

/**
 * Tests pour l'obje Chrono
 */
public class ChronoTests {
  private static boolean emitDebugMessages = false;

  /**
   * Test de recul de la marque de 10 secondes
   * @throws Exception _
   */
  @Test
  public void testChrono1()
      throws Exception
  {
    Chrono c1 = new Chrono();
    c1.setMark(c1.getMark() - 10_000); //reculer de 10 secondes
    if (emitDebugMessages) System.out.println("c1:" + c1.messageEn("Temps passé : ", ChronoUnit.SECONDS) + c1.messageEn(", soit ", ChronoUnit.MILLIS, "."));
  }
  
  /**
   * Test de recul de la marque de 1 seconde
   * @throws Exception _
   */
  @Test
  public void testChrono2()
      throws Exception
  {
    Chrono c2 = new Chrono();
    c2.setMark(c2.getMark() - 1000); //reculer de 1 seconde
    if (emitDebugMessages) System.out.println("c2:" + c2.messageFr("Temps passé : ", ChronoUnit.SECONDS) + c2.messageFr(", soit ", ChronoUnit.MILLIS, "."));
  }
  
}
