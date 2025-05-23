package fr.gpmsi;

import java.util.Iterator;

import groovy.lang.Closure;

/**
 * Objet de sélection qui désigne une sélection de rangées dans une {@link StringTable}.
 * Ne pas utiliser cette sélection si la compsition des lignes de la StringTable a changé depuis 
 * la création de la sélection.
 * @author hkaradimas
 *
 */
public class StringTableSelection
implements Iterable<StringTableRow> 
{

  Integer[] rowNrs = new Integer[0];
  StringTable owner;
  
  /**
   * Constructeur avec le propriétaire
   * @param owner La StringTable concernée par cette sélection
   */
  public StringTableSelection(StringTable owner) {
    this.owner = owner;
  }

  /**
   * Créer l'objet sélection
   * @param owner le propriétaire des rangées
   * @param rowNrs les numéros des rangées sélectionnées (les numéros commencent à 0)
   */
  public StringTableSelection(StringTable owner, Integer[] rowNrs) {
    //System.err.println("Constructing selector");
    this.owner = owner;
    this.rowNrs = rowNrs;
  }

  /**
   * Retourner la sélection
   * @return Un tableau avec chaque numéro de ligne sélectionné
   */
  public Integer[] getLineNrs() {
    return rowNrs;
  }

  /**
   * Définir la sélection
   * @param lineNrs Un tableau avec chaque numéro de ligne sélectionné
   */
  public void setLineNrs(Integer[] lineNrs) {
    this.rowNrs = lineNrs;
  }

  /**
   * Sélectionner dans cette sélection, les lignes concernées par la Closure.
   * @param selectionClosure la closure de sélection qui renvoie un Boolean
   * @return Un objet {@link StringTableSelection} qui contient ce qui a été sélectionné
   */
  public StringTableSelection selectRows(Closure<Boolean> selectionClosure) {
    return new StringTableSelection(owner, owner.findRows(rowNrs, selectionClosure));
  }

  /**
   * Itérateur pour parcourir cette sélection
   */
  @Override
  public Iterator<StringTableRow> iterator() {
    return new StringTableSelectionRowIterator(this);
  }
  

  /**
   * Retourner le nombre de rangées sélectionnées
   * @return Le nombre de rangées sélectionnées
   */
  public int getSelectedRowsCount() { return rowNrs.length; }
  
}

class StringTableSelectionRowIterator
implements Iterator<StringTableRow> 
{
  StringTableSelection sel;
  StringTableRow row;
  int selNr;
  
  StringTableSelectionRowIterator(StringTableSelection sel) {
    this.sel = sel;
    row = new StringTableRow(sel.owner, 0);
  }

  @Override
  public boolean hasNext() {
    return selNr < sel.rowNrs.length;
  }

  @Override
  public StringTableRow next() {
    row.rowNr = sel.rowNrs[selNr];
    selNr++;
    return row;
  }
  
}
