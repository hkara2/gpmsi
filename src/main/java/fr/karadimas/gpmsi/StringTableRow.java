package fr.karadimas.gpmsi;

/**
 * Rangée dans une {@link StringTable}
 * @author hkaradimas
 *
 */
public class StringTableRow {

  StringTable owner;
  int rowNr;
  
  public StringTableRow(StringTable owner, int rowNr) {
    this.owner = owner;
    this.rowNr = rowNr;
  }

  public String getValue(int colNr) {
    return owner.getValue(rowNr, colNr);
  }
  
  public String getValue(String colName) 
      throws ColumnNotFoundException 
  {
    return owner.getValue(rowNr, colName);
  }

  /**
   * Equivalent de {@link #getValue(int)} mais pour Groovy. 
   * Dans Groovy cette méthode correspond à l'opérateur '[]'
   * @param colNr numéro de colonne (commence à 0)
   * @return la valeur à la rangée/colonne donnée
   */
  public String getAt(int colNr) { return owner.getValue(rowNr, colNr); }
  
}
