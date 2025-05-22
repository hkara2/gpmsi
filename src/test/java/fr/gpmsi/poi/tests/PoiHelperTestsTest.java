package fr.gpmsi.poi.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;

import fr.gpmsi.StringTable;
import fr.gpmsi.poi.PoiHelper;

/** test */
public class PoiHelperTestsTest {

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
    Workbook wb = WorkbookFactory.create(xl1_xlsxFile);
    PoiHelper poih = new PoiHelper();
    StringTable stbl = poih.sheetToStringTable(wb.getSheetAt(0), null, null);
    //dump(stbl);
    assertEquals(6, stbl.getColumnCount());
    assertEquals("d with space", stbl.getColumnName(3));
    assertEquals("1,29E+130", stbl.getValue(3, 3));
    wb.close();
  }
  
  @Test
  public void testSheetToStringTable2()
      throws EncryptedDocumentException, IOException 
  {
    File testFilesDir = new File("test-files");
    File inDir = new File(testFilesDir, "in");
    File xl1_xlsxFile = new File(inDir, "xl1.xlsx");
    Workbook wb = WorkbookFactory.create(xl1_xlsxFile);
    //now let's change a value
    wb.getSheetAt(0).getRow(3).getCell(4).setCellValue(0.0000012345);
    PoiHelper poih = new PoiHelper();
    DecimalFormat df = new DecimalFormat("#.#########E0");
    StringTable stbl = poih.sheetToStringTable(wb.getSheetAt(0), new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss"), df);
    //dump(stbl);
    assertEquals(6, stbl.getColumnCount());
    assertEquals("\u00e9", stbl.getColumnName(4)); //&eacute;
    assertEquals("1,2345E-6", stbl.getValue(3, 4));
    wb.close();
  }
  
}
