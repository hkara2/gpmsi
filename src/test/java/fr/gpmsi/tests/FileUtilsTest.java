package fr.gpmsi.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import fr.gpmsi.FileUtils;

/** tests */
public class FileUtilsTest {

  /**
   * Test de lecture du premier .tra.txt du fichier out.zip qui est dans les fichiers de test.
   * @throws IOException si erreur E/S
   */
  @Test
  public void testFindFirstInZip()
      throws IOException 
  {
    String testFilePath = "test-files/inout/910019447.2024.0.20250130112254.out.zip";
    File testFile = new File(testFilePath);
    String testFileCanonicalPath = testFile.getCanonicalPath();
    if (!testFile.exists()) fail("fichier non trouve : '"+testFileCanonicalPath+"'");
    Path tra = FileUtils.findFirstInZip(testFile, "*.tra.txt");
    if (tra != null) {
      //System.out.println("Trouve:"+tra);
      Reader rdr = Files.newBufferedReader(tra);
      String content = FileUtils.toString(rdr);
      //System.out.println("Contenu : "+content);
      //System.out.println("Contenu :");
      //System.out.println(content.trim());
      assertTrue("Le fichier devrait commencer par 0000000001;308461", content.startsWith("0000000001;308461"));
    }
    else fail("*.tra.txt Non trouve dans le zip : "+testFileCanonicalPath);
  }

}
