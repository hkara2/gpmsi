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

  /**
   * Constructeur avec le propriétaire et la rangée concernée
   * @param owner La StringTable concernée
   * @param rowNr Le numéro de la rangée concernée (commence à 0)
   */
  public StringTableRow(StringTable owner, int rowNr) {
    this.owner = owner;
    this.rowNr = rowNr;
  }

  /**
   * Retourner la valeur pour la colonne donnée
   * @param colNr Le numéro de la colonne
   * @return La valeur qui est dans cette colonne
   */
  public String getValue(int colNr) {
    return owner.getValue(rowNr, colNr);
  }
  
  /**
   * Retourner la valeur pour la colonne donnée.
   * Dans Groovy (via une métaclasse chargée au démarrage) il suffit d'utiliser ['nomColonne'] pour avoir la valeur.
   * @param colName Le nom de la colonne
   * @return La valeur qui est dans cette colonne
   * @throws ColumnNotFoundException Si la colonne n'a pas été trouvée
   */
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
