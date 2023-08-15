package fr.karadimas.gpmsi.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.karadimas.gpmsi.StringTransformable;
import fr.karadimas.gpmsi.StringTransformers;

public class StringTransformersTest {

	@Test
	public void testFrenchToUsNumber1() throws Exception {
		StringTransformable st = StringTransformers.frenchToUsNumber();
		String x = "1,3";
		String y = st.transform(x);
		assertEquals("1.3", y);
		//System.out.println(y);
	}

	@Test
	public void testFrenchToUsNumber2() throws Exception {
		StringTransformable st = StringTransformers.frenchToUsNumber();
		String x = "1,3E8";
		String y = st.transform(x);
		System.out.println(y);
		assertEquals("1.3E8", y);
	}

	@Test
	public void testFrenchToUsNumber3() throws Exception {
		StringTransformable st = StringTransformers.frenchToUsNumber();
		String x = "-2E-8";
		String y = st.transform(x);
		//S ystem.out.println(y);
		assertEquals("-2E-8", y);
	}

	@Test
	public void testFrenchToUsDate() throws Exception {
		StringTransformable st = StringTransformers.frenchToIsoDate();
		String x = "31/12/2016";
		String y = st.transform(x);
		//S ystem.out.println(y);
		assertEquals("2016-12-31", y);
	}
}
