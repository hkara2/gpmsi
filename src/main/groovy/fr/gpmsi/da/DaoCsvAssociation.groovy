package fr.gpmsi.da;

import fr.gpmsi.CsvRow
import fr.gpmsi.StringTable
import fr.gpmsi.StringUtils

/**
 * Association de colonnes csv et d'enregistrements Dao, pour aider à stocker facilement depuis un fichier csv des
 * valeurs en base (est aussi utilisé pour des colonnes POI Excel)
 * @author hkaradimas
 *
 */
class DaoCsvAssociation {
  Map csvColsByDaoName = [:]
  Map daoColsByCsvName = [:]
  Map csvReadHandlersByCsvName = [:]
  Map csvWriteHandlersByCsvName = [:]
  
  /**
   * Déclarer une association csv - Dao.
   * Si un des deux noms de colonne est vide, sort immédiatement.
   * @param csvColumnName Le nom de la colonne csv. Ignoré si vide.
   * @param daoColumnName Le nom de la colonne dans le Dao. Ignoré si vide.
   */
  void csvDao(String csvColumnName, String daoColumnName) {
    if (StringUtils.isTrimEmpty(csvColumnName) || StringUtils.isTrimEmpty(daoColumnName)) return;
    if (csvColsByDaoName.containsKey(daoColumnName)) throw new Exception("nom de colonne Dao '"+daoColumnName+"' déjà déclaré")
    if (daoColsByCsvName.containsKey(csvColumnName)) throw new Exception("nom de colonne Csv '"+csvColumnName+"' déjà déclaré")
    csvColsByDaoName[daoColumnName] = csvColumnName
    daoColsByCsvName[csvColumnName] = daoColumnName
  }
  
  /**
   * Déclarer un gestionnaire de lecture csv, qui va retravailler la valeur qui est dans la colonne csv et retourner le bon objet.
   * @param csvColumnName Le nom de la colonne csv
   * @param readClosure La closure gestionnaire
   */
  void declareCsvReadHandler(String csvColumnName, Closure readClosure) {
    if (csvReadHandlersByCsvName.containsKey(csvColumnName)) {
       throw new Exception("Il y a déjà un gestionnaire de lecture déclaré pour la colonne csv '"+csvColumnName+"'")
    }
    csvReadHandlersByCsvName[csvColumnName] = readClosure
  }
  
  /**
   * Déclarer un gestionnaire d'écriture de csv, qui va retravailler la valeur csv avant que celle-ci ne soit écrite
   * @param csvColumnName Le nom de la colonne csv
   * @param writeClosure La closure gestionnaire
   */
  void declareCsvWriteHandler(String csvColumnName, Closure writeClosure) {
    if (csvWriteHandlersByCsvName.containsKey(csvColumnName)) {
       throw new Exception("Il y a déjà un gestionnaire d'écriture déclaré pour la colonne csv '"+csvColumnName+"'")
    }
    csvWriteHandlersByCsvName[csvColumnName] = writeClosure
  }

  /**
   * Pour chaque déclaration de colonne Dao, regarder s'il existe une association avec une colonne csv, et écrire
   * la valeur csv dans le tableau des valeurs, en utilisant le gestionnaire de lecture csv s'il existe.
   * 
   * @param row La rangée csv
   * @param values La liste des valeurs pour la rangée
   * @param dao L'objet da (data access)
   */
  void writeCsvToDao(CsvRow row, List values, Dao dao) {
    String[] csvColNames = row.getOwner().getCsvHeaderRow()
    LinkedHashMap daoColsByCsvName = this.daoColsByCsvName
    csvColNames.each { name ->
      if (daoColsByCsvName.containsKey(name)) {
        String daoColName = daoColsByCsvName[name]
        ColumnDef daoCol = dao.getColumn(daoColName)
        if (daoCol != null) {
          def csvReadClosure = csvReadHandlersByCsvName[name]
          if (csvReadClosure != null) {
            //c'est la closure qui va gérer la conversion String vers le bon objet, on lui fait confiance
            Object newVal = csvReadClosure(row.getValue(name))
            dao.setValue(values, daoColName, newVal)
          }
          else {
            String newVal = row.getValue(name)
            def newObjVal = daoCol.stringToValue(newVal, null)
            dao.setValue(values, daoColName, newObjVal)
          }
        }
      }
    }
  }//writeCsvToDao

