package fr.karadimas.gpmsi;

import java.util.Iterator;

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
  
  /**
   * Iterateur pour parcourir toutes les colonnes
   * @return Un iterateur sur toutes les colonnes
   */
  public Iterator<String> iterator() {
    return new Iterator<String>() {
      int i = 0;

      @Override
      public boolean hasNext() {
        return owner.getColumnCount() > i;
      }

      @Override
      public String next() {
        return owner.getValue(rowNr, i++);
      }
    };
  }
  
}
