//:encoding=UTF-8: éèêà
package fr.gpmsi.poi.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import org.junit.Test;

import fr.gpmsi.poi.XlsxHelper;

public class XlsxHelperTests {

  /**
   * Test création et remplissage d'un fichier xlsx de 3 lignes avec en-tête,
   * et utilisation de divers formats, à l'aide de {@link XlsxHelper}.
   * Permet aussi de voir si les caractères accentués et les séquences codées de type '_x00EA_'
   * sont bien protégées et pas transformées en caractère unicode.
   * @throws Exception Si il y a un problème
   */
  @Test
  public void testXlsxHelper() 
  throws Exception
  {
    //Créer et remplir le classeur
    XlsxHelper classeur = new XlsxHelper("Classeur test");
    classeur.addCell("Nom");
    classeur.addCell("Prénom_x00EA_.");
    classeur.addCell("Date de naissance");
    classeur.addCell("Taille (m)");
    classeur.addCell("Décédé");
    classeur.newRow();
    classeur.addCell("OBAMA");
    classeur.addCell("Barack"); 
    Calendar ddn = Calendar.getInstance();
    ddn.set(1961, 07, 04); //4 aout 1961
    classeur.addCell(ddn); //s'affichera en 1961-08-04
    classeur.addCell(1.87); //s'affichera en 1,87 (sur Excel en français)
    classeur.addCell(false);
    classeur.newRow();
    classeur.addCell("LINCOLN");
    classeur.addCell("Abraham");
    ddn = Calendar.getInstance();
    ddn.set(1809, 01, 12); //12 fev 1809 -> attention ça ne marchera pas dans excel !
    classeur.addCell("1809-02-12"); //s'affichera correctement, mais ce sera du texte !
    classeur.addCell(1.93, "0.0"); //ne mettre qu'une décimale, s'affichera en 1,9 (sur Excel en français)
    classeur.addCell(true);
    classeur.newRow();
    classeur.addCell("MITTERRAND");
    classeur.addCell("Francois");
    ddn = Calendar.getInstance();
    ddn.set(1916, 10, 26); //26 oct 1916
    classeur.addCell(ddn, "d/m/yy"); //mettre date courte française, s'affichera en 26/11/16
    classeur.addCell(1.72, "0.00E+00"); //mettre notation scientifique, s'affichera en 1,72E+00 (sur Excel en français)
    classeur.addCell(true);
    classeur.newRow();
    //maintenant, écrire le classeur dans le fichier de destination
    File testFilesDir = new File("test-files");
    if (!testFilesDir.exists()) throw new FileNotFoundException("Repertoire test-files n'existe pas");
    File tmpOutDir = new File(testFilesDir, "tmp-out");
    if (!tmpOutDir.exists()) {
      boolean ok = tmpOutDir.mkdir();
      if (!ok) throw new IOException("Impossible de creer le repertoire " + tmpOutDir);
    }
    if (!tmpOutDir.isDirectory()) throw new IOException("'tmp-out' n'est pas un repertoire");
    File destFile = new File(tmpOutDir, "XlsxHelperTestsOutput1.xlsx");
    classeur.setOutput(destFile);
    classeur.writeFileAndClose();
    assertTrue(destFile.exists());
    assertFalse(destFile.isDirectory());
    assertTrue(destFile.length() > 2000);
  }
  
  @Test
  public void testEscaping() {
    XlsxHelper classeur = new XlsxHelper("Classeur test");
    System.out.println();
    String expected = "Sp_x005F_x00EA_cialité de la maison _x005F_x00eb_.";
    String encoded = classeur.msUtfEncode("Sp_x00EA_cialité de la maison _x00eb_.");
    assertEquals("Les caractères auraient dû être échappés", expected, encoded);
  }

}