  /**
   * Déclarer une association entre une colonne csv et une colonne utilisée dans un DAO.
   * @param csvColumnName Le nom de la colonne csv. Ignoré si vide.
   * @param daoColumnName Le nom de la colonne dao. Ignoré si vide. Si le nom commence par # , le # est enlevé
   */
  void addAssociation(String csvColumnName, String daoColumnName) {
    if (StringUtils.isTrimEmpty(csvColumnName) || StringUtils.isTrimEmpty(daoColumnName)) return;
    if (daoColumnName.startsWith('#')) daoColumnName = daoColumnName.substring(1)
    csvColsByDaoName.put(daoColumnName, csvColumnName)
    daoColsByCsvName.put(csvColumnName, daoColumnName)
  }
  
  /**
   * Déclarer des associations, à chaque fois entre une colonne csv et une colonne dao
   * @param associationTable Une StringTable qui contient les noms
   * @param csvNamesColumn Le nom de la colonne qui contient les noms de colonne csv
   * @param daoNamesColumn Le nom de la colonne qui contient les noms de colonne dao
   */
  void addCsvDaos(StringTable associationTable, String csvNamesColumn, String daoNamesColumn) {
    int rowCount = associationTable.getRowCount()
    for (int i = 0; i < rowCount; i++) {
      String csvColumnName = associationTable.getValue(i, csvNamesColumn)
      String daoColumnName = associationTable.getValue(i, daoNamesColumn)
      addAssociation(csvColumnName, daoColumnName)
    }
  }
  
  /**
   * Fabrique une Map de valeurs (en général pour du JDBC), à partir des valeurs csv passées, et des définitions de colonnes.
   * S'il y a un gestionnaire de lecture de csv (csvReadHandler) déclaré, il est appelé pour la conversion texte vers objet.
   * Sinon la conversion par défaut de la définition de colonne est utilisée.
   * @param tableDao L'objet DAO qui représente la table
   * @param row La rangée CsvRow
   * @param prefs Un objet de préférences pour le traitement des cas non conformes (peut être null)
   * @return Une Map qui contient les valeurs sous forme d'objet
   */
  Map<String, Object> makeValuesMap(Dao tableDao, CsvRow row, DaPreferences prefs) {
    HashMap<String, Object> vm = new HashMap<>();
    List<ColumnDef> colDefs = tableDao.getAllColumnDefs();
    colDefs.each {ColumnDef cd ->
      String daoColName = cd.name
      String csvColName = csvColsByDaoName[daoColName]
      if (csvColName == null) return
      String csvValue = row.getValue(csvColName)
      Object valueObject
      def readHandler = csvReadHandlersByCsvName.get(csvColName) 
      if (readHandler) valueObject = readHandler(csvValue) //utilisation du gestionnaire spécialisé
      else valueObject = cd.stringToValue(csvValue, prefs) //utilisation du gestionnaire inclus dans la définition de colonne
      vm.put(daoColName, valueObject)
    }
    return vm
  }
  
  /**
   * Retourner le nom de colonne dao à partir du nom de colonne csv
   * @param csvColumnName Le nom de colonne csv
   * @return le nom de colonne dao ou null si le nom csv n'est pas connu ou pas associé
   */
  String getDaoColumnName(String csvColumnName) {
    return daoColsByCsvName.get(csvColumnName)
  }
  
  /**
   * Retourner le nom de colonne csv à partir du nom de colonne dao 
   * @param daoColumnName Le nom de colonne dao
   * @return le nom de colonne csv ou null si le nom dao n'est pas connu ou pas associé
   */
  String getCsvColumnName(String daoColumnName) {
    return csvColsByDaoName.get(daoColumnName)
  }
}