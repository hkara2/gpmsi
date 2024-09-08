package fr.gpmsi;

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
	
	/**
	 * Constructeur par défaut
	 */
	public CodeSetManager() {
	}

	/**
	 * Initialiser ce gestionnaire
	 * @param initMap Les éléments à utiliser pour initialiser le gestionnaire
	 * @throws SQLException Si problème de base de données durant l'initialisation
	 */
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
	
	/**
	 * Ajouter un ensemble de codes
	 * @param cs L'ensemble à ajouter
	 * @throws IOException  si erreur entrées/sortie
	 * @throws SQLException -
	 */
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
	
	/**
	 * Est-ce que cet ensemble contient le code
	 * @param codesetName Nom de l'ensemble
	 * @param code Code à rechercher
	 * @return true si l'ensemble contient le code
	 * @throws SQLException Si erreur SQL lors de la recherche du code
	 */
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
