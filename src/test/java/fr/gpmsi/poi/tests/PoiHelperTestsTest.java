package fr.gpmsi.poi.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;

import fr.gpmsi.StringTable;
import fr.gpmsi.poi.PoiHelper;

/** test */
public class PoiHelperTestsTest {

  //@SuppressWarnings("unused")
  private void dump(StringTable stbl) {
    for (int i = 0; i<stbl.getColumnCount(); i++) System.out.print(stbl.getColumnName(i)+";");
    System.out.println();
    for (int j = 1; j < stbl.getRowCount(); j++) {
      for (int i = 0; i < stbl.getColumnCount(); i++) {
        System.out.print(stbl.getValue(j, i)+";");
      }
      System.out.println();
    }    
  }
  
  /** test de getColumnNumber */
  @Test
  public void testGetColumnNumber() {
    assertEquals("colonne A devrait etre 0", 0, PoiHelper.getColumnNumber("A")); //1-1
    assertEquals("colonne AA devrait etre 26", 26, PoiHelper.getColumnNumber("AA")); //27-1
    assertEquals("colonne AAA devrait etre 702", 702, PoiHelper.getColumnNumber("AAA")); //703-1
    assertEquals("colonne AG devrait etre 32", 32, PoiHelper.getColumnNumber("AG")); //33-1
    assertEquals("colonne XFC devrait etre 16382", 16382, PoiHelper.getColumnNumber("XFC")); //16383-1
  }

  @Test
  public void testSheetToStringTable()
      throws EncryptedDocumentException, IOException 
  {
    File testFilesDir = new File("test-files");
    File inDir = new File(testFilesDir, "in");
    File xl1_xlsxFile = new File(inDir, "xl1.xlsx");
    //faire un backup, car la manipulation change le fichier, bien que les seules opérations faites soient en lecture !
    File xl1_xlsxBackupFile = new File(inDir, "xl1_backup.xlsx");
    Files.copy(xl1_xlsxFile.toPath(), xl1_xlsxBackupFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
    //test proprement dit
    Workbook wb = WorkbookFactory.create(xl1_xlsxFile);
    PoiHelper poih = new PoiHelper();
    StringTable stbl = poih.sheetToStringTable(wb.getSheetAt(0), null, null);
    dump(stbl);
    assertEquals(6, stbl.getColumnCount());
    assertEquals("d with space", stbl.getColumnName(3));
    assertEquals("1,29E+130", stbl.getValue(2, 3)); //(2, 3) au lieu de (3, 3) depuis modification de sheetToStringTable
    wb.close(); //noter que le fichier va être altéré ici !
    //remettre l'ancien fichier
    Files.copy(xl1_xlsxBackupFile.toPath(), xl1_xlsxFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
    xl1_xlsxBackupFile.delete(); //effacer le backup
  }
  
  @Test
  public void testSheetToStringTable2()
      throws EncryptedDocumentException, IOException 
  {
    File testFilesDir = new File("test-files");
    File inDir = new File(testFilesDir, "in");
    File xl1_xlsxFile = new File(inDir, "xl1.xlsx");
    //faire un backup, car la manipulation change le fichier, bien que les seules opérations faites soient en lecture !
    File xl1_xlsxBackupFile = new File(inDir, "xl1_backup.xlsx");
    Files.copy(xl1_xlsxFile.toPath(), xl1_xlsxBackupFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
    //test proprement dit
    Workbook wb = WorkbookFactory.create(xl1_xlsxFile);
    //now let's change a value
    wb.getSheetAt(0).getRow(3).getCell(4).setCellValue(0.0000012345); //getRow(2) au lieu de getRow(3) depuis modification de sheetToStringTable
    PoiHelper poih = new PoiHelper();
    DecimalFormat df = new DecimalFormat("#.#########E0");
    StringTable stbl = poih.sheetToStringTable(wb.getSheetAt(0), new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss"), df);
    //dump(stbl);
    assertEquals(6, stbl.getColumnCount());
    assertEquals("\u00e9", stbl.getColumnName(4)); //&eacute;
    assertEquals("1,2345E-6", stbl.getValue(2, 4)); //getValue(2, 4) au lieu de getValue(3, 4) depuis modification de sheetToStringTable
    wb.close();
    //remettre l'ancien fichier
    Files.copy(xl1_xlsxBackupFile.toPath(), xl1_xlsxFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
    xl1_xlsxBackupFile.delete(); //effacer le backup
  }
  
}
