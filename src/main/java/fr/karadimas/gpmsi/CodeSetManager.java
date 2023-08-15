package fr.karadimas.gpmsi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Set;

/**
 * Gestionnaire d'ensembles de codes en base de données.
 * Crée une table "codeset" qui contient deux colonnes "name" et "code".
 * Réservé pour un usage futur.
 * @author hkaradimas
 *
 */
public class CodeSetManager {

	Connection cxn;
	PreparedStatement searchCodePs;
	PreparedStatement updateCodePs;
	
	public CodeSetManager() {
	}

	public void init(HashMap<String, Object> initMap)
			throws SQLException 
	{
		this.cxn = (Connection) initMap.get("connection");
		Statement st = this.cxn.createStatement();
		try {
			st.executeUpdate("create table codeset("
					+ "name varchar(128) primary key, "
					+ "code varchar(128)"
					+ ")");
			st.executeQuery("create index codeset_name_code on codeset(name, code)");
		}
		finally {
			st.close();
		}
	}
	
	public void addCodeSet(CodeSet cs)
			throws IOException, SQLException
	{
	  Set<String> codes = cs.getCodes();
	  for (String code : codes) addCode(cs.getName(), code);
	}
	
	private void addCode(String codeSetName, String code) throws SQLException
	{
		try {
			if (updateCodePs == null) {
				updateCodePs = cxn.prepareStatement("insert into codeset(name,code) values (?,?)");
			}
			updateCodePs.setString(1, codeSetName);
			updateCodePs.setString(2, code);
			updateCodePs.executeUpdate();
		}
		finally {
			
		}
	}
	public boolean containsCode(String codesetName, String code) throws SQLException
	{
		if (searchCodePs == null) {
			String sql = "select count(code) from codeset where name=? and code=?";
			searchCodePs = cxn.prepareStatement(sql);
		}
		searchCodePs.setString(1, codesetName);
		searchCodePs.setString(2, code);
		ResultSet rs = searchCodePs.executeQuery();
		try {
			while (rs.next()) {
				int n = rs.getInt(1);
				return n > 0;
			}			
			return false;
		}
		finally {
			rs.close();
		}
	}
	
}
