package fr.gpmsi.da

import groovy.sql.Sql

import java.sql.Connection
import java.sql.ResultSet
import java.sql.Types
import java.text.NumberFormat
import java.text.SimpleDateFormat

import org.apache.log4j.LogManager

import com.graphbuilder.math.func.LgFunction

import fr.gpmsi.StringUtils
import fr.gpmsi.pmsixml.FszField
import fr.gpmsi.pmsixml.FszNode

/**
 * Classe pour un DAO (Data Access object) qui sert d'intermédiaire entre une base de données
 * et des classes Groovy.
 * A noter que Groovy facilite beaucoup l'accès aux bases de données, et un système à base de Dao est
 * moins utile que pour, par exemple, java.
 * L'utilisation d'objets Dao évite le recours à Hibernate (<a href="https://hibernate.org/">https://hibernate.org/</a>),
 * qui est assez lourd en manipulation. Mais Hibernate est plus adapté à partir de quelques dizaines de tables.
 * Ici il y a pas mal de restrictions, notamment les noms de table ne doivent pas contenir d'espace.
 */
class Dao {
    
    static org.apache.log4j.Logger lg = LogManager.getLogger(Dao.class)
    
    /**
     * Contrôle si on émet des println avec des informations de débogage. Par défaut à false.
     */
    static boolean emitDebugPrints = false
    
    /**
     * Contrôle si on émet des println avec des informations sur les colonnes et les valeurs insérées. Par défaut à false.
     */
    static boolean emitInsertDebugPrints = false
    
    /**
     * Teste si la table existe
     * @param gsql Une connexion Groovy sql
     * @param name Le nom de la table. Si c'est pour une base de données H2, il faut généralement donner le nom de la table en majuscules,
     *    sauf si les réglages par défaut ont été modifiés, ou la table déclarée avec le nom entre guillemets.
     * @return true si la table existe
     */
    static boolean isTableExistent(Sql gsql, String name) {
      boolean tableExists = false;
      
      Connection conn = gsql.getConnection()
      ResultSet rset = conn.getMetaData().getTables(null, null, name, null) //String catalog, String schemaPattern, String tableNamePattern, String[] types
      if (rset.next()) tableExists = true
      return tableExists
    }
    
    def columnDefsByName = [:]
    def columnDefIndexesByName = [:]
    List<ColumnDef> columnDefs = [] //toutes les définitions de colonne, y compris les clés primaires
    List<ColumnDef> pkDefs = [] //les définitions de clé primaire. Si vide -> pas de clé primaire. Si plus de 1 -> clé primaire composite
    def tableName = "?"
    String dialect = "H2"
    
    /**
     * Constructeur avec nom de la table
     * @param tableName Le nom de la table que ce DAO reflète
     */
    Dao(String tableName) { this.tableName = tableName }
    
    /** Retourner le nom de la table */
    def getTableName() { tableName } 
    
    /** Déclarer une colonne à partir de sa définition */
    ColumnDef col(ColumnDef cd) { 
        columnDefsByName.put(cd.getName(), cd)
        cd.owner = this
        cd.index = columnDefs.size()
        if (emitDebugPrints) println("for col '${cd.getName()}' putting index ${cd.index}")
        columnDefIndexesByName[cd.getName()] = cd.index
        columnDefs.add(cd)
        return cd //retourner cd pour pouvoir faire d'autres changements
    }
    
    /** déclarer une colonne comme partie de la clé primaire */
    ColumnDef pkcol(ColumnDef cd) { col(cd); pkDefs << cd; cd.setPrimaryKey(true); return cd }
    
    /** Déclarer une colonne de type char */
    ColumnDef colChar(String name, int maxLen) {
      ColumnDef cd = new CChar(name, maxLen)
      col(cd)
      return cd
    }
    
    /** Déclarer une colonne de type date */
    ColumnDef colDate(String name) {
      ColumnDef cd = new CDate(name)
      col(cd)
      return cd
    }
    
