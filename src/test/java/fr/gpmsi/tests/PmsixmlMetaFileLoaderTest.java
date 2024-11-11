package fr.gpmsi.tests;

import java.io.IOException;

import org.junit.Test;

import fr.gpmsi.pmsixml.MetaFileLoader;

/**
 * Tests de MetaFileLoader
 */
public class PmsixmlMetaFileLoaderTest {

    /**
     * Tester la recherche des fichiers 'resource'
     * @throws IOException Si erreur E/S
     */
    @Test
    public void testListResourceFiles()
            throws IOException
    {
        String[] list = MetaFileLoader.listResourceFiles();
        for (String entry : list) {
            System.out.println("Entry:"+entry);
        }
    }
}
