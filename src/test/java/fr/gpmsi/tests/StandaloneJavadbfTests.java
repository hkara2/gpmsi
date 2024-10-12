package fr.gpmsi.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import com.linuxense.javadbf.DBFDataType;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;

/**
 * Tests de {@link DBFReader}
 */
public class StandaloneJavadbfTests {

  static final String DBF_R_ACTE_FORFAIT = "test-files\\in\\R_ACTE_FORFAIT.dbf";
  
  /**
   * Test simple d'ouverture d'un fichier DBF et vérification qu'il y a bien 3 champs.
   * @throws FileNotFoundException _
   * @throws IOException _
   */
  @Test
  public void testFileOpen()
      throws FileNotFoundException, IOException
  {
    try (FileInputStream fis = new FileInputStream(new File(DBF_R_ACTE_FORFAIT))) {
      DBFReader drdr = new DBFReader(fis);
      int nrFields = drdr.getFieldCount();
      //System.out.println("Nr of fields : " + nrFields);
      assertEquals("Il doit y avoir exactement 3 champs", 3, nrFields);
      for (int i = 0; i < 3; i++) {
        DBFField fi = drdr.getField(i);
        //System.out.println("fi name : " + fi.getName());
        DBFDataType fiType = fi.getType();
        //System.out.println("fi type code : " + fiType.getCharCode());
        //System.out.println("fi length : " + fi.getLength());
        //System.out.println("fi decimal count : " + fi.getDecimalCount());        
        switch (i) {
        case 0:
          assertEquals("Colonne 0 devrait etre de type C", 'C', fiType.getCharCode());
          assertEquals("Colonne 0 devrait ACTE_COD", "ACTE_COD", fi.getName());
          assertEquals("Colonne 0 devrait avoir la longueur 13", 13, fi.getLength());
          break;
        case 1:
          assertEquals("Colonne 1 devrait etre de type D", 'D', fiType.getCharCode());
          assertEquals("Colonne 1 devrait ACDT_MODIF", "ACDT_MODIF", fi.getName());
          assertEquals("Colonne 1 devrait avoir la longueur 8", 8, fi.getLength());
          break;
        case 2:
          assertEquals("Colonne 2 devrait etre de type C", 'C', fiType.getCharCode());
          assertEquals("Colonne 2 devrait FORFAI_COD", "FORFAI_COD", fi.getName());
          assertEquals("Colonne 2 devrait avoir la longueur 1", 1, fi.getLength());
          break;
        }
      }
      drdr.close();
    }//try
  }

}
