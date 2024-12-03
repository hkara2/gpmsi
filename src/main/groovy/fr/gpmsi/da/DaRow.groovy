//:encoding=UTF-8:
package fr.gpmsi.da

import groovy.sql.Sql
import fr.gpmsi.CsvRow

/**
 * Un accès à une rangèe de donnèes.
 * C'est une combinaison d'un objet d'accès aux données et d'un tableau de
 * valeurs, qui va servir pour les échanges avec la base de données (ou comme
 * stockage intermédiaire, pour recopie dans un autre tableau de valeurs).
 * L'accès aux propriétés et aux méthodes de cette classe a été changé.
 * Les méthodes <b>vget</b>xxx et <b>vset</b>xxx permettent l'accès à la 
 * valeur de la colonne xxx, et la propriété xxx donne l'accès à la définition
 * de la colonne xxx (attention, sensible à la casse !).
 */
class DaRow {
    Dao dao
    def values = []
    
    //initialiseur statique pour les modifications de métaclasse
    static {
        /**
         * modifier l'acces aux propriétés de manière à ce qu'un nom de 
         * colonne donne l'accès à la définition de la colonne.
         */
        DaRow.metaClass.getProperty = {String propName ->
            def meta = DaRow.metaClass.getMetaProperty(propName)
            if (meta) {
                meta.getProperty(delegate)
            } else {
                delegate.getColumn(propName) 
            }
        } //DaRow.metaClass.getProperty
    }//static
    
    DaRow(Dao dao) {
        this.dao = dao
        values = [null] * dao.columnCount
    }
    
    /**
     * Pour les methodes qui commencent par "vget" et "vset" on accede a la
     * valeur indiquee par la colonne. Par exemple <code>vgetid()</code> est
     * équivalent à <code>getValue("id")</code>, et <code>vsetid(10)</code>
     * <code>setValue("id", 10)</code>
     */
    def invokeMethod(String name, Object args) {
        if (name.startsWith('vget')) {
            String colName = name.substring(4)
            int ix = dao.getColumnIndex(colName)
            // println("Column index of '$colName' : $ix")
            if (ix < 0) {
                throw new RuntimeException("Colonne inconnue '$colName'")
            }
            if (args.size() > 0) {
                throw new RuntimeException("Pas d'argument autorises pour un appel 'vget' a '$colName'")
            }
            return values[ix]
        }
        else if (name.startsWith('vset')) {
            String colName = name.substring(4)
            int ix = dao.getColumnIndex(colName)
            // println("Column index of '$colName' : $ix")
            if (ix < 0) {
                throw new RuntimeException("Colonne inconnue '$colName'")
            }
            if (args.size() != 1) {
                throw new RuntimeException("Un seul argument attendu pour un appel 'vset' a '$colName'")
            }
            values[ix] = args[0]
        }
        else throw new RuntimeException("Methode inconnue $name")
    }
     
    /**
     * Lire la rangée dans la base de donnée, en se servant des clés qui
     * doivent avoir préalablement été remplies dans la rangée
     */
    def readFromDb(Sql gsql) { return dao.readFromDb(values) }
    
    /**
     * Attribuer les valeurs, en se servant des chaînes de caractère
     * passées en paramètre, qui doivent être dans l'ordre de déclaration.
     */
    def setValuesFromStrings(List<String> stringValues, DaPreferences prefs) {
        values = dao.makeValuesFromStrings(stringValues, prefs) 
    }
    
    /**
     * Attribuer les valeurs (par nom) depuis la rangée csv. Tous les noms de la rangée
     * csv doivent correspondre à un nom de la rangée.
     * L'ordre des colonnes dans la rangée csv n'a pas d'importance.
     * Seuls les noms des colonnes csv sont importants et doivent correspondre 
     * à des noms présents dans le Dao.
     */
    def setValuesFromCsvRow(CsvRow row, DaPreferences prefs) {
        String[] names = row.getOwner().getCsvHeaderRow()
        String[] values = row.getValues()
        names.eachWithIndex {name, index->
            def col = dao.getColumn(name)
            if (col == null) throw new Exception("Colonne non trouvee '$name'")
            setValue(name, col.stringToValue(values[index], prefs))
        }
    }
    
