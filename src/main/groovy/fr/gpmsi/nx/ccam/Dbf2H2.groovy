package fr.gpmsi.nx.ccam;

import org.apache.log4j.LogManager
import org.apache.log4j.Logger

import fr.gpmsi.Chrono
import fr.gpmsi.DbfRow
import fr.gpmsi.Groovy
import fr.gpmsi.GroovyScriptsBase
import fr.gpmsi.StringUtils
import groovy.sql.Sql
import groovy.xml.XmlSlurper

/*
 * Transfert de données depuis l'ensemble de tables DBF Ameli vers une base H2
 * Dernière exécution : 3 427 112 enregistrements transférés en 293.392 secondes (4 mn 53 s)
 * Taille de la base H2 à la fin : 131 MO, avec 7-zip compression zip ultra : 33 MO
 */

class Dbf2H2 {
    private static final String TABLES_DDL_RESOURCE = '/fr/gpmsi/pmsixml/nx/tables-nx-DBF201301v9_h2_create.ddl'
    private static final String TABLES_XML_RESOURCE = '/fr/gpmsi/pmsixml/nx/tables-nx-DBF201301v9.xml'
    private static Logger lg = LogManager.getLogger(Dbf2H2.class)
     
    File dbfDir
    Sql gsql
    String tableCreationResourceName = '/fr/gpmsi/pmsixml/nx/tables-nx-DBF201301v9_h2_create.ddl'
    List<TableMeta> tableMetas = []
    boolean printFeedback = true
    
    class ColumnMeta {
        String name
        String type
        int maxLength
        int precision
        int scale
    }
    
    class TableMeta {
        String name
        List<ColumnMeta> columnMetas = []
    }
    
    class CcamScriptBase extends  GroovyScriptsBase {

        @Override
        public Object run() {
            return null;
        }
        
    }
    
    def tableNames = [
        'R_AAP_PMSI',
        'R_ACTE',
        'R_ACTE_CLASSE_DMT',
        'R_ACTE_CMUC',
        'R_ACTE_COND_GEN',
        'R_ACTE_EXO_TM',
        'R_ACTE_FORFAIT',
        'R_ACTE_IVITE',
        'R_ACTE_IVITE_PHASE',
        'R_ACTE_NAT_ASS',
        'R_ACTE_PRESCRIPTEUR',
        'R_ACTIVITE',
        'R_ACTIVITE_AGRE_RADIO',
        'R_ACTIVITE_EXECUTANT',
        'R_ACTIVITE_EXTENSION',
        'R_ACTIVITE_MODIFICATEUR',
        'R_ACTIVITE_PHASE_DENT',
        'R_ACTIVITE_PHASE_DOM',
        'R_ACTIVITE_RMO',
        'R_AGRE_RADIO',
        'R_ASSOCIATIONS',
        'R_CATE_SPEC',
        'R_CATEGORIE_MEDIC',
        'R_CLASSE_DMT',
        'R_COMPAT_EXO_TM',
        'R_COND_GEN',
        'R_CONTEXT_BN',
        'R_CONTEXT_PS',
        'R_DENT',
        'R_DOM',
        'R_EXO_TM',
        'R_EXTENSION',
        'R_FORFAIT',
        'R_FRAIS_DEP',
        'R_GLOSSAIRE',
        'R_INCOMPATIBILITES',
        'R_MENU',
        'R_NAT_ASS',
        'R_NOTE_ACTE',
        'R_NOTE_MENU',
        'R_PAIEMENT',
        'R_PHASE',
        'R_PROCEDURE',
        'R_PU_BASE',
        'R_REGROUPEMENT',
        'R_REMBOURSEMENT',
        'R_RMO',
        'R_TB01',
        'R_TB02',
        'R_TB03',
        'R_TB04',
        'R_TB05',
        'R_TB06',
        'R_TB07',
        'R_TB08',
        'R_TB09',
        'R_TB10',
        'R_TB11',
        'R_TB12',
        'R_TB13',
        'R_TB14',
        'R_TB15',
        'R_TB17',
        'R_TB18',
        'R_TB19',
        'R_TB20',
        'R_TB21',
        'R_TB22',
        'R_TB23',
        'R_TYPE',
        'R_TYPE_NOTE',
        'R_UNITE_OEUVRE'
        ]
    
    Sql connectH2(url, user, password) {
        gsql = Sql.newInstance(url, user, password)
        return gsql
    }
    
    void dropTables() {
        println "DROP des tables"
        tableNames.each {tn->
            gsql.execute("DROP TABLE IF EXISTS $tn" as String)
        }
    }        
    
    void createTables() {
        println "Creation des tables"
        def ddlScript = StringUtils.getUtf8StringResource(TABLES_DDL_RESOURCE);
        //println ddlScript
        gsql.execute(ddlScript)
    }
    
    /**
     * Tronquer à 10 caractères (pour avoir le nom de la colonne DBF à partir de
     * la colonne oracle
     * @param str La string
     * @return la string tronquée à 10 car max
     */
    String trunc10(String str) {
        if (str == null) return null;
        if (str.length() < 10) return str;
        return str.substring(0, 10);
    }
    
