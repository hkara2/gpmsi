package fr.gpmsi.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.gpmsi.StringTransformable;
import fr.gpmsi.StringTransformers;

/**
 * Test de {@link StringTransformers}
 */
public class StringTransformersTest {

	/**
	 * Tester la transformation de nombres français en nombres anglais. 1,3 -&gt; 1.3
	 * @throws Exception _
	 */
	@Test
	public void testFrenchToUsNumber1() throws Exception {
		StringTransformable st = StringTransformers.frenchToUsNumber();
		String x = "1,3";
		String y = st.transform(x);
		assertEquals("1.3", y);
		//System.out.println(y);
	}

	/**
	 * Tester la transformation de nombres français en nombres anglais. 1,3E8 -&gt; 1.3E8
	 * @throws Exception _
	 */
	@Test
	public void testFrenchToUsNumber2() throws Exception {
		StringTransformable st = StringTransformers.frenchToUsNumber();
		String x = "1,3E8";
		String y = st.transform(x);
		System.out.println(y);
		assertEquals("1.3E8", y);
	}

	/**
	 * Tester la transformation de nombres français en nombres anglais. -2E-8 -&gt; -2E-8
	 * @throws Exception _
	 */
	@Test
	public void testFrenchToUsNumber3() throws Exception {
		StringTransformable st = StringTransformers.frenchToUsNumber();
		String x = "-2E-8";
		String y = st.transform(x);
		//S ystem.out.println(y);
		assertEquals("-2E-8", y);
	}

	/**
	 * Tester la transformation de dates au format français en dates au format iso
	 * @throws Exception _
	 */
	@Test
	public void testFrenchToIsoDate() throws Exception {
		StringTransformable st = StringTransformers.frenchToIsoDate();
		String x = "31/12/2016";
		String y = st.transform(x);
		//S ystem.out.println(y);
		assertEquals("2016-12-31", y);
	}
}
