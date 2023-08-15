package fr.karadimas.gpmsi.tests;

import java.io.File;

import org.junit.Test;

import fr.karadimas.gpmsi.StringTable;
import fr.karadimas.pmsixml.FszGroupMeta;
import fr.karadimas.pmsixml.MetaFileLoader;

public class StringTableTests {

  @Test
  public void testReadFromFsz()
      throws Exception
  {
    File in = new File(new File("test-files", "in"), "tra1.txt");
    fr.karadimas.pmsixml.MetaFileLoader mfl = new MetaFileLoader();
    FszGroupMeta gm = mfl.getOrLoadMeta("/tra2016.csv");
    FszGroupMeta traMeta = gm.findChildGroupMeta("TRA");
    StringTable stbl = new StringTable("TRA");
    stbl.readFrom(in, traMeta);
    StringBuffer sb = new StringBuffer();
    stbl.prettyPrintTo(sb);
    System.out.println(sb);
  }

}