    /** Déclarer une colonne de type integer (32 bits signés dans h2) */
    ColumnDef colInteger(String name) {
      ColumnDef cd = new CInteger(name)
      col(cd)
      return cd
    }
    
    /** Déclarer une colonne de type numeric (correspond à BigDecimal dans h2) */
    ColumnDef colNumeric(String name, int precision, int scale) {
      ColumnDef cd = new CNumeric(name, precision, scale)
      col(cd)
      return cd
    }
    
    /** Déclarer une colonne de type timestamp */
    ColumnDef colTimestamp(String name) {
      ColumnDef cd = new CTimestamp(name)
      col(cd)
      return cd
    }
    
    /** Déclarer une colonne de type varchar */
    ColumnDef colVarchar(String name, int maxLength) {
      ColumnDef cd = new CVarchar(name, maxLength)
      col(cd)
      return cd
    }
    
    /**
     * Retourner la liste de toutes les définitions de colonne (clés primaires incluses)
     * @return la liste
     */
    def getAllColumnDefs() {
      return columnDefs
    }
    
    /**
     * Retourner la liste de toutes les définitions de colonne de clé primaire
     * @return la liste
     */
    def getAllPkColumnDefs() {
      return pkDefs
    }
    
    /**
     * Retourner la définition de colonne pour le nom de colonne donné
     * @param name Le nom de colonne
     * @return la définition de colonne ou null si pas trouvé
     */
    def getColumnDef(String name) {
      return columnDefsByName[name]
    }
    
    /**
     * Retourner tous les noms de colonnes
     * @return Une liste des noms de colonne
     */
    def getAllColNames() { columnDefs.collect { e -> e.name } }
    
    /**
     * Retourner les noms de colonne qui font partie de la clé primaire
     * @return Une liste des noms de colonne
     */
    def getAllPkColNames() { pkDefs.collect { e -> e.name } }
    
    /**
     * Créer une liste de valeurs null, de la bonne taille (le nombre de colonnes)
     * @return La liste
     */
    def makeEmptyValueList() { return [null] * columnDefs.size() }
    
    /**
     * Pour une liste de valeurs (rangées dans l'ordre des colonnes), retourner
     * la valeur qui correspond au nom de colonne
     * @param values Les valeurs
     * @param name Le nom de la colonne
     * @return La valeur à l'endroit de la colonne (qui peut être null), ou null si la colonne n'existe pas
     * @throws NullNotAllowedException Si la valeur null n'est pas acceptée dans la base
     */
    def getValue(List values, String name)
      throws NullNotAllowedException 
    {
        Integer index = columnDefIndexesByName[name]
        if (index == null) return null
        return values.get(index)
    }

    /**
     * Pour une liste de valeurs (rangées dans l'ordre des colonnes), retourner
     * la valeur qui correspond à la définition de la colonne
     * @param values Les valeurs
     * @param cdef La définition de colonne
     * @return La valeur à l'endroit de la colonne (qui peut être null), ou null si la colonne n'existe pas
     * @throws NullNotAllowedException Si la valeur null n'est pas acceptée dans la base
     */
    def getValue(List values, ColumnDef cdef)
      throws NullNotAllowedException 
    {
        return values.get(cdef.index)
    }

    /**
     * Dans la liste de valeurs (rangées dans l'ordre des colonnes),
     * définir l'élément qui correspond au nom de colonne. 
     * @param values La liste de valeurs
     * @param name Le nom de la colonne
     * @param value La valeur à mettre à l'endroit de la colonne
     * @return cet objet DAO
     */
    def setValue(List values, String name, Object value) {
        int index = columnDefIndexesByName[name]
        values.set(index, value)
        this
    }
    
