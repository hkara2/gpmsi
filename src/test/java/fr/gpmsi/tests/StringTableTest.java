package fr.gpmsi.tests;

import org.junit.Test;

import fr.gpmsi.StringTable;
import junit.framework.TestCase;

/**
 * Tests de {@link StringTable}
 */
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
	
	/**
	 * Test de recherche de rangée avec index. Recherche de la première rangée qui contient "999999",
	 * et imprime le temps de recherche. N'est pas vraiment un test mais montre l'accélération apportée par
	 * un index.
	 */
	@Test
	public void testFindRow1() {
		StringTable bigTable = makeBigTable();
		System.out.println("Looking for row (with index)");
		long tic = System.currentTimeMillis();
		int rowNr = bigTable.findRow(0, "999999");
		long tac = System.currentTimeMillis();
		System.out.println("row number : "+rowNr+" found in "+(tac-tic)+" ms");
	}

	/**
	 * Test de recherche de rangée mais sans index.
	 */
	@Test
	public void testFindRow2() {
		StringTable bigTable = makeBigTableWithoutIndex();
		System.out.println("Looking for row (without index)");
		long tic = System.currentTimeMillis();
		int rowNr = bigTable.findRow(0, "999999");
		long tac = System.currentTimeMillis();
		System.out.println("row number : "+rowNr+" found in "+(tac-tic)+" ms");
	}

	/**
	 * Test de recherche qui ne va pas aboutir, dans la table avec index.
	 */
	@Test
	public void testFindRow3() {
		StringTable bigTable = makeBigTableWithIndex1();
		System.out.println("Looking for inexistant row (with index)");
		long tic = System.currentTimeMillis();
		int rowNr = bigTable.findRow(1, "999999");
		long tac = System.currentTimeMillis();
		System.out.println("row number : "+rowNr+" found in "+(tac-tic)+" ms");
	}

	/**
	 * Test de recherche qui ne va pas aboutir, dans la table sans index.
	 */
	@Test
	public void testFindRow4() {
		StringTable bigTable = makeBigTableWithoutIndex();
		System.out.println("Looking for inexistant row (without index)");
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
