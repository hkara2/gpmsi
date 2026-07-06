package fr.gpmsi.da;

import fr.gpmsi.CsvRow

/**
 * Association de colonnes csv et d'enregistrements Dao, pour aider à stocker facilement depuis un fichier csv des
 * valeurs en base
 * @author hkaradimas
 *
 */
class DaoCsvAssociation {
  def csvColsByDaoName = [:]
  def daoColsByCsvName = [:]
  def csvReadHandlersByCsvName = [:]
  def csvWriteHandlersByCsvName = [:]
  
  /**
   * Déclarer une association csv - Dao
   * @param csvColumnName Le nom de la colonne csv
   * @param daoColumnName Le nom de la colonne dans le Dao
   */
  void csvDao(String csvColumnName, String daoColumnName) {
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
  
}