    /**
     * Tronquer à 9 caractères (pour avoir le nom de la colonne DBF "extension" à partir de
     * la colonne oracle
     * @param str La string
     * @return la string tronquée à 10 car max
     */
    String trunc9(String str) {
        if (str == null) return null;
        if (str.length() < 10) return str;
        return str.substring(0, 9);
    }
    
    /**
     * Transférer les tables Ccam (traitement spécial des tables textuelles) de dbf vers
     * la base H2. Ici les noms de colonne sont bien tronqués à 10 caractères avant d'être
     * recherchés dans les tables dbf.
     */
    void transferCcamTables() {
        println "Transfert des dbf vers les tables"
        Chrono exeChrono = new Chrono()
        Chrono totalChrono = new Chrono()
        long nrecords = 0
        //reconstruire en mémoire les métadonnées à partir du fichier XML
        String xmlStr = StringUtils.getUtf8StringResource(TABLES_XML_RESOURCE);
        XmlSlurper xslu = new XmlSlurper()
        def tablesRoot = xslu.parseText(xmlStr)
        tablesRoot.children().each {table->
            TableMeta tm = new TableMeta(name: table.@NAME)
            //println "Table : ${table.@NAME}"
            table.COLUMN.each {column->
                //println "Column : ${column.@NAME}"
                ColumnMeta cm = new ColumnMeta(name: column.@NAME, type: column.@TYPE)
                String maxLength = column.'@MAX-LENGTH'
                if (maxLength.length() > 0) { cm.maxLength = maxLength as int } 
                String precision = column.'@PRECISION'
                if (precision.length() > 0) { cm.precision = precision as int } 
                String scale = column.'@SCALE'
                if (scale.length() > 0) { cm.scale = scale as int } 
                tm.columnMetas << cm
            }
            tableMetas << tm
        }//tablesRoot.children().each
        CcamScriptBase gsb = new CcamScriptBase()
        gsb.setBinding(Groovy.getBinding())
        //transférer les données
        tableMetas.each { tm->
            String tableFileName = tm.name + '.dbf'
            File tableFile = new File(dbfDir, tableFileName)
            if (!tableFile.exists()) {
                lg.warn("table $tableFileName non trouvée.")
                return
            }
            gsb.dbf { 
                input tableFile.getAbsolutePath()
                inputEncoding 'cp850' //le DbaseIII est encodé en DOS, qui est le codepage 850
                onItem {item->
                    if (item.linenr == 1) return
                    DbfRow row = item.row
                    Map<String, Object> valuesByColName = [:]
                    List<String> columnList = []
                    tm.columnMetas.each {cm->
                        String colName = cm.name
                        String trunc10ColName = trunc10(colName)
                        columnList << colName
                        String colType = cm.type
                        if (!row.isColumnPresent(trunc10ColName)) {
                            lg.warn("La colonne $tableFileName '$trunc10ColName' n'est pas présente")
                            valuesByColName[colName] = null
                        }
                        else if (colType == 'NUMBER'
                              || colType == 'DATE'
                              || colType == 'TIME' 
                              || colType == 'DATETIME' 
                              || colType == 'TIMESTAMP') {
                            valuesByColName[colName] = row.getValue(trunc10ColName)
                        } 
                        else if (colType == 'VARCHAR') {
                            //reconstruire la valeur découpée
                            String textVal = row.getValue(trunc10ColName)
                            int n = 0;
                            for (n = 0; n < 16; n++) {
                                String dig = Integer.toHexString(n).toUpperCase()
                                String cn2 = trunc9(colName) + dig
                                //on teste cn2 != trunc10ColName a cause de TEXTE_NOTE ou le 'E' ne fait pas partie des codes hexa autorisés ...
                                //on teste la longueur du texte pour ne pas avoir de bugs avec certaines colonnes comme R_TB09.MODIF_COD1 et R_TB09.MODIF_COD1
                                if (textVal.length() >= 254 && cn2 != trunc10ColName && row.owner.csvHeaderRow.contains(cn2)) {
                                    textVal += row.getValue(cn2)
                                }
                            }
                            //println "textval : $textVal"
                            valuesByColName[colName] = textVal
                        }//tm.columnMetas.each
                    }//tm.columnMetas.each
                    //println "valeurs a inserer : $valuesByColName"
                    gsql.executeUpdate('insert into '+tm.name+'('+columnList.join(',')+') values (:'+columnList.join(',:')+')', valuesByColName)
                    nrecords++
                    if (exeChrono.elapsed() > 1000) {
                        //toutes les secondes, donner le nombre d'enregistrements transférés
                        print "\r             \r$nrecords"
                        exeChrono.resetMark()
                    }
                }//onItem
            }//gsb.dbf
        }//tableMetas.each()
        println ""
        println "Transfert terminé"
        println "$nrecords enregistrements transférés en ${totalChrono.elapsed()/1000.0} secondes"
    }
    
    void destructiveCopy() {
        dropTables()
        createTables()
        transferCcamTables()
    } 
}