    /**
     * Dans la liste de valeurs (rangées dans l'ordre des colonnes),
     * définir l'élément qui correspond à la définition de colonne.
     * @param values La liste de valeurs
     * @param cdef  La définition de colonne
     * @param value La valeur à mettre à l'endroit de la colonne
     * @return cet objet DAO
     */
    def setValue(List values, ColumnDef cdef, Object value) {
      values.set(cdef.index, value)
      this
    }
    
    /**
     * Gérer la valeur à insérer ou mettre à jour, pour convertir en un autre type si besoin, pour l'instant, gestion d'une chaîne vide
     * pour transformer en zéro ou en null le cas échéant
     * @param val la valeur à insérer
     * @param vals La liste des valeurs qui sera utilisée dans l'INSERT ou l'UPDATE
     * @param cd La définition de colonne qui correspond à la valeur
     */
    protected void handleColumnValue(Object val, List vals, ColumnDef cd) {
      //essayer de détecter quelques cas de prise en charge de null qui peuvent conduire à une erreur SQL mais qu'on peut prévenir
      if (val == null) {
          if (!cd.isNullable()) {
            if (cd instanceof IZeroForNullAllowed) {
              IZeroForNullAllowed sfna = (IZeroForNullAllowed) cd
              vals << sfna.getObjectForZero()
            }
            else {
              throw new NullNotAllowedException("Null pas autorisé pour la colonne ${tableName}.${cd.name}")
            }
          }
          else {
            vals << null
          }
      }
      else if (val instanceof String) {
        //a voir si plus tard on essaie de convertir une String en nombre si la colonne est numérique ;  pour l'instant on laisse faire le driver jdbc
        String strVal = (String) val
        if (strVal.trim() == '') {
          if (cd instanceof CInteger || cd instanceof CNumeric) {
            if (cd.isNullable()) {
              vals << null
            }
            else {
              IZeroForNullAllowed zfna = (IZeroForNullAllowed)cd
              if (zfna.isZeroForNullAllowed()) {
                  vals << zfna.getObjectForZero()
              }
              else {
                  throw new NullNotAllowedException("Null pas autorisé pour la colonne ${tableName}.${cd.name}")
              }
            }
          }
          else {
            //ce ne sont pas des nombres, insérer la valeur en espérant que le pilote va gérer une chaîne vide
            vals << val
          }//if (cd instanceof CInteger || cd instanceof CNumeric)
        }// if (strVal.trim() == '')
        else { //TODO peut-être traiter aussi les cas de CDate, CTimestamp pour éventuellement convertir intelligemment ?
          //ce n'est pas une chaîne vide
          vals << val
        }// if (strVal.trim() == '')
      }
      else {
          //pas une String, enregistrer tel quel
          vals << val
      }//else if (val instanceof String)
    }
    
    /**
     * Insérer les valeurs dans la base de données
     * Gére également les valeurs autogénérées, en ne les insérant pas lorsqu'elles sont null (de ce fait c'est h2 qui va les générer).
     * Dans le tableau de valeurs qui est passé, met les clés qui ont été autogénérées s'il y a lieu.
     * On essaie aussi de gérer les cas où l'objet est une String, éventuellement vide, et la colonne un type autre que varchar,
     * pour éviter des erreurs sql.
     * 
     * @param gsql La connexion
     * @param values Une liste de valeurs, dans l'ordre des colonnes
     */
    def insertInDb(Sql gsql, values) {
        def qms = ""
        if (emitInsertDebugPrints) { values.eachWithIndex {v,i-> println "$i:'$v'" } }
        def tn = getTableName()
        def colsToInsert = []
        def colNamesToInsert = []
        def valsToInsert = []
        columnDefs.eachWithIndex {cd, index ->
            Object val = values[index]
            //ColumnDef cd = getColumnDef(col.name)
            if (cd.autogenerated && val == null) {
                //ignorer cette colonne
            }
            else {
              colsToInsert << cd
              colNamesToInsert << cd.name
              handleColumnValue(val, valsToInsert, cd)
            }// if (col.autogenerated && val == null)
        }
        if (valsToInsert.size() > 0) qms = (",?" * valsToInsert.size())[1..-1]
        def allInsCols = colNamesToInsert.join(',')
        def insertSql = "INSERT INTO $tn($allInsCols) VALUES ($qms)" as String
        if (emitInsertDebugPrints) println("Insert sql : $insertSql , Vals to insert : $valsToInsert")
		def generatedValues = gsql.executeInsert insertSql, valsToInsert
		//now use the returned list of rows to set the values that were generated
		def j = 0
		columnDefs.eachWithIndex {cd, index ->
            if (cd.autogenerated && values[index] == null) {
                values[index] = generatedValues[0][j] 
                j++
            }
        }
	}

