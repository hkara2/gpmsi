package fr.gpmsi.tests;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests de création d'un fichier Excel
 */
public class ExcelFileCreationTest {

  /** 
   * préparation
   * @throws Exception _
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * Démontage
   * @throws Exception _
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Tester génération d'un fichier vide
   * @throws IOException _
   */
  @Test
  public void testEmptyFileGeneration() 
      throws IOException 
  {
    Workbook wb = new XSSFWorkbook();
    FileOutputStream out = new FileOutputStream("test-files/tmp-out/generated0.xlsx");
    wb.createSheet();
    wb.write(out);
    out.close();
    wb.close();
  }

  /**
   * Tester génération d'un fichier simple
   * @throws IOException _
   */
  @Test
  public void testSimpleFileGeneration() 
      throws IOException 
  {
    Workbook wb = new XSSFWorkbook();
    FileOutputStream out = new FileOutputStream("test-files/tmp-out/generated1.xlsx");
    Sheet sh = wb.createSheet();
    wb.setSheetName(0, "Foo");
    Row rw = sh.createRow(0);
    Cell c = rw.createCell(0);
    c.setCellValue("Hello");
    c = rw.createCell(1);
    c.setCellValue("All\r\nYou\r\nHappy\r\nMicrosoft"); //remarquer les sauts de ligne dans la cellule
    c = rw.createCell(2);
    c.setCellValue("Fans !");
    wb.write(out);
    out.close();
    wb.close();
  }

}
