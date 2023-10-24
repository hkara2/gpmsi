package fr.karadimas.gpmsi;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVReader;

import fr.karadimas.pmsixml.FieldParseException;
import fr.karadimas.pmsixml.FszField;
import fr.karadimas.pmsixml.FszGroup;
import fr.karadimas.pmsixml.FszGroupMeta;
import fr.karadimas.pmsixml.FszMeta;
import fr.karadimas.pmsixml.FszNode;
import fr.karadimas.pmsixml.InputString;
import fr.karadimas.pmsixml.ObjectUtils;
import groovy.lang.Closure;

/**
 * Une table simple, représentée par des lignes et colonnes de Strings.
 * Les numéros de lignes et de colonnes commencent à 0.
 * Il y a néanmoins des facilités d'utilisation, notamment les colonnes peuvent
 * être représentées par des noms, et l'utilisation des noms de colonnes peut
 * être sensible à la casse ou non.
 * Utilisable pour lier simplement une table a une autre dans un script, sans etre oblige
 * de recourir a R ou pandas.
 * @author hkaradimas
 */
public class StringTable
implements Iterable<StringTableRow>
{
	static Logger lg = LogManager.getLogger(StringTable.class);
	
	String name;
	ArrayList<String[]> rows = new ArrayList<>();
	
	/** 
	 * numéro de colonne pour un nom de colonne donné. Si les noms de colonne sont
	 * insensibles à la casse, ici il n'y aura que des minuscules.
	 */
	HashMap<String, Integer> colNumbersByName = new HashMap<>();
	HashMap<Integer, String> colNamesByNumber = new HashMap<>();
	
	HashMap<Integer, TreeSet<IndexEntry>> indexesByColNr = new HashMap<>();

	boolean truncatedInputAccepted = true;
	
	boolean namesCaseSensitive = false;
	
	char csvSeparatorChar = ';'; //séparateur csv : par défaut c'est ';'
	
	class IndexEntry
	implements Comparable<IndexEntry> 
	{
		String value;
		int rowNr;
		@Override
		public boolean equals(Object obj) {
			IndexEntry b = (IndexEntry) obj;
			if (rowNr != b.rowNr) return false;
			return ObjectUtils.safeEquals(value, b.value);
		}
		@Override
		public int compareTo(IndexEntry b) {
			int r = ObjectUtils.safeCompare(value, b.value);
			if (r != 0) return r;
			return rowNr - b.rowNr;
		}		
	}

	/**
	 * Constructeur simple avec juste le nom de la table.
	 * @param name Le nom de la table (devient le nom de la table de base de données
	 * lorsque l'on injecte la table via SQL)
	 */
	public StringTable(String name) { this.name = name; }
	
	/**
	 * Constructeur pour convenance, qui permet d'indiquer outre le nom le fichier csv qui
	 * devra être lu dans la table. Le séparateur est ';' et l'encodage est celui par
	 * défaut du système (soit pour la France "windows-1251" (si on n'a pas touché aux
	 * réglages de la JVM) qui est l'encodage utilisé aussi par Excel, ce qui tombe bien).
	 * @param name Le nom de la table (devient le nom de la table de base de données
	 * @param f Le fichier qui sera lu pour remplir la table (le fichier doit utiliser l'encodage par défaut du système)
	 * @throws IOException si erreur d'entrée/sortie
	 */
	public StringTable(String name, File f) throws IOException {
	  this(name); 
	  readFrom(f);
	}
	
	/**
	 * Ajouter un index sur la colonne colNr (commence à 0)
	 * @param colNr Numéro de la colonne (commence à 0)
	 */
	public void addIndex(int colNr) {
		indexesByColNr.put(colNr, new TreeSet<IndexEntry>());
		//build index if table is not empty
		int rowCount = rows.size();
		for (int i = 0; i < rowCount; i++) {
			String[] rowa = rows.get(i);
			String val = rowa[colNr];
			IndexEntry ie = new IndexEntry(); ie.rowNr = i; ie.value = val;
			indexesByColNr.get(colNr).add(ie);			
		}
	}
	
	/**
	 * Ajouter un index a la colonne en donnant son nom. On peut ajouter plusieurs index à une table.
	 * @param colName Le nom de la colonne
	 * @throws ColumnNotFoundException Si la colonne n'a pas été trouvée
	 */
	public void addIndex(String colName)
	    throws ColumnNotFoundException 
	{
		int ix = getColumnNumber(colName);
		if (ix < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee"); 
		addIndex(ix);
	}
	
	/**
	 * Analyser la ligne et la transformer en tableau de String
	 * @param line La ligne à analyser
	 * @param separator Le séparateur de colonne
	 * @return Le tableau de String
	 */
	public static String[] parseLine(String line, String separator) {
		ArrayList<String> fields = new ArrayList<>();
		StringTokenizer stok = new StringTokenizer(line, separator, true);
		String lastToken = "";
		while (stok.hasMoreTokens()) {
			String tok = stok.nextToken();
			if (tok.equals(separator)) {
				if (lastToken.equals(separator)) fields.add("");
			}
			else {
				fields.add(stok.nextToken());				
			}
			lastToken = tok;
		}
		String[] fielda = new String[fields.size()];
		fielda = fields.toArray(fielda);
		return fielda;
	}
	
	/** Constructeur par défaut */
	public StringTable() {
	}

	/**
	 * Contructeur pour construire et charger une StringTable directement, avec 
	 * l'encodage et l'index de colonne donnés.
	 * @param f Le fichier à lire
	 * @param encoding Encodage à utiliser
	 * @param colIndex Numéro de la colonne à indexer (commence à 0)
	 * @throws IOException Si erreur E/S
	 */
	public StringTable(File f, String encoding, int colIndex)
			throws IOException
	{
		addIndex(colIndex);
		readFrom(f, encoding);
	}
	
	/**
	 * Attribuer un numéro au nom de colonne.
	 * @param name Le nom de la colonne
	 * @param index Le numéro à attribuer (les numéros commencent à 0)
	 */
	public void setColumnNumber(String name, int index) {
	    if (namesCaseSensitive) colNumbersByName.put(name, index);
	    else colNumbersByName.put(name.toLowerCase(), index);
		colNamesByNumber.put(index, name);
	}
	
	/**
	 * Trouver le numéro de colonne.
	 * @param name Le nom de la colonne à trouver
	 * @return Le numéro de la colonne (commence à 0) ou -1 si non trouvé
	 */
	public int getColumnNumber(String name) {
		Integer ix;
		if (namesCaseSensitive) ix = colNumbersByName.get(name);
		else ix = colNumbersByName.get(name.toLowerCase());
		if (ix == null) return -1;
		else return ix;
	}
	
	/**
	 * Trouver le nom de la colonne
	 * @param index Le numéro de la colonne (commence à 0)
	 * @return Le nom de la colonne ou null si non trouvé
	 */
	public String getColumnName(int index) {
		return colNamesByNumber.get(index);
	}
	
	/**
	 * Retourne le nombre de colonnes
	 * @return Le nombre de colonnes
	 */
	public int getColumnCount() {
		Set<Integer> colNrs = colNamesByNumber.keySet();
		int maxNr = 0;
		for (Integer colNr : colNrs) {
			if (colNr != null && colNr > maxNr) maxNr = colNr;
		}
		if (maxNr < colNrs.size()) maxNr = colNrs.size();
		return maxNr;
	}
	
	/**
	 * Retourne les noms de colonne
	 * @return Un tableau avec les noms de colonne
	 */
	public String[] getColumnNames() {
		String[] colNames = new String[getColumnCount()];
		for(int i = 0; i < colNames.length; i++) {
			colNames[i] = getColumnName(i);
		}
		return colNames;
	}
	
	/**
	 * Donner l'index des colonnes à partir d'un tableau de titres
	 * @param titles La liste des noms de colonnes
	 */
	public void setColumnIndexesFromTitles(String[] titles) {
		for (int i = 0; i < titles.length; i++) {
			setColumnNumber(titles[i], i);
		}
	}
	
	/**
	 * Synonyme de {@link #setColumnIndexesFromTitles(String[])}
	 * @param titles La liste des noms de colonnes
	 */
	public void declareColumnNames(String[] titles) {
	  setColumnIndexesFromTitles(titles);
	}
	
	/**
	 * Synonyme de {@link #setColumnIndexesFromTitles(List)}
	 * @param titles La liste des noms de colonne
	 */
    public void declareColumnNames(List<String> titles) {
      setColumnIndexesFromTitles(titles);
    }
    
	
    /**
     * Donner l'index des colonnes à partir d'une liste de nom : pour chaque chaque nom de colonne,
     * attribue le numéro correspondant. par exemple pour la liste "NOM", "PRENOM", "TAILLE" , NOM 
     * aura le numéro 0, PRENOM le numéro 1, TAILLE le numéro 2. 
     * @param titles La liste des titres
     */
    public void setColumnIndexesFromTitles(List<String> titles) {
      if (titles == null) {
        lg.warn("parameter 'titles' is null in setColumnIndexesFromTitles");
        return;
      }
      setColumnIndexesFromTitles(titles.toArray(new String[titles.size()]));
    }
    
    /**
     * Retourner la rangée dont le numéro est r
     * @param r Le numéro de la rangée
     * @return Un tableau de String ou null si la rangée n'existe pas
     */
	public String[] getRow(int r) {
		if (r < 0 || r >= rows.size()) return null;
		return rows.get(r);
	}
	
	/**
	 * Identique à getRow(int) mais permet d'utiliser l'opérateur [] dans Groovy
     * @param r Le numéro de la rangée
     * @return Un tableau de String ou null si la rangée n'existe pas
	 */
	public String[] getAt(int r) {
	    return getRow(r);
	}
	
	/**
	 * Retourne le nombre de rangées de la table
	 * @return le nombre de rangées de la table
	 */
	public int getRowCount() { return rows.size(); }
	
	/**
	 * Retourner la valeur à la ligne et colonne donnée.
	 * Les numéros commencent à 0.
	 * @param rowNr Numéro de ligne (commence à 0)
	 * @param colNr Numéro de colonne (commence à 0)
	 * @return valeur à ligne/colonne
	 */
	public String getValue(int rowNr, int colNr) {
		String[] rowa = getRow(rowNr);
		if (rowa == null) return null;
		if (colNr < 0 || colNr >= rowa.length) return null;
		return rowa[colNr];
	}
	
    /**
     * Retourner la valeur à la ligne donnée et à la colonne nommée.
     * Les numéros commencent à 0.
     * Si le nom de colonne n'est pas trouvé, renvoie null.
     * @param rowNr Numéro de ligne (commence à 0)
     * @param colName Nom de la colonne
     * @return La valeur à ligne/colonne
     * @throws ColumnNotFoundException Si la colonne n'existe pas
     */
    public String getValue(int rowNr, String colName)
        throws ColumnNotFoundException 
    {
        int colNr = getColumnNumber(colName);
        if (colNr < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
        return getValue(rowNr, colNr);
    }
    
    /**
     * Mettre la valeur à jour. Met aussi l'index de la colonne à jour s'il y a un index pour cette colonne.
     * @param rowNr Numéro de rangée (commence à 0)
     * @param colNr Numéro de colonne (commence à 0)
     * @param val Valeur à mettre à jour (peut être null)
     */
	public void setValue(int rowNr, int colNr, String val) {
		String[] rowa = getRow(rowNr);
		if (rowa == null) return;
		if (colNr < 0 || colNr >= rowa.length) return;
		String oldval = rowa[colNr];
		TreeSet<IndexEntry> index = indexesByColNr.get(colNr);
		if (index != null) {
		  IndexEntry ie = new IndexEntry(); ie.rowNr = rowNr; ie.value = oldval;
		  index.remove(ie); //remove old entry
		  ie.value = val;
		  index.add(ie);
		}
		rowa[colNr] = val;
	}
	
	/**
	 * Met à jour la valeur (et son index s'il existe)
	 * @param rowNr Le numéro de la rangée
	 * @param colName Le nom de la colonne
	 * @param val La valeur à mettre à jour
	 * @throws ColumnNotFoundException Si la colonne n'existe pas
	 */
    public void setValue(int rowNr, String colName, String val)
        throws ColumnNotFoundException
    {
      int colNr = getColumnNumber(colName);
      if (colNr < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
      setValue(rowNr, colNr, val);
    }
        
    /**
     * Ajouter une rangée vide (un tableau de String qui ne contient que des null) à la table
     * @param lst Liste de String
     */
	public void addRow(List<String> lst) {
		if (lst == null) return;
		String[] rowa = new String[lst.size()];
		rowa = lst.toArray(rowa);
		addRow(rowa);
	}
	
	/**
	 * Ajouter la rangée au tableau
	 * @param rowa La rangée à ajouter
	 */
	public void addRow(String[] rowa) {
		int rowNr = rows.size();
		rows.add(rowa);
		//maintain indexes
		for (Integer colNr : indexesByColNr.keySet()) {
			String val = rowa[colNr];
			IndexEntry ie = new IndexEntry(); ie.rowNr = rowNr; ie.value = val;
			indexesByColNr.get(colNr).add(ie);
		}		
	}
	
	/**
	 * Retourner l'index de la première rangée qui contient l'entrée donnée pour la colonne donnée,
	 * ou -1 si non trouvé. Se sert de l'index s'il y en a un, pour accélérer la recherche.
	 * @param colNr Le numéro de la colonne où il faut chercher
	 * @param value La valeur à rechercher
	 * @return Le numéro de la première rangée où le résultat a été trouvé (commence à 0)
	 */
	public int findRow(int colNr, String value) {
		//look if an index exists
		TreeSet<IndexEntry> ie = indexesByColNr.get(colNr);
		if (ie != null) {
			IndexEntry e = new IndexEntry();
			e.value = value;
			IndexEntry m = ie.ceiling(e);
			if (m != null && m.value.equals(value)) return m.rowNr;
			else return -1;
		}
		for (int i = 0; i < rows.size(); i++) {
			String[] row = rows.get(i);
			String v = row[colNr];
			if (v.equals(value)) return i;
		}
		return -1;
	}
	
	/**
	 * Recherche la première ligne de la colonne nommée qui contient la valeur. 
	 * @param colName La colonne où il faut chercher
	 * @param value La valeur à rechercher
	 * @return le numéro de la ligne ou -1 si la valeur n'a pas été trouvée ou si la colonne n'existe pas
	 * @throws ColumnNotFoundException Si colonne non trouvée
	 */
	public int findRow(String colName, String value)
	    throws ColumnNotFoundException 
	{
	  int colNr = getColumnNumber(colName);
      if (colNr < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
      return findRow(colNr, value);
	}
	
	/**
	 * Regarder si la colonne contient la valeur donnée. Forme abrégée de <code>findRow(colNr, value) &gt;= 0</code>.
	 * @param colNr Numéro de colonne
	 * @param value Valeur
	 * @return true si la colonne contient la valeur
	 */
	public boolean contains(int colNr, String value) {
	  return findRow(colNr, value) >= 0;
	}
	
	/**
	 * Regarder si la colonne contient la valeur donnée.
	 * @param colName nom de la colonne
	 * @param value Valeur à tester
	 * @return true si la colonne contient la valeur
	 * @throws ColumnNotFoundException Si la colonne n'a pas été trouvée
	 */
	public boolean contains(String colName, String value) 
	    throws ColumnNotFoundException {
      return findRow(colName, value) >= 0;
    }
	
	/**
	 * Trouver les rangées pour lesquelles la colonne contient la valeur
	 * @param colNr Le numéro de la colonne à interroger
	 * @param value La valeur recherchée
	 * @return Un tableau d'entiers avec les numéros des lignes trouvées
	 */
	public Integer[] findRows(int colNr, String value) {
		//regarder si un index existe
		ArrayList<Integer> rowIndexes = new ArrayList<>();
		TreeSet<IndexEntry> ie = indexesByColNr.get(colNr);
		if (ie != null) {
			IndexEntry e = new IndexEntry();
			e.value = value;
			IndexEntry m = ie.ceiling(e);
			while (m != null && m.value.equals(value)) {
				if (m.value.equals(value)) rowIndexes.add(m.rowNr);				
				m = ie.higher(m);
			}
			return rowIndexes.toArray(new Integer[0]);
		}
		//Pas d'index, regarder toutes les rangées les unes après les autres
		for (int i = 0; i < rows.size(); i++) {
			String[] row = rows.get(i);
			String v = row[colNr];
			if (v.equals(value)) rowIndexes.add(i);
		}
		return rowIndexes.toArray(new Integer[0]);
	}
	
	/**
	 * Trouver les rangées pour lesquelles la colonne contient la valeur
	 * @param colName Le nom de la colonne à interroger
	 * @param value La valeur recherchée
	 * @return Un tableau d'entiers avec les numéros des lignes trouvées
	 * @throws ColumnNotFoundException Si la colonne n'a pas été trouvée
	 */
	public Integer[] findRows(String colName, String value)
	    throws ColumnNotFoundException 
	{
	  int colNr = getColumnNumber(colName);
      if (colNr < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
      return findRows(colNr, value);
	}
	
	/**
	 * Chercher les lignes dans la sélection pour lesquelles la colonne donnée
	 * contient la valeur donnée
	 * @param selection La sélection de lignes sur laquelle effectuer la recherche
	 * @param colNr Le numéro de la colonne à rechercher
	 * @param value La valeur que doit avoir la cellule pour que la ligne soit retenue
	 * @return un tableau d'indexs pour lesquels la condition est remplie, qui peut donc être vide. Ne renvoie jamais null.
	 */
	public Integer[] findRows(Integer[] selection, int colNr, String value) {
	  if (value == null) throw new NullPointerException("value ne peut pas etre null");
	  ArrayList<Integer> rowIndexes = new ArrayList<>();
	  for (int ix:selection) {
	    String v = getValue(ix, colNr);
	    if (v != null && value.equals(v)) rowIndexes.add(ix);
	  }
	  return rowIndexes.toArray(new Integer[0]);
	}
	
	/**
	 * Idem mais avec le nom de colonne au lieu du numéro
	 * @see #findRows(Integer[], int, String)
     * @param selection La sélection de lignes sur laquelle effectuer la recherche
     * @param colName Le nom de la colonne à rechercher
     * @param value La valeur que doit avoir la cellule pour que la ligne soit retenue
	 * @return un tableau d'indexs pour lesquels la condition est remplie, qui peut donc être vide. Ne renvoie jamais null.
	 * @throws ColumnNotFoundException Si colonne non trouvée
	 */
	public Integer[] findRows(Integer[] selection, String colName, String value)
	    throws ColumnNotFoundException
	{
	  int colNr = getColumnNumber(colName);
      if (colNr < 0) throw new ColumnNotFoundException("Colonne non trouvee '"+colName+"'");
      return findRows(selection, colNr, value);
	}
	
	/**
	 * Chercher les lignes pour lesquelles la Closure renvoie true
	 * @param selectionClosure Closure qui sera exécutée pour chaque rangée (un objet {@link StringTableRow})
	 * @return Le tableau des numéros des lignes trouvées 
	 */
	public Integer[] findRows(Closure<Boolean> selectionClosure) {
	  ArrayList<Integer> rowIndexes = new ArrayList<>();
	  StringTableRow row = new StringTableRow(this, 0);
	  int rowCount = rows.size();
	  for (int rowNr = 0; rowNr < rowCount; rowNr++) {
	    row.rowNr = rowNr;
	    Boolean select = selectionClosure.call(row);
	    if (select != null && select.booleanValue() == true) {
	      rowIndexes.add(rowNr);
	    }
	  }
	  return rowIndexes.toArray(new Integer[0]);
	}
	
	/**
	 * Chercher, dans le tableau des lignes données, celles qui répondent
	 * aux critères de sélection de la Closure.
	 * @param sel Lignes à traiter
	 * @param selectionClosure Closure à utiliser
	 * @return Les numéros de ligne correspondants
	 */
	public Integer[] findRows(Integer[] sel, Closure<Boolean> selectionClosure) {
      ArrayList<Integer> rowIndexes = new ArrayList<>();
      StringTableRow row = new StringTableRow(this, 0);
      int rowCount = sel.length;
      for (int selNr = 0; selNr < rowCount; selNr++) {
        row.rowNr = sel[selNr];
        Boolean select = selectionClosure.call(row);
        if (select != null && select.booleanValue() == true) {
          rowIndexes.add(sel[selNr]);
        }
      }
      return rowIndexes.toArray(new Integer[0]);
    }
	
	/**
	 * Créer une nouvelle sélection qui inclut toutes les rangées pour lesquelles l'exécution de
	 * la closure (fonction) de sélection renvoie true
	 * @param selectionClosure La closure à utiliser pour la sélection
	 * @return La sélection ( {@link StringTableSelection} ) trouvée
	 */
	public StringTableSelection selectRows(Closure<Boolean> selectionClosure) {
	  return new StringTableSelection(this, findRows(selectionClosure));
	}
	
	/**
	 * Ecrire la table dans la destination
	 * @param csvDest Objet {@link CsvDestination} dans lequel écrire
	 * @throws IOException Si erreur E/S
	 */
	public void writeTo(CsvDestination csvDest)
			throws IOException
	{
		for (String[] row : rows) {
			for (String cell : row) csvDest.f(cell);
			csvDest.endRow();
		}
	}
	
	/**
	 * Lire le contenu csv du fichier, en utilisant l'encodage par défaut de la plateforme,
	 * et ';' comme séparateur. La première ligne <i>doit</i> contenir les noms de colonne.
	 * @param f Le fichier à lire
	 * @throws IOException  Si erreur E/S
	 */
	public void readFrom(File f)
			throws IOException 
	{
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		readFrom(br, csvSeparatorChar);
		br.close();
		fr.close();
	}
	
	/**
	 * Lire le contenu csv du fichier, en utilisant le <i>charset</i> donné, et ';' en séparateur.
	 * La première ligne <i>doit</i> contenir les noms de colonne.
	 * @param f Le fichier à lire
	 * @param charset Le <i>charset</i> à utiliser, par exemple <code>"UTF-8"</code>
	 * @throws IOException  Si erreur E/S
	 */
	public void readFrom(File f, String charset)
			throws IOException 
	{
		readFrom(f, charset, csvSeparatorChar);
	}
	
	/**
     * Lire le contenu csv du fichier, en utilisant le <i>charset</i> donné, et le séparateur donné.
     * La première ligne <i>doit</i> contenir les noms de colonne.
	 * @param f Le fichier à lire
     * @param charset Le jeu de caractères (<i>charset</i>) à utiliser
     * @param separator Le séparateur à utiliser
	 * @throws IOException  Si erreur E/S
	 */
	public void readFrom(File f, String charset, char separator)
			throws IOException 
	{
		readFrom(f, null, charset, separator);
	}
	
    /**
     * Lire le contenu csv du fichier, en utilisant le <i>charset</i> donné, et le séparateur donné.
     * Ici la première ligne ne contient pas les noms de colonne, mais ces noms sont fournis en
     * paramètre.
     * @param f Le fichier à partir duquel lire
     * @param titles Les noms de colonne à utiliser
     * @param charset Le jeu de caractère (encodage) à utiliser
     * @param separator Le séparateur à utiliser
     * @throws IOException Si erreur E/S
     */
    public void readFrom(File f, String[] titles, String charset, char separator)
            throws IOException 
    {
        FileInputStream fis = new FileInputStream(f);
        BufferedInputStream bis = new BufferedInputStream(fis);
        InputStreamReader isr = new InputStreamReader(bis, charset);
        readFrom(isr, titles, separator);
        isr.close();
        bis.close();
        fis.close();
    }
    
    /**
     * Lire le contenu du Reader dans cette table. Il faut déclarer les index avant
     * d'appeler cette méthode, sinon les index seront reconstruits au moment de la
     * déclaration, ce qui est plus lent. Un
     * <a href="https://javacsv.sourceforge.net/com/csvreader/CsvReader.html">CsvReader</a>
     * est utilisé,
     * ainsi le séparateur de colonne, le <i>quoting</i> du texte et d'autre réglages
     * peuvent être contrôlés.
     * La première ligne du Reader doit contenir les noms de colonne.
     * 
     * @param rdr Le {@link Reader} à utiliser
     * @param separator le séparateur à utiliser
     * @throws IOException Si erreur E/S
     */
	public void readFrom(Reader rdr, char separator)
        throws IOException 
    {
	  readFrom(rdr, null, separator);
    }
	
	/**
     * Lire le contenu du Reader dans cette table. Il faut déclarer les index avant
     * d'appeler cette méthode, sinon les index seront reconstruits au moment de la
     * déclaration, ce qui est plus lent. Un
     * <a href="https://javacsv.sourceforge.net/com/csvreader/CsvReader.html">CsvReader</a>
     * est utilisé,
     * ainsi le séparateur de colonne, le <i>quoting</i> du texte et d'autre réglages
     * peuvent être contrôlés.
     * Le paramètre 'titles' est utilisé pour donner les noms des colonnes. Si ce
     * paramètre est null, la première ligne est utilisée pour donner
     * les noms des colonnes. 
     * 
	 * @param rdr Le {@link Reader} à utiliser
	 * @param titles Les noms de colonne à utiliser (si <code>null</code>, la première ligne du {@link Reader} sera utilisée à la place)
	 * @param separator Le séparateur à utiliser
	 * @throws IOException Si erreur E/S
	 */
	public void readFrom(Reader rdr, String[] titles, char separator)
			throws IOException 
	{
		CSVReader csvrdr = new CSVReader(rdr, separator);
		try {
			if (titles == null) titles = csvrdr.readNext();
			if (titles == null) {
				throw new IOException("Reader/File is empty");
			}
			setColumnIndexesFromTitles(titles);
			String[] row = csvrdr.readNext();		
			while (row != null) {
			    if (!isEmpty(row)) addRow(row);
				row = csvrdr.readNext();
			}//while
		}
		finally {
			csvrdr.close();			
		}
	}
	
	/**
	 * Lecture depuis un fichier a positions fixes.
	 * @param f Le fichier à lire
	 * @param gm La métadonnée du groupe
	 * @throws IOException Si erreur E/S
	 * @throws FieldParseException Si erreur d'analyse
	 */
    public void readFrom(File f, FszGroupMeta gm)
        throws IOException, FieldParseException
    {
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      try {
        readFrom(br, gm);
      }
      finally {
        br.close();
        fr.close();
      }
    }
    
    /**
     * Lecture depuis un fichier a positions fixes.
     * @param f Le fichier à partir
     * @param encoding L'encodage à utiliser
     * @param gm La métadonnée du groupe à lire
     * @throws IOException Si erreur E/S
     * @throws FieldParseException Si erreur d'analyse
     */
    public void readFrom(File f, String encoding, FszGroupMeta gm)
        throws IOException, FieldParseException
    {
      FileInputStream fis = new FileInputStream(f);
      try {
        BufferedInputStream bis = new BufferedInputStream(fis);
        InputStreamReader isr = new InputStreamReader(bis, encoding);
        try {
          readFrom(isr, gm);
        }
        finally {
          isr.close();
          bis.close();
        }
      }
      finally {
        fis.close();
      }
    }
    
    /**
     * Lecture depuis un fichier a positions fixes.
     * @param rdr Le {@link Reader} à partir duquel lire
     * @param gm La métadonnée du groupe à analyser
     * @throws IOException Si erreur d'E/S
     * @throws FieldParseException Si une erreur survient durant l'analyse du champ
     */
    public void readFrom(Reader rdr, FszGroupMeta gm)
        throws IOException, FieldParseException
    {
      BufferedReader br = new BufferedReader(rdr);
      try {
        readFrom(br, gm);
      }
      finally {
        br.close();
      }
    }
    
	/**
	 * Lecture depuis un fichier a positions fixes.
	 * @param br Le {@link BufferedReader} à partir duquel lire
	 * @param gm La métadonnée du groupe à analyser
	 * @throws IOException Si erreur E/S
	 * @throws FieldParseException Si erreur lors de l'analyse de la ligne
	 */
	public void readFrom(BufferedReader br, FszGroupMeta gm)
	    throws IOException, FieldParseException
	{
	  ArrayList<String> colNames = new ArrayList<>();
	  List<FszMeta> childMetas = gm.getChildMetas();
	  for (FszMeta childMeta : childMetas) {
        if (childMeta.isFieldMeta()) {
          colNames.add(childMeta.getStdName());
        }
        else {
          lg.error("ignoring "+childMeta.getStdName());
        }
      }//for
	  String[] titles = new String[colNames.size()];
	  titles = colNames.toArray(titles);
	  setColumnIndexesFromTitles(titles);
	  String line;
	  InputString in = new InputString();
	  in.acceptTruncated = truncatedInputAccepted;
	  while ((line = br.readLine()) != null) {
	    in.nextLine(line);
	    if (line.isEmpty()) continue;
	    FszNode nd = gm.makeNewNode();
	    nd.read(in);
	    FszGroup grp = (FszGroup) nd;
	    ArrayList<String> rowElts = new ArrayList<>();
	    for (int i = 0; i < titles.length; i++) {
	      FszField fld = grp.getChildField(titles[i]);
	      if (fld != null) {
	        rowElts.add(fld.getValue());
	      }
	      else {
	        rowElts.add("");
	        lg.error("Champ "+titles[i]+" non retrouve a la ligne "+in.lineNumber);
	      }
        }//for
	    addRow(rowElts);
	  }//while	  
	}
	
	/**
	 * Lire cette StringTable depuis un ResultSet JDBC.
	 * Les noms de colonne sont pris avec le label, ce qui permet d'utiliser une clause AS ... pour donner le nom que l'on veut à la colonne.
	 * Tous les résultats JDBC sont lus, donc faire attention car si il y a trop de résultats,
	 * cela peut planter tout le programme...
	 * @param rs Le ResultSet a utiliser. N'est pas refermé ici, normalement c'est automatique avec la fermeture du Statement qui a exécuté la requête.
	 * @param ofmt Le formateur d'objet ( {@link ObjectFormatter} ) à utiliser 
	 * @throws SQLException Si erreur sql lors de la lecture des résultats
	 */
	public void readFrom(ResultSet rs, ObjectFormatter ofmt)
	    throws SQLException
	{
	  ResultSetMetaData md = rs.getMetaData();
	  int cc = md.getColumnCount();
	  String[] titles = new String[cc];
	  int[] types = new int[cc]; 
	  for (int i = 1; i <= cc; i++) {
	    String colLabel = md.getColumnLabel(i);
	    titles[i-1] = colLabel;
	    types[i-1] = md.getColumnType(i);
	  }
	  setColumnIndexesFromTitles(titles);
	  while (rs.next()) {
	    String rowa[] = new String[cc];
	    for (int i = 1; i <= cc; i++) {
	      Object obj = rs.getObject(i);
	      rowa[i-1] = ofmt.formatSql(obj, types[i-1]);
	    }
	    addRow(rowa);
	  }//while
	}
	
	/**
	 * Lire les rangées depuis le ResultSet en utilisant le formateur objet par défaut.
	 * @param rs Le ResultSet
	 * @throws SQLException -
	 */
	public void readFrom(ResultSet rs) throws SQLException
	{
	  readFrom(rs, ObjectFormatter.defaultFormatter);
	}
	
	/**
	 * Regarde si le tableau est vide
	 * @param sa le tableau à analyser (null autorisé)
	 * @return true si sa est null ou si le tableau ne contient que des nulls ou des string vides (longueur 0 après trim())
	 */
	boolean isEmpty(String[] sa) {
	  if (sa == null || sa.length == 0) return true;
	  for (int i = 0; i < sa.length; i++) {
        String s = sa[i];
        if (s != null && s.trim().length() > 0) return false;
      }
	  return true;
	}

	/**
	 * Trouver une valeur dans une colonne donnée, et retourner une autre valeur de colonne, dans la même rangée.
     * Utile pour retrouver rapidement une valeur, comme on le ferait avec le <a href="https://support.microsoft.com/fr-fr/office/fonction-recherchev-0bbc8083-26fe-4963-8ab8-93a18ad188a1">RECHERCHEV</a> de Excel.
	 * @param nrOfColNrToSearch Numéro de la colonne dans laquelle rechercher
	 * @param valToSearch Valeur à rechercher
	 * @param nrOfColToRetrieve Numéro de la colonne où l'on prend la valeur à retourner
	 * @return La valeur retournée ou null si rien n'a été trouvé
	 */
    public String find(int nrOfColNrToSearch, String valToSearch, int nrOfColToRetrieve) {
      int rowIx = findRow(nrOfColNrToSearch, valToSearch);
      if (rowIx < 0) return null;
      String[] row = rows.get(rowIx);
      if (row.length <= nrOfColToRetrieve) return null;
      return row[nrOfColToRetrieve];    
    }
    
	/**
	 * Trouver une valeur dans une colonne donnée, et retourner une autre valeur de colonne, dans la même rangée.
	 * Utile pour retrouver rapidement une valeur, comme on le ferait avec le <a href="https://support.microsoft.com/fr-fr/office/fonction-recherchev-0bbc8083-26fe-4963-8ab8-93a18ad188a1">RECHERCHEV</a> de Excel.
	 * @param colToSearch Colonne à rechercher
	 * @param valToSearch Valeur à rechercher
	 * @param colToRetrieve Colonne où l'on prend la valeur à retourner
	 * @return la valeur qui est dans "colToRetrieve"
	 * @throws ColumnNotFoundException Si au moins une des colonnes n'existe pas
	 */
	public String find(String colToSearch, String valToSearch, String colToRetrieve)
	    throws ColumnNotFoundException 
	{
		int colToSearchIx = getColumnNumber(colToSearch);
		if (colToSearchIx < 0) throw new ColumnNotFoundException("Colonne '"+colToSearch+"' non trouvee");
		int colToRetrieveIx = getColumnNumber(colToRetrieve);
		if (colToRetrieveIx < 0) throw new ColumnNotFoundException("Colonne '"+colToRetrieve+"' non trouvee");
		return find(colToSearchIx, valToSearch, colToRetrieveIx);
	}

	/**
	 * Calculer la taille maximale de la colonne
	 * @param colNr Le numéro de la colonne
	 * @return la taille maximale de la colonne
	 */
	public int calcMaxSize(int colNr)
	{
		int maxSize = 0;
		int rowNr = 0;
		int rowCount = rows.size();
		while (rowNr < rowCount) {
			String val = getValue(rowNr, colNr);
			if (val != null) {
				if (val.length() > maxSize) maxSize = val.length();
			}
			rowNr++;
		}//while
		return maxSize;
	}
	
	/**
	 * Déterminer la taille maximum utilisée par la colonne.
	 * @param colName Le nom de la colonne 
	 * @return La taille maximum (sup ou égal à 0)
	 * @throws ColumnNotFoundException Si la colonne n'existe pas
	 */
	public int calcMaxSize(String colName)
	    throws ColumnNotFoundException
	{
	  int colNr = getColumnNumber(colName);
      if (colNr < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
      return calcMaxSize(colNr);
	}
	
	/**
	 * Compter le nombre de valeurs vides (null ou longueur = 0)
	 * @param colNr La colonne sur laquelle compter
	 * @return le nombre de valeurs vides
	 */
	public int countEmptyValues(int colNr)
	{
		int emptyValuesNr = 0;
		int rowNr = 0;
		int rowCount = rows.size();
		while (rowNr < rowCount) {
			String val = getValue(rowNr, colNr);
			if (val == null) { emptyValuesNr++; }
			else {
				if (val.length() == 0) emptyValuesNr++;
			}
			rowNr++;
		}//while		
		return emptyValuesNr;
	}
	
	/**
	 * Compter le nombre de valeurs vides (null ou dont la longueur est 0)
	 * @param colName Le nom de la colonne
	 * @return le nombre de valeurs vide
	 * @throws ColumnNotFoundException  Si la colonne n'existe pas
	 */
	public int countEmptyValues(String colName) 
	    throws ColumnNotFoundException
    {
      int colNr = getColumnNumber(colName);
      if (colNr < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
      return countEmptyValues(colNr);
    }
	
    /**
     * La colonne ne contient-elle que des nombres entiers ?
     * @param colNr Le numéro de la colonne
     * @return vrai si la colonne ne contient que des entiers ou est vide, faux
     *     si il y a des valeurs dans la colonne qui ne sont pas des entiers
     */
	public boolean containsOnlyIntegers(int colNr)
	{
		int rowNr = 0;
		int rowCount = rows.size();
		while (rowNr < rowCount) {
			String val = getValue(rowNr, colNr);
			if (val != null && val.length() > 0) {
				try {	Integer.parseInt(val); }
				catch (NumberFormatException nfex) { return false; }
			}
			rowNr++;
		}//while
		return true;
	}
	
	/**
	 * La colonne ne contient-elle que des nombres entiers ?
	 * @param colName Le nom de la colonne
	 * @return vrai si la colonne ne contient que des entiers ou est vide, faux
	 *     si il y a des valeurs dans la colonne qui ne sont pas des entiers ou bien
	 *     si la colonne n'existe pas
	 * @throws ColumnNotFoundException Si la colonne n'existe pas
	 */
	public boolean containsOnlyIntegers(String colName)
	    throws ColumnNotFoundException
    {
      int colNr = getColumnNumber(colName);
      if (colNr < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
      return containsOnlyIntegers(colNr);
    }
	
	/**
	 * Regarder si la colonne ne contient que des nombres au format donné
	 * @param colNr Le numéro de la colonne à regarder
	 * @param nf Le format de nombre
	 * @return true si la colonne ne contient que des nombres
	 */
	public boolean containsOnlyNumbers(int colNr, NumberFormat nf)
	{
		int rowNr = 0;
		int rowCount = rows.size();
		while (rowNr < rowCount) {
			String val = getValue(rowNr, colNr);
			if (val != null && val.length() > 0) {
				try {	nf.parse(val); }
				catch (ParseException e) { return false; }
			}
			rowNr++;
		}//while
		return true;
	}
	
    /**
     * Regarder si la colonne ne contient que des nombres au format donné
     * @param colName Le nom de la colonne à regarder
     * @param nf Le format de nombre
     * @return true si la colonne ne contient que des nombres
     * @throws ColumnNotFoundException Si la colonne n'a pas été trouvée
     */	
    public boolean containsOnlyNumbers(String colName, NumberFormat nf) 
        throws ColumnNotFoundException
    {
      int colNr = getColumnNumber(colName);
      if (colNr < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
      return containsOnlyNumbers(colNr, nf);
    }
    
    /**
     * Regarder si la colonne ne contient que des dates au format donné.
     * @param colNr Le numéro de la colonne à analyser
     * @param df Le format de date
     * @return true si la colonne ne contient que des dates
     */
	public boolean containsOnlyDates(int colNr, DateFormat df)
	{
		int rowNr = 0;
		int rowCount = rows.size();
		while (rowNr < rowCount) {
			String val = getValue(rowNr, colNr);
			if (val != null && val.length() > 0) {
				try {	df.parse(val); }
				catch (ParseException e) { return false; }
			}
			rowNr++;
		}//while
		return true;
	}
	
	/**
	 * Regarder si la colonne ne contient que des dates au format donné.
	 * @param colName Le nom de la colonne à analyser
	 * @param df Le format de date
	 * @return true si la colonne ne contient que des dates
	 * @throws ColumnNotFoundException Si la colonne n'existe pas
	 */
	public boolean containsOnlyDates(String colName, DateFormat df) 
	    throws ColumnNotFoundException
    {
      int colNr = getColumnNumber(colName);
      if (colNr < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
      return containsOnlyDates(colNr, df);
    }

	/**
	 * Transformer les dates pour qu'elles passent d'un format à l'autre.
	 * @param colNr Numéro de la colonne à ajuster (commence à 0)
	 * @param from Format de départ (sert à analyser le texte existant)
	 * @param to Format de destination (sert à produire le nouveau texte)
	 * @throws ParseException Si il y a une erreur de format de date
	 */
	public void transformDates(int colNr, DateFormat from, DateFormat to)
			throws ParseException
	{
		int rowNr = 0;
		int rowCount = rows.size();
		while (rowNr < rowCount) {
			String val = getValue(rowNr, colNr);
			if (val != null && val.length() > 0) {
				Date d = from.parse(val);
				val = to.format(d);
				setValue(rowNr, colNr, val);				
			}
			rowNr++;
		}//while		
	}
	
    /**
     * Transformer les dates pour qu'elles passent d'un format à l'autre.
     * @param colName Nom de la colonne à ajuster
     * @param from Format de départ (sert à analyser le texte existant)
     * @param to Format de destination (sert à produire le nouveau texte)
     * @throws ParseException Si il y a une erreur de format de date
     * @throws ColumnNotFoundException Si la colonne n'existe pas
     */
    public void transformDates(String colName, DateFormat from, DateFormat to)
        throws ParseException, ColumnNotFoundException
    {
      int colNr = getColumnNumber(colName);
      if (colNr < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non retrouvee");
      transformDates(colNr, from, to);
    }

	/**
	 * Transformer toute une colonne en utilisant un objet qui implémente {@link StringTransformable}.
	 * Voir {@link StringTransformers} pour les opérations toutes prêtes à être utilisées.
	 * @param colNr le numéro de la colonne qui doit être transformée (commence à 0)
	 * @param transformer Le transformateur (qui implémente {@link StringTransformable}) à utiliser 
	 * @throws ParseException Si quelque chose est incorrect pour le transformer.
	 */
	public void transform(int colNr, StringTransformable transformer)
			throws ParseException
	{
		int rowNr = 0;
		int rowCount = rows.size();
		while (rowNr < rowCount) {
			String val = getValue(rowNr, colNr);
			String transformedVal = transformer.transform(val);
			setValue(rowNr, colNr, transformedVal);				
			rowNr++;
		}//while		
	}
	
	/**
	 * Transformer la colonne en utilisant le transformateur donné
	 * @param colName Le nom de la colonne à transformer
	 * @param transformer L'objet transformateur
	 * @throws ParseException -
	 * @throws ColumnNotFoundException -
	 */
    public void transform(String colName, StringTransformable transformer)
        throws ParseException, ColumnNotFoundException
    {
      int colNr = getColumnNumber(colName);
      if (colNr < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
      transform(colNr, transformer);
    }

	/**
	 * Faire le CREATE TABLE en utilisant les noms de colonne en noms SQL directement.
	 * Toutes les colonnes sont déclarées en VARCHAR, avec pour taille maximum la taille maximum
	 * mesurée de la colonne.
	 * @param cxn La connexion JDBC
	 * @throws SQLException s'il y a un problème d'exécution sur la base.
	 */
	public void createTable(Connection cxn)
	throws SQLException
	{
	  StringBuffer sb = new StringBuffer();
      int cc = getColumnCount();
      int[] maxSizes = new int[cc];
      for (int i = 0; i < cc; i++) {
        maxSizes[i] = calcMaxSize(i);
      }
      sb.append("CREATE TABLE "); sb.append(getName()); sb.append('(');
      //envoyer les definitions
      for (int i = 0; i < cc; i++) {
        if (i > 0) sb.append(',');
        sb.append(getColumnName(i)); 
        sb.append(" VARCHAR("); sb.append(maxSizes[i]); sb.append(')');
      }
      sb.append(')');
	}
	
	/**
	 * Injecter les rangées. Tous les noms doivent être compatibles avec des identificateurs
	 * de SQL. (la base H2 a des règles complexes pour les guillemets des noms)
	 * @param cxn La connexion JDBC
	 * @throws SQLException si erreur lors de l'exécution du SQL.
	 */
	public void injectRows(Connection cxn) 
	throws SQLException
	{
		StringBuffer sb = new StringBuffer("INSERT INTO "+name+"(");
		String[] cols = getColumnNames();
		for (int i = 0; i < cols.length; i++) {
			if (i > 0) sb.append(',');
			String col = cols[i];
			sb.append(col); 
		}
		sb.append(") VALUES (");
		for (int i = 0; i < cols.length; i++) {
			if (i > 0) sb.append(',');
			sb.append("?");
		}
		sb.append(')');
		String sqlInsert = sb.toString();
		PreparedStatement ps = cxn.prepareStatement(sqlInsert);
		int j = 0;
		try {
			for (j = 0; j < rows.size(); j++) {
				for (int i = 0; i < cols.length; i++) {
					String val = getValue(j, i);
					lg.debug("for row "+j+", col "+i+", setting string '"+val+"'");
					if (val == null || val.trim().equals("")) ps.setNull(i+1, Types.VARCHAR);
					else ps.setString(i+1, val);
				}//for
				ps.executeUpdate();
			}//for
			cxn.commit();
		}
		catch (SQLException ex) {
		  //catch just to print current row ...
		  System.err.println("At row "+j);
		  throw ex;
		}
		finally {
			ps.close();
		}
	}

	/**
	 * Retourner le nom de la table
	 * @return Le nom de cette table de chaînes de caractères.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Définir le nom de la table
	 * @param name Le nom de la table
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Envoyer en formaté au {@link StringBuffer}, utilisé pour imprimer de facon agréable la table.
	 * @param sb Le {@link StringBuffer} à utiliser
	 */
	public void prettyPrintTo(StringBuffer sb) {
	  final String NL = System.getProperty("line.separator");
	  int cc = getColumnCount();
	  int[] maxSizes = new int[cc];
	  for (int i = 0; i < cc; i++) {
	    maxSizes[i] = calcMaxSize(i);
	  }
	  //envoyer les titres
	  for (int i = 0; i < cc; i++) {
	    if (i > 0) sb.append(' ');
	    sb.append(adjust(getColumnName(i), maxSizes[i]));
	  }
	  sb.append(NL);
	  //envoyer les lignes
	  for (String[] row : rows) {
	    //envoyer les colonnes
	    for (int i = 0; i < cc; i++) {
	      if (i > 0) sb.append('|');
	      sb.append(adjust(row[i], maxSizes[i]));
	    }
	    sb.append(NL);        
      }
	}
	
	/**
	 * Ajuster la string à la longueur donnée. Si elle est plus grande, elle est coupée.
	 * Si elle est plus petite, des espaces sont rajoutés.
	 * @param str La string à ajuster. Si null, est remplacée par une chaîne vide.
	 * @param len La longueur voulue
	 * @return La string ajustée à la bonne longueur.
	 */
	public String adjust(String str, int len) {
	  StringBuffer sb = new StringBuffer(str == null ? "" : str);
	  while (sb.length() < len) sb.append(' ');
	  return sb.substring(0, len);
	}

	/**
	 * Est-ce que l'on accepte des lignes tronquées lorsque l'on lit depuis
	 * des lignes à position de champs fixes ? Par défaut : true.
	 * @return true si on accepte des lignes tronquées
	 */
  public boolean isTruncatedInputAccepted() {
    return truncatedInputAccepted;
  }

  /**
   * Définir si l'on accepte des lignes tronquées lorsque l'on lit depuis
   * des lignes à position de champs fixes. Par défaut : true.
   * @param truncatedInputAccepted true si on accepte des lignes tronquées
   */
  public void setTruncatedInputAccepted(boolean truncatedInputAccepted) {
    this.truncatedInputAccepted = truncatedInputAccepted;
  }

  /**
   * Retourner le caractère séparateur de Csv
   * @return Le séparateur utilisé pour lire des fichiers .csv (par défaut : ';')
   */
  public char getCsvSeparatorChar() {
    return csvSeparatorChar;
  }

  /**
   * Définir le séparateur csv
   * @param csvSeparatorChar Le caractère séparateur csv
   */
  public void setCsvSeparatorChar(char csvSeparatorChar) {
    this.csvSeparatorChar = csvSeparatorChar;
  }

  /**
   * Les noms de colonne sont-ils sensibles à la casse ? Par défaut : false.
   * @return true si les noms sont sensibles à la casse
   */
  public boolean isNamesCaseSensitive() {
    return namesCaseSensitive;
  }

  /**
   * Définir si les noms sont sensibles à la casse.
   * @param namesCaseSensitive true si les noms sont sensibles à la casse (par défaut : false)
   */
  public void setNamesCaseSensitive(boolean namesCaseSensitive) {
    this.namesCaseSensitive = namesCaseSensitive;
  }

  /**
   * Renvoie un itérateur qui permet de parcourir les rangées grâce à un
   * objet StringTableRow. Ceci permet avec Groovy également d'utiliser .each 
   */
  @Override
  public Iterator<StringTableRow> iterator() {
    return new StringTableRowIterator(this);
  }

}

class StringTableRowIterator
implements Iterator<StringTableRow> {
  StringTableRow row;
  
  StringTableRowIterator(StringTable owner) {
    this.row = new StringTableRow(owner, 0);
  }
  
  @Override
  public boolean hasNext() {
    return row.rowNr + 1 < row.owner.getRowCount();
  }
  
  @Override
  public StringTableRow next() {
    row.rowNr++;
    return row;
  }
  
}