	/**
	 * Update this object row
	 * Returns nr. of updated rows (normally 1)
	 */
	def updateToDb(Sql gsql, values) 
      throws NullNotAllowedException
    {
	    def allColNames = getAllColNames()
	    def allVals = []
		def sets = ""
        //préparer la partie des SET x=?,y=? etc. en n'incluant pas les clés primaires
		columnDefs.each {colDef ->
            if (colDef.isPrimaryKey()) return
			if (sets.length() > 0) sets += ','
			sets += colDef.name
			sets += '=?'
            Object val = getValue(values, colDef)
            handleColumnValue(val, allVals, colDef)
		}
		def wheres = ""
		def allPkColDefs = pkDefs
		allPkColDefs.eachWithIndex {colDef, index ->
			if (index > 0) wheres += ' AND '
			wheres += colDef.name
			wheres += '=?'
            Object val = getValue(values, colDef)
            handleColumnValue(val, allVals, colDef) //ajouter la valeur pour cette partie de clé primaire
		}
		def tn = getTableName()
		def updateSql = "UPDATE $tn SET $sets WHERE $wheres" as String
		def nrowsu = gsql.executeUpdate updateSql, allVals
		return nrowsu
	}
  
    /**
     * Effacer de la table l'enregistrement qui correspond aux valeurs de clé primaire passées
     * @param gsql La connexion sql
     * @param values Les valeurs d'enregistrement, avec les valeurs de clé primaire renseignées
     * @return le nombre d'enregistrements effaces (ne devrait pas être &gt;1)
     */
    def deleteFromDb(Sql gsql, List values) {
      def allVals = []
      def wheres = ""
      def allPkColDefs = pkDefs
      allPkColDefs.eachWithIndex {colDef, index ->
          if (index > 0) wheres += ' AND '
          wheres += colDef.name
          wheres += '=?'
          Object val = getValue(values, colDef)
          handleColumnValue(val, allVals, colDef) //ajouter la valeur pour cette partie de clé primaire
      }
      def tn = getTableName()
      def deleteSql = "DELETE FROM $tn WHERE $wheres" as String
      def nrowsd = gsql.executeUpdate deleteSql, allVals
      return nrowsd
    }

	/** Renvoie la colonne qui est à l'index donné (commence à 0) */
	ColumnDef getColumn(int index) { return columnDefs[index] }
	
	/**
	 * Renvoie la colonne dont le nom est donné dans name.
	 * Renvoie null si la colonne n'existe pas.
	 */
	ColumnDef getColumn(String cname) { return columnDefsByName[cname] }
	
	/**
	 * Renvoie le nombre de colonnes déclarées
	 */
	int getColumnCount() { columnDefs.size() }
	
	/**
	 * Retourne le numéro de la colonne. Attention ici les numéros commencent
	 * à 0
	 */
	int getColumnIndex(String cname) {
	    Integer ix = columnDefIndexesByName[cname]
	    // println("For column '$cname' got index $ix")
	    return (ix == null) ? -1 : ix //return ix or -1 if null
	}

