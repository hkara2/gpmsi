package fr.karadimas.gpmsi;

import java.time.LocalDateTime;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import fr.karadimas.gpmsi.poi.PoiHelper;
import fr.karadimas.gpmsi.poi.ValueWrapper;

/**
 * Encapsulation de {@link Row} pour pouvoir utiliser les noms de colonne qui sont dans le ScriptStep de type xlpoi.
 * On peut utiliser en groovy des expressions telles que (s'il y a une colonne qui s'appelle codepostal) :
 * <code>row.codepostal</code>.
 * Par contre ici, contrairement à {@link CsvRow}, on ne peut pas changer la valeur par cet accesseur.
 * Mais il y a accès à toute la rangée via {@link #getPoiRow()}, voire même à tout le classeur 
 * via {@link #getPoiSheet()} donc il est possible par ce biais de faire ce que l'on veut avec
 * la rangée, l'onglet (et même le classeur car à travers {@link Sheet} on a accès à {@link Workbook}).
 * <b>Attention</b> dans apache POI les numéros commencent tous à 0 (pour lignes et colonnes).
 * Ici les numéros de colonne commencent à 0, mais les numéros de ligne commencent à 1 (comme pour
 * les autres scriptstep, et aussi comme dans Excel.
 * @author hkaradimas
 */
public class XlRow {
  ScriptStep owner;
  
  /**
   * Constructeur.
   * @param owner Le ScriptStep propriétaire de cet objet rangée Xl POI.
   */
  public XlRow(ScriptStep owner) {
    this.owner = owner;
  }

  /**
   * Retourner le propriétaire
   * @return Le propriétaire
   */
  public ScriptStep getOwner() { return this.owner; }
  
  /**
   * Retourner la valeur qui est à la colonne dont le numéro est colNrObj.
   * @param colNrObj le numéro de colonne (commence à 0 (zéro))
   * @return La valeur, renvoie la chaîne vide "" si la ligne ou la cellule n'existe pas.
   */
  public String getStringValue(Integer colNrObj) {
    int colNr = 0;
    if (colNrObj != null) colNr = colNrObj.intValue();
    Row row = owner.sh.getRow(owner.linenr-1); //linenr commence à 1 et pour poi ça commence à 0
    if (row == null) return "";
    return owner.poiHelper.getCellValueAsString(row.getCell(colNr));
  }
  
  /**
   * Retourner directement la cellule qui est à la colonne dont le numéro est colNrObj.
   * Permet d'utiliser toutes la puissance (et la complexité) d'apache POI.
   * Bien étudier l'API de Apache POI (<a href="https://poi.apache.org/">https://poi.apache.org/</a>) avant de se lancer dans l'utilisation directe des cellules.
   * @param colNrObj le numéro de colonne (commence à 0 (zéro))
   * @return La cellule, ou null s'il n'y a pas de cellule à cet endroit.
   */
  public Cell getCell(Integer colNrObj) {
    int colNr = 0;
    if (colNrObj != null) colNr = colNrObj.intValue();
    Row row = owner.sh.getRow(owner.linenr-1); //linenr commence à 1 et pour poi ça commence à 0
    if (row == null) return null;
    return row.getCell(colNr);    
  }
  
