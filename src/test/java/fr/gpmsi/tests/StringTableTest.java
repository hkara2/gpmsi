package fr.gpmsi.tests;

import org.junit.Test;

import fr.gpmsi.StringTable;
import junit.framework.TestCase;

public class StringTableTest
extends TestCase 
{

	StringTable makeBigTable() {
		StringTable st = new StringTable();
		st.addIndex(0);
		for (int i = 0; i < 1000000; i++) {
			String[] row = {""+i, ""+i*2, ""+i*4};
			st.addRow(row);
		}
		return st;
	}
	
	StringTable makeBigTableWithoutIndex() {
		StringTable st = new StringTable();
		for (int i = 0; i < 1000000; i++) {
			String[] row = {""+i, ""+i*2, ""+i*4};
			st.addRow(row);
		}
		return st;
	}
	
	StringTable makeBigTableWithIndex1() {
		StringTable st = new StringTable();
		st.addIndex(1);
		for (int i = 0; i < 1000000; i++) {
			String[] row = {""+i, ""+i*2, ""+i*4};
			st.addRow(row);
		}
		return st;
	}
	
	@Test
	public void testFindRow1() {
		StringTable bigTable = makeBigTable();
		System.out.println("Looking for row");
		long tic = System.currentTimeMillis();
		int rowNr = bigTable.findRow(0, "999999");
		long tac = System.currentTimeMillis();
		System.out.println("row number : "+rowNr+" found in "+(tac-tic)+" ms");
	}

	@Test
	public void testFindRow2() {
		StringTable bigTable = makeBigTableWithoutIndex();
		System.out.println("Looking for row");
		long tic = System.currentTimeMillis();
		int rowNr = bigTable.findRow(0, "999999");
		long tac = System.currentTimeMillis();
		System.out.println("row number : "+rowNr+" found in "+(tac-tic)+" ms");
	}

	@Test
	public void testFindRow3() {
		StringTable bigTable = makeBigTableWithIndex1();
		System.out.println("Looking for row");
		long tic = System.currentTimeMillis();
		int rowNr = bigTable.findRow(1, "999999");
		long tac = System.currentTimeMillis();
		System.out.println("row number : "+rowNr+" found in "+(tac-tic)+" ms");
	}

	//@Test
	//public void testFindRows() {
		//fail("Not yet implemented");
	//}

}