	List<ColumnDef> getColumns(String[] colNames) {
	    def cols = []
	    colNames.each {colName ->
	        def col = getColumn(colName)
	        if (col == null) throw new Exception("Column not found '$colName'")
	        else cols << col
	    }
	    return cols
	}
	
	/**
	 * Teste si la rangée existe dans la base de données, en utilisant les clés
	 * primaires.
	 * @return true si c'est le cas
	 */
	boolean rowExists(Sql gsql, List keyValues) {
	    def tn = getTableName()
        def allPkColNames = getAllPkColNames()
        def wheres = ""
        allPkColNames.eachWithIndex {colName, index ->
            if (index > 0) wheres += ' AND '
            wheres += colName
            wheres += '=?'
        }
	    def selectSql = "SELECT count(*) rowcount FROM $tn WHERE $wheres"
	    def row = gsql.firstRow selectSql, keyValues
	    return row.rowcount > 0
	}
	
	/**
	 * Produit des valeurs correctes à partir des chaînes de caractère passées
	 * en paramètre, la liste doit être dans l'ordre de déclaration des colonnes
	 */
	List makeValuesFromStrings(List<String> strVals, DaPreferences prefs) {
	    def results = []
	    def sz = strVals.size()
	    for (int i = 0; i < sz; i++) {
	        def col = columnDefs[i]
	        if (emitDebugPrints) println ("col '${col.name}'")
	        def val = col.stringToValue(strVals[i], prefs)
	        if (emitDebugPrints) println("val:$val")
	        results << val
	    }
	    return results;
	}
	
	/**
	 * Lit les valeurs depuis la base de données.
	 * Les clés doivent avoir été mises au bon endroit dans le tableau.
	 * Met à jour le tableau passé en paramètre, et renvoie aussi ce tableau.
	 */
	def readFromDb(Sql gsql, values) {
	    def tn = getTableName()
	    def allCols = getAllColNames().join(',')
	    def keyValues = []
        def allPkColNames = getAllPkColNames()
        def wheres = ""
        allPkColNames.eachWithIndex {colName, index ->
            if (index > 0) wheres += ' AND '
            wheres += colName
            wheres += '=?'
        }
	    pkDefs.each {k -> keyValues << values[columnDefIndexesByName[k.name]] }
	    def selectSql = "SELECT $allCols rowcount FROM $tn WHERE $wheres"
	    def row = gsql.firstRow selectSql, keyValues
	    def rowSize = row.size()
	    for (int i = 0; i < rowSize; i++) { values[i] = row.getAt(i) }
	    return values
	}
	
    /**
     * Crée une nouvelle rangée de Dao
     * @return Un nouvel objet DaoRow, avec ce Dao en parent
     */
	def makeNewDaRow() { return new DaRow(this) }
	
  /**
   * Créer une instruction DDL pour définir cette table dans la base, avec une clause "if not exists"
   * @param extraSql le SQL à rajouter à la fin. Doit contenir la virgule initiale qui sépare cette instruction de celles qui le précèdent.
   * @param dialect Le nom du dialecte à utiliser. Seul "H2" est supporté pour l'instant.
   * @return L'instruction DDL de création de la table.
   */
   String makeTableDdl(String extraSql, String dialect) {
     return makeTableDdl(extraSql, dialect, true)
   }
   
