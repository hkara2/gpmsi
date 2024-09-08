package fr.gpmsi.tests;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.h2.tools.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.gpmsi.Groovy;
import junit.framework.TestCase;

/**
 * Teste la création d'un Serveur h2 en mémoire, la création de quelques tables, l'exécution d'un script "externe"
 * qui crée une base H2 locale.
 * @author hkaradimas
 *
 */
public class H2Tests 
extends TestCase 
{
    Server h2;
    
    @Before
    public void setUp() throws Exception {
      String[] args = {};
      h2 = Server.createTcpServer(args);
    }
    
    @After
    public void tearDown() {
      h2.stop();
    }
    
	public H2Tests() {
	}

	public H2Tests(String name) {
		super(name);
	}

	@Test
	public void testConnect1()
			throws Exception 
	{
		Connection cxn = DriverManager.getConnection("jdbc:h2:mem:");
		Statement st = cxn.createStatement();
		st.executeUpdate("CREATE TABLE FOO(NAME VARCHAR(32), AMOUNT INTEGER)");
		st.executeUpdate("INSERT INTO FOO(NAME, AMOUNT) VALUES ('TOTO', 15)");
		st.executeUpdate("INSERT INTO FOO(NAME, AMOUNT) VALUES ('TITI', 30)");
		PreparedStatement ps = cxn.prepareStatement("SELECT NAME, AMOUNT FROM FOO");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			System.out.println("NAME="+rs.getString(1)+",AMOUNT="+rs.getInt(2));
		}
	}
	
	/**
	 * Teste si le script testCsv_injecter_h2 fonctionne.
	 * Pour cela efface la précédente base de test si elle existait, pour en créer une nouvelle.
	 * @throws Exception Si erreur lors du test.
	 */
	@Test
	public void testCsv_injecter_h2()
	throws Exception
	{
	  String localH2Path = "test-files/tmp-out/csv_injecter_h2";
	  File localH2_traceFile = new File(localH2Path+".trace.db");
	  if (localH2_traceFile.exists()) localH2_traceFile.delete();
      File localH2_mvFile = new File(localH2Path+".mv.db");
      if (localH2_mvFile.exists()) localH2_mvFile.delete();
	  
	  Connection cxn = DriverManager.getConnection("jdbc:h2:./"+localH2Path, "sa", "");
      Statement st = cxn.createStatement();
      st.executeUpdate("CREATE TABLE TABLEFICTIVE (\r\n"
          + "  ID_TABLEFICTIVE IDENTITY PRIMARY KEY, -- clé primaire générée automatiquement lors de l'INSERT\r\n"
          + "  C_TEXTE VARCHAR(64), -- exemple de champ texte\r\n"
          + "  C_DATE DATE,  -- exemple de champ date\r\n"
          + "  C_TIMESTAMP DATETIME,  -- exemple de champ DATETIME pouvant contenir un Timestamp\r\n"
          + "  C_NB_ENTIER INTEGER,  -- exemple de champ contenant un nombre entier\r\n"
          + "  C_NB_DEC NUMERIC(12,3) -- exemple de nombre décimal\r\n"
          + ")");
      cxn.close();
      //maintenant appel du script sur la nouvelle base créée
      Groovy g = new Groovy();
      String[] args = {"-script","script-templates\\groovy\\csv_injecter_h2.groovy",
          "-a:jdbcurl", "jdbc:h2:./"+localH2Path,
          "-a:jdbcuser", "sa",
          "-a:jdbcpwd", "",
          "-a:input", "test-files/in/TABLEFICTIVE.csv" };
      g.init(args); 
      int result = g.run();
      assertEquals("csv_injecter_h2.groovy returned "+result, 0, result);
	}
	
	
	
}
