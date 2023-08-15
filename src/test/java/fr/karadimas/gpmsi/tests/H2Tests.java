package fr.karadimas.gpmsi.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import org.h2.tools.Server;

/**
 * Teste la création d'un Serveur h2 en mémoire, la création de quelques tables.
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
	
	
	
}