  /**
   * Créer une instruction DDL pour définir cette table dans la base.
   * @param extraSql le SQL à rajouter à la fin. Doit contenir la virgule initiale qui sépare cette instruction de celles qui le précèdent.
   * @param dialect Le nom du dialecte à utiliser. Seul "H2" est supporté pour l'instant.
   * @param ifNotExists Si true, ajoute une clause IF NOT EXISTS à la création de table.
   * @return L'instruction DDL de création de la table.
   */
    String makeTableDdl(String extraSql, String dialect, boolean ifNotExists) {
      if (!dialect.equalsIgnoreCase("H2")) return "dialecte non supporté : $dialect"
      String nl = System.lineSeparator()
      String ine = ifNotExists ? 'IF NOT EXISTS' : ''
      StringBuilder sb = new StringBuilder()
      sb << "CREATE TABLE $ine $tableName ("
      boolean first = true
      //on commence par déclarer la ou les clés primaires
      if (pkDefs.size() == 1) {
        sb << nl
        //cle primaire composee d'une seule colonne
        sb << pkDefs[0].getDdl(dialect) << " PRIMARY KEY"
        first = false
      }
      //ensuite on déclare chaque colonne
      columnDefs.each {c ->
        if (emitDebugPrints) println "c -> ${c.name}"
        if (pkDefs.contains(c) && pkDefs.size() == 1) {
          if (emitDebugPrints) println "pk : $pkDefs contains col $c"
          return; //si c'est une clé primaire unique, elle a déjà été déclarée
        }
        if (emitDebugPrints) println "col ddl for ${c.name}"
        if (first) { first = false } else { sb << "," }
        sb << nl
        sb << c.getDdl(dialect)         
      }
      //puis si la clé primaire est composée de plusieurs colonnes, on les ajoute
      if (pkDefs.size() > 1) {
        if (first) first = false
        else sb << ","
        sb << nl
        //cle multiple, declarer cette contrainte à la fin
        def pkNames = pkDefs*.name
        sb << "PRIMARY KEY(${pkNames.join(',')})"
      }
      //enfin on ajoute le SQL supplémentaire si nécessaire
      if (extraSql != null) {
        sb << extraSql
        first = false
      }
      if (first) first = false
      else sb << nl
      sb << ")"
      return sb.toString();
    } 
    
    /**
     * A partir d'un FszNode (voir pmsixml), prend chaque valeur correspondant à une colonne et si elle existe et n'est pas null
     * la met dans le tableau de valeurs
     * @param aNode un noeud de type FszGroup
     * @return un tableau de valeurs qui contient les objets correspondants au noeud FszNode
     */
    def makeValues(FszNode aNode) {
      Date dt
      java.sql.Date sqlDt
      //println "aNode:$aNode"
      def values = makeEmptyValueList()
      columnDefs.each {ColumnDef colDef->
        def nd = aNode.getChild(colDef.name.toUpperCase())
        //println "  nd:$nd"
        if (nd && nd.isField()) {
          switch (colDef.getSqlType()) {
            case Types.VARCHAR:
            case Types.CHAR:
            setValue(values, colDef.name, ((FszField)nd).valueAsText)
            break;
            case Types.NUMERIC:
            setValue(values, colDef.name, ((FszField)nd).correctedValue) //Renvoie la vraie valeur numérique, par exemple si le type est 5+3 renvoie 1234.567 pour 1234567
            break;
            case Types.DATE:
            case Types.TIMESTAMP: //il n'y a jamais de TIMESTAMPs dans les données PMSI
            dt = ((FszField)nd).toDate()
            if (dt == null) setValue(values, colDef.name, null)
            else {
              sqlDt = new java.sql.Date(dt.getTime())
              setValue(values, colDef.name, sqlDt)
            }
            break;
            default:
            lg.warn("Type sql imprevu " + colDef.getSqlType() + " utilisation de getValueAsObject sur " + nd.toString())
            setValue(values, colDef.name, nd.valueAsObject)
          }//switch
        }
      }
      return values
    }

    String toString() {
      def s = "Dao($tableName,${pkDefs.size()},${columnDefs.size()})"
      return s.toString()
    }

    /**
     * Quel est le dialecte SQL utilisé.
     * @return le dialecte SQL qui sera utilisé
     */
    public String getDialect() {
      return dialect;
    }

    /**
     * Mettre le dialecte SQL à utiliser
     * @param dialect le nom du dialecte SQL à utiliser, par défaut "H2"
     */
    public void setDialect(String dialect) {
      this.dialect = dialect;
    }

}
