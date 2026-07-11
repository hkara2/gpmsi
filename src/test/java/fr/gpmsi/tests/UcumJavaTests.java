package fr.gpmsi.tests;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.fhir.ucum.Decimal;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Ucum
 */
public class UcumJavaTests {
    UcumService ucumService;

    private UcumService getUcumEssenceService()
        throws FileNotFoundException, UcumException 
    {
        String fileName = "ucum-essence.xml";
        ClassLoader classLoader = getClass().getClassLoader();
        //URL fn = classLoader.getResource(fileName);
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        //File file = new File(fn.getFile());
        //InputStream inputStream = new FileInputStream(file);
        UcumService ucumService = new UcumEssenceService(inputStream);
        return ucumService;
    }
    
    /**
     * Initialisation pour tous les tests
     * @throws FileNotFoundException
     * @throws UcumException
     */
    @Before
    public void beforeAll()
        throws FileNotFoundException, UcumException 
    {
        ucumService = getUcumEssenceService();
    }
    
    /**
     * tester m
     * @throws UcumException _
     */
    @Test
    public void testConnect()
        throws UcumException 
    {
        String meter = ucumService.analyse("m");
        assertEquals("Analyse de m devrait etre (meter)", "(meter)", meter);
    }
    
    /**
     * Tester conversion cm vers metre
     * @throws Exception _
     */
    @Test
    public void testMetricConversion1()
        throws Exception 
    {
        Decimal r = ucumService.convert(Decimal.one(), "cm", "m");
        assertEquals("1 cm = 0.01 m", new Decimal("0.010"), r); //note echoue si on met seulement 0.01 , voir pourquoi
        //System.out.println("r:" + r);
    }

}