  /**
   * Retourner directement la cellule qui est à la colonne qui a le nom donné.
   * @param colName Le nom de la colonne
   * @return La cellule, ou null s'il n'y a pas de cellule à cet endroit.
   * @throws ColumnNotFoundException Si la colonne n'a pas été trouvée
   */
  public Cell getCell(String colName) 
      throws ColumnNotFoundException 
  {
    int ix = owner.getCsvColumnIndex(colName);
    if (ix < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
    return getCell(ix);    
  }
  
  /**
   * Renvoyer la cellule sous forme d'objet.
   * Cf. {@link PoiHelper}
   * @param colNrObj Le numéro de colonne ou null (null est considéré ici comme 0).
   * @return l'objet qui est dans la colonne (peut retourner null)
   */
  public Object getCellObject(Integer colNrObj) {
    int colNr = 0;
    if (colNrObj != null) colNr = colNrObj.intValue();
    Row row = owner.sh.getRow(owner.linenr-1); //linenr commence à 1 et pour poi ça commence à 0
    if (row == null) return null;
    return owner.poiHelper.getCellValueAsObject(row.getCell(colNr));        
  }
  
  /**
   * Renvoyer la cellule sous forme d'objet.
   * @param colName Le nom de la colonne
   * @return l'objet qui est dans la colonne (peut retourner null)
   * @throws ColumnNotFoundException Si la colonne n'a pas été trouvée
   */
  public Object getCellObject(String colName) 
      throws ColumnNotFoundException 
  {
    int ix = owner.getCsvColumnIndex(colName);
    if (ix < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
    return getCellObject(ix);
  }
  
  /**
   * Récupère un ValueWrapper pour la cellule qui est au numéro donné
   * @param colNrObj Le numéro de colonne ou null (null est considéré ici comme 0).
   * @return le {@link ValueWrapper} qui correspond à la valeur dans la colonne (peut retourner null)
   */
  public ValueWrapper getValueWrapper(Integer colNrObj) {
    int colNr = 0;
    if (colNrObj != null) colNr = colNrObj.intValue();
    Row row = owner.sh.getRow(owner.linenr-1); //linenr commence à 1 et pour poi ça commence à 0
    if (row == null) return null;
    return new ValueWrapper(row.getCell(colNr));
  }
  
  /**
   * Récupère un ValueWrapper pour la cellule qui a le nom donné
   * @param colName Le nom de la colonne
   * @return le {@link ValueWrapper} qui correspond à la valeur dans la colonne (peut retourner null)
   * @throws ColumnNotFoundException Si la colonne n'a pas été trouvée
   */
  public Object getValueWrapper(String colName) 
      throws ColumnNotFoundException 
  {
    int ix = owner.getCsvColumnIndex(colName);
    if (ix < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
    return getValueWrapper(ix);
  }
  
  /**
   * Meme chose que getStringValue, mais permet d'ecrire en groovy row[1] qui est traduit en row.getAt(1) 
   * @param colNr Le numéro de colonne (commence à 0)
   * @return La valeur
   */
  public String getAt(int colNr) {
    return getStringValue(colNr);
  }
  
  /**
   * Retourne la valeur (en String) qui est à la colonne qui porte le nom colName.
   * @param colName le nom de la colonne dont on veut la valeur
   * @return la valeur
   * @throws ColumnNotFoundException s'il n'existe pas de colonne pour ce nom
   */
  public String getStringValue(String colName) 
      throws ColumnNotFoundException 
  {
    int ix = owner.getCsvColumnIndex(colName);
    if (ix < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
    return getStringValue(ix);
  }
    
  /**
   * Retourner toutes les valeurs de la rangée en tant que String
   * @return Les valeurs
   */
  public String[] getRowAsStrings() {
    int cc = owner.csvHeaderRow.length;
    String[] row = new String[cc];
    for (int i = 0; i < cc; i++) row[i] = getStringValue(i);
    return row;
  }

  /**
   * Identique a getRow(), mais plus clair
   * @return les valeurs
   */
  public String[] getStringValues() {
    return getRowAsStrings();
  }

  /**
   * Ramener l'objet {@link Row} poi directement.
   * @return l'objet {@link Row} ou null s'il n'y a pas d'objet à cette ligne.
   */
  public Row getPoiRow() {
    Row row = owner.sh.getRow(owner.linenr-1); //linenr commence à 1 et pour poi ça commence à 0
    if (row == null) return null; else return row;    
  }
  
  /**
   * Ramener l'objet apache poi {@link Sheet} directement.
   * @return l'objet apache poi {@link Sheet}
   */
  public Sheet getPoiSheet() {
    return owner.sh;
  }
  
  /**
   * La cellule est-elle vide ?
   * @param colNr Le numéro de colonne de la cellule
   * @return true si la cellule est vide, c'est à dire si getStringValue ramène une chaîne vide ou une chaîne qui ne contient que des espaces.
   */
  public boolean isEmpty(Integer colNr) {
    return getStringValue(colNr).trim().length() == 0;
  }
  
  /**
   * La cellule est-elle vide ?
   * @param colName Le nom de la colonne
   * @return true si la cellule est vide, c'est à dire si getStringValue ramène une chaîne vide ou une chaîne qui ne contient que des espaces.
   * @throws ColumnNotFoundException Si la colonne n'a pas été trouvée
   */
  public boolean isEmpty(String colName) 
      throws ColumnNotFoundException 
  {
    int ix = owner.getCsvColumnIndex(colName);
    if (ix < 0) throw new ColumnNotFoundException("Colonne '"+colName+"' non trouvee");
    return isEmpty(ix);
  }
  
  /**
   * Retourner true si toute la rangee est vide.
   * La rangée est vide si toutes les cellules de cette rangée qui ont des en-têtes sont vides.
   * Une cellule est vide si getStringValue renvoie une chaîne vide ou une chaîne qui ne
   * contient que des espaces.
   * @return True si la rangée est vide
   */
  public boolean isEmpty() {
    boolean empty = true;
    String[] vals = getStringValues();
    for (String val:vals) { if (val.trim().length() > 0) empty = false; }
    return empty;
  }
  
  /**
   * Méthode de confort pour retourner le DataFormatter sans avoir à en recréer un nouveau.
   * @return Le {@link DataFormatter}
   */
  public DataFormatter getDataFormatter() { return owner.poiHelper.getDataFormatter(); }
  
  /**
   * Nombre de colonnes
   * @return Le nombre de colonnes
   */
  public int getColumnCount() { return owner.getXlpoiColumnCount(); }
  
  /**
   * Contrôle si les cellules date sont retournées en {@link Date} ou en {@link LocalDateTime}.
   * @param b true si ce sont des {@link LocalDateTime} qui doivent être retournés, false si ce sont
   * des {@link Date} qui doivent être retournés.
   * Par défaut false.
   * Si on veut mettre la valeur à true, il suffit de le faire à la première ligne, ce réglage
   * reste actif pour les lignes suivantes.
   * Si on met cette valeur à chaque ligne, ce n'est pas grave, l'exécution est très rapide.
   */
  public void setNewJavaTimeUsed(boolean b) { owner.poiHelper.setNewJavaTimeUsed(b); }
  
}
