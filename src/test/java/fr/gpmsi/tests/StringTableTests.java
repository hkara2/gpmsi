package fr.gpmsi.tests;

import java.io.File;

import org.junit.Test;

import fr.gpmsi.StringTable;
import fr.gpmsi.pmsixml.FszGroupMeta;
import fr.gpmsi.pmsixml.MetaFileLoader;

/**
 * Test de remplissage de {@link StringTable}
 */
public class StringTableTests {

  /**
   * Test de lecture depuis un fichier TRA
   * @throws Exception
   */
  @Test
  public void testReadFromFsz()
      throws Exception
  {
    File in = new File(new File("test-files", "in"), "tra1.txt");
    fr.gpmsi.pmsixml.MetaFileLoader mfl = new MetaFileLoader();
    FszGroupMeta gm = mfl.getOrLoadMeta("/fr/gpmsi/pmsixml/tra2016.csv");
    FszGroupMeta traMeta = gm.findChildGroupMeta("TRA");
    StringTable stbl = new StringTable("TRA");
    stbl.readFrom(in, traMeta);
    StringBuffer sb = new StringBuffer();
    stbl.prettyPrintTo(sb);
    System.out.println(sb);
  }

}