    /**
     * Mettre les valeurs du tableau d'un seul coup, à l'aide d'un nouveau 
     * tableau qui viendra prendre la place de l'ancien (pas de copie ici)
     */
    def setValues(newValues) {
        if (newValues.size() != dao.columnCount) {
            throw new Exception("La taille du tableau est de ${newValues.size()}, on attendait ${dao.columnCount}")
        }
        values = newValues 
    }
    
    /**
     * mettre la valeur de la colonne dans le tableau des valeurs
     */
    def setValue(String columnName, Object val) {
        int columnIndex = dao.getColumnIndex(columnName)
        if (columnIndex < 0) throw new Exception("Unknown column '$columnName'")
        values[columnIndex] = val
    }
    
    /**
     * récupérer la valeur de la colonne dans le tableau des valeurs
     */
    def getValue(String columnName) {
        int columnIndex = dao.getColumnIndex(columnName)
        if (columnIndex < 0) throw new Exception("Unknown column '$columnName'")
        return values[columnIndex]
    }
    
    /**
     * Mettre à jour la base avec les valeurs du tableau.
     * @return le nombre d'enregistrements modifi�s (normalement 1)
     */
    def updateToDb(Sql gsql) { return dao.updateToDb(gsql, values) }
    
    /**
     * Insère les valeurs du tableau dans la base.
     * Si une ou plusieurs colonnes sont de type "autogenerated", et que leur
     * valeur dans le tableau des valeurs est null, la base de données
     * génèrera automatiquement ces valeurs, qui seront ensuite mises dans le
     * tableau des valeurs à la place des valeurs null.
     * Si les valeurs ne sont pas à null, elles seront utilisées telles quelles,
     * ce qui est déconseillé étant donné que c'est la base qui doit gérer ces
     * numéros (à moins que l'on sache vraiment ce que l'on fait, il vaut mieux
     * laisser la base gérer les numéros lorsque la numérotation est automatique)
     */
    def insertInDb(Sql gsql) { return dao.insertInDb(gsql, values) }
    
    /**
     * Copier toutes les colonnes dont le nom est dans la liste vers une
     * autre rangee
     */
    def copyColumnsTo(List<String> columnNames, DaRow otherRow) {
        columnNames.each {name ->
            otherRow.setValue(name, getValue(name))
        }
    }
    
    /**
     * Copier toutes les colonnes dont le nom est dans la liste vers une
     * autre rangee, mais en utilisant d'autres noms de colonne, avec une
     * table de correspondance (mapper)
     */
    def copyColumnsToMapped(List<String> columnNames, ColumnMapper mapper, DaRow otherRow) {
        columnNames.each {name ->
            String targetName = mapper.getTargetColumnName(name)
            if (targetName == null) {
                throw new Exception("Pas de correspondance trouvee pour la colonne '$name'")
            }
            otherRow.setValue(targetName, getValue(name))
        }
    }
    
    /**
     * Copier toutes les colonnes de cette rangée vers une autre rangée.
     * La copie se fait par nom, pas par ordre dans le tableau.
     */
    def copyAllColumnsTo(DaRow otherRow) {
        copyColumnsTo(dao.getAllColNames(), otherRow)
    }
    
    /**
     * Copier toutes les colonnes de cette rangée vers une autre rangée.
     * La copie se fait par nom, pas par ordre dans le tableau.
     * On utilise un transcodage des noms de colonnes avec 'mapper'.
     */
    def copyAllColumnsToMapped(DaRow otherRow, ColumnMapper mapper) {
        copyColumnsToMapped(dao.getAllColNames(), mapper, otherRow)
    }
    
}


