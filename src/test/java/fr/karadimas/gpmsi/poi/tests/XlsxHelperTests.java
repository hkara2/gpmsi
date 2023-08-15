//:encoding=UTF-8: éèêà
package fr.karadimas.gpmsi.poi.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import org.junit.Test;

import fr.karadimas.gpmsi.poi.XlsxHelper;

public class XlsxHelperTests {

  /**
   * Test création et remplissage d'un fichier xlsx de 3 lignes avec en-tête,
   * et utilisation de divers formats, à l'aide de {@link XlsxHelper}.
   * @throws Exception Si il y a un problème
   */
  @Test
  public void testXlsxHelper() 
  throws Exception
  {
    //Créer et remplir le classeur
    XlsxHelper classeur = new XlsxHelper("Classeur test");
    classeur.addCell("Nom");
    classeur.addCell("Prénom");
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
    File poiDir = new File(testFilesDir, "poi");
    if (!poiDir.exists()) {
      boolean ok = poiDir.mkdir();
      if (!ok) throw new IOException("Impossible de creer le repertoire test-files/poi");
    }
    if (!poiDir.isDirectory()) throw new IOException("'poi' n'est pas un repertoire");
    File destFile = new File(poiDir, "XlsxHelperTestsOutput1.xlsx");
    classeur.setOutput(destFile);
    classeur.writeFileAndClose();
    assertTrue(destFile.exists());
    assertFalse(destFile.isDirectory());
    assertTrue(destFile.length() > 2000);
  }

}
