package fr.karadimas.gpmsi;

/**
 * Encapsulation de CsvRow pour pouvoir utiliser les noms de colonne qui sont dans le ScriptStep de type csv.
 * On peut utiliser en groovy des expressions telles que (s'il y a une colonne qui s'appelle codepostal) :
 * <code>row.codepostal</code> et même changer la valeur
 * par exemple <code>row.codepostal = '91150'</code>.
 * @author hkaradimas
 */
public class CsvRow {
  ScriptStep owner;
  String[] row;
  
  public CsvRow(ScriptStep owner) {
    this.owner = owner;
  }

  public ScriptStep getOwner() { return this.owner; }
  
  /**
   * Retourner la valeur qui est à la colonne dont le numéro est colNrObj.
   * @param colNrObj le numéro de colonne (commence à 0 (zéro))
   * @return La valeur
   */
  public String getValue(Integer colNrObj) {
    int colNr = 0;
    if (colNrObj != null) colNr = colNrObj.intValue();
    return row[colNr];
  }
  
  /**
   * Meme chose que getValue, mais permet d'ecrire en groovy row[1] qui est traduit en row.getAt(1) 
   * @param colNr Le numéro de colonne (commence à 0)
   * @return La valeur
   */
  public String getAt(int colNr) {
    return row[colNr];
  }
  
  /**
   * Retourne la valeur qui est à la colonne qui porte le nom colName.
   * @param colName le nom de la colonne dont on veut la valeur
   * @return la valeur
   * @throws ColumnNotFoundException s'il n'existe pas de colonne pour ce nom
   */
  public String getValue(String colName) 
      throws ColumnNotFoundException 
  {
    int ix = owner.getCsvColumnIndex(colName);
    if (ix < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
    if (row.length <= ix) return ""; //pas de valeur dans la rangée, on retourne chaîne vide
    return row[ix];
  }

  public void setValue(int colNr, String value)
  {
    row[colNr] = value;
  }
  
  public void setValue(String colName, String value)
      throws ColumnNotFoundException
  {
    int ix = owner.getCsvColumnIndex(colName);
    if (ix < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
    row[ix] = value;
  }
  
  /**
   * Retourner toutes les valeurs de la rangée
   * @return Les valeurs
   */
  public String[] getRow() {
    return row;
  }

  /**
   * Identique a getRow(), mais plus clair
   * @return les valeurs
   */
  public String[] getValues() {
    return row;
  }

  
  public void setRow(String[] row) {
    this.row = row;
  }

  /**
   * Retourner true si cette rangee est vide. En raison de l'utilisation de la
   * classe CSVReader, on n'a pas accès à la ligne qui a été lue. On trouve donc
   * que la ligne est vide si toutes les valeurs de la rangée sont vides.
   * Ex (utilisation de Groovy) :
   * <pre>
   *   onItem {item-&gt;
   *       if (item.linenr == 1) return //ignorer l'en tete
   *       if (item.row.isEmpty()) return //ignorer lignes vides
   *       ...
   * </pre>
   * N.B. Lorsque Groovy est utilisé, <code>item.row.empty</code> est équivalent à <code>item.row.isEmpty()</code>
   * 
   * 
   * @return True si la rangée est vide
   */
  public boolean isEmpty() {
    for(String s:row) {
      if (s != null && s.length() > 0) return false;
    }
    return true;
  }
  
  /**
   * Pour toutes les cellules, remplacer la valeur par une nouvelle valeur dans laquelle
   * les éventuelles fin de lignes ont été remplacées par une séquence particulière.
   * {@link CsvUtils#replaceNewlines(String, String)} pour plus d'informations.
   * @param newSequence
   */
  public void replaceNewlines(String newSequence) {
    CsvUtils.replaceNewlines(row, newSequence);
  }
  
  public int getColumnCount() { return owner.getCsvColumnCount(); }
  
}
