package fr.karadimas.gpmsi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linuxense.javadbf.DBFField;

/**
 * Encapsulation d'une rangée de fichier dbf pour pouvoir utiliser les noms de colonne qui sont dans le ScriptStep de type dbf.
 * On peut utiliser en groovy des expressions telles que (s'il y a une colonne qui s'appelle codepostal) :
 * <code>row.codepostal</code>.
 * @author hkaradimas
 */
public class DbfRow {
  static Logger lg = LoggerFactory.getLogger(DbfRow.class);
  
  ScriptStep owner;
  
  public DbfRow(ScriptStep owner) {
    this.owner = owner;
  }

  public ScriptStep getOwner() { return this.owner; }
  
  /**
   * Retourner la valeur qui est à la colonne dont le numéro est colNrObj.
   * @param colNrObj le numéro de colonne (commence à 0 (zéro))
   * @return La valeur ou null si le numero de colonne est invalide, ou s'il n'y a pas de rangée courante.
   */
  public Object getValue(Integer colNrObj) {
    int colNr = 0;
    if (colNrObj != null) colNr = colNrObj.intValue();
    Object[] row = owner.dbfRow;
    if (row == null) {
      lg.error("L'objet dbfRow est null, on retourne la valeur null");
      return null;
    }
    if (colNr >= row.length) {
      lg.error("Indice colNr "+colNr+" en dehors de la rangee (longueur "+row.length+"), on retourne null");
      return null;
    }
    return  row[colNr];
  }
  
  /**
   * Meme chose que getValue, mais permet d'ecrire en groovy row[1] qui est traduit en row.getAt(1) 
   * @param colNr Le numéro de colonne (commence à 0)
   * @return La valeur ou null si le numero de colonne est invalide, ou s'il n'y a pas de rangée courante.
   */
  public Object getAt(int colNr) {
    return getValue(colNr);
  }
  
  /**
   * Retourne la valeur qui est à la colonne qui porte le nom colName.
   * @param colName le nom de la colonne dont on veut la valeur
   * @return la valeur
   * @throws ColumnNotFoundException s'il n'existe pas de colonne pour ce nom
   */
  public Object getValue(String colName) 
      throws ColumnNotFoundException 
  {
    int ix = owner.getCsvColumnIndex(colName);
    if (ix < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
    return getValue(ix);
  }

  /**
   * Retourner toutes les valeurs de la rangée
   * @return Les valeurs ou null s'il n'y a pas de rangee courante.
   */
  public Object[] getRow() {
    return owner.dbfRow;
  }

  /**
   * Identique a getRow(), mais plus clair
   * @return les valeurs
   */
  public Object[] getValues() {
    return owner.dbfRow;
  }
 
  public int getColumnCount() { return owner.getCsvColumnCount(); }

  /**
   * Retourner la définition de champ pour la colonne donnée
   * @param colNr Le numéro de la colonne
   * @return Un objet {@link DBFField}
   */
  public DBFField getDBFField(int colNr) {
    return owner.dbfrdr.getField(colNr);
  }
  
  /**
   * Retourner la définition de champ pour la colonne donnée
   * @param colName Le nom de la colonne
   * @return Un objet {@link DBFField}
   * @throws ColumnNotFoundException Si la colonne ("champ" dbf) n'a pas été trouvée
   */
  public DBFField getDBFField(String colName)
      throws ColumnNotFoundException 
  {
    int ix = owner.getCsvColumnIndex(colName);
    if (ix < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
    return owner.dbfrdr.getField(ix);
  }
  
}
