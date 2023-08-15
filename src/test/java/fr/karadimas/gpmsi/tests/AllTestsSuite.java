package fr.karadimas.gpmsi.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
    ExcelFileCreationTest.class,
    H2Tests.class,
    PhonexTest.class,
    StringTable1TestJ.class,
    StringTableTests.class,
    StringTransformersTest.class,
})

public class AllTestsSuite {

}
