/**:encoding=UTF-8:
 * class Dbf2H2
 */
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

/**
 * Classe de creation des tables CCAM dans une base H2, à partir de fichiers
 * dbf fournis par l'assurance maladie.
 * Les chemins des "resources" à utiliser sont dans les champs :
 * - tablesCreationResourceName
 * - tablesXmlResourceName
 * Ils sont initialisés avec des valeurs par défaut valides (au 13/11/2026)
 *
 */
class Dbf2H2 {
    private static final String DEF_TABLES_DDL_RESOURCE = '/fr/gpmsi/pmsixml/nx/tables-nx-DBF201301v9_h2_create.ddl'
    private static final String DEF_TABLES_XML_RESOURCE = '/fr/gpmsi/pmsixml/nx/tables-nx-DBF201301v9.xml'
    private static Logger lg = LogManager.getLogger(Dbf2H2.class)

    File dbfDir
    Sql gsql
    String tablesCreationResourceName = DEF_TABLES_DDL_RESOURCE
    String tablesXmlResourceName = DEF_TABLES_XML_RESOURCE
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
        def ddlScript = StringUtils.getUtf8StringResource(tablesCreationResourceName);
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
        String xmlStr = StringUtils.getUtf8StringResource(tablesXmlResourceName);
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
                        print "\r                        \r$nrecords enregistrements"
                        exeChrono.resetMark()
                    }
                }//onItem
            }//gsb.dbf
        }//tableMetas.each()
        println ""
        println "Transfert terminé"
        println "$nrecords enregistrements transférés en ${totalChrono.elapsed()/1000.0} secondes"
        println ""
        println "Ajout colonne R_ACTE(DT_FIN_EFFET) ..."
        exeChrono.resetMark()
        gsql.execute('ALTER TABLE r_acte ADD dt_fin_effet DATE')
        println "... en ${exeChrono.elapsed()/1000.0} secondes"
        println ""
        println "Ajout index sur R_ACTE(COD_ACTE) ..."
        exeChrono.resetMark()
        gsql.execute('CREATE INDEX r_acte_cod_acte ON r_acte(cod_acte)')
        println "... en ${exeChrono.elapsed()/1000.0} secondes"
        println ""
        println "Ajout index sur R_ACTE(COD_ACTE, DT_EFFET) ..."
        exeChrono.resetMark()
        gsql.execute('CREATE INDEX r_acte_cod_acte_dt_effet ON r_acte(cod_acte, dt_effet)')
        println "... en ${exeChrono.elapsed()/1000.0} secondes"
        println ""
        println "Ajout valeurs dans R_ACTE(DT_FIN_EFFET) ..."
        exeChrono.resetMark()
        gsql.execute('''
UPDATE r_acte ra1 SET ra1.dt_fin_effet = (
  SELECT DATEADD(DAY, -1, MIN(dt_effet))
  FROM r_acte ra2
  WHERE ra2.cod_acte = ra1.cod_acte AND ra2.dt_effet > ra1.dt_effet
)
        ''')
        println "... en ${exeChrono.elapsed()/1000.0} secondes"
        println ""
        println "Ajout valeurs de fin 2099-12-31 dans R_ACTE(DT_FIN_EFFET) ..."
        exeChrono.resetMark()
        gsql.execute("UPDATE r_acte SET dt_fin_effet = DATE '2099-12-31' WHERE dt_fin_effet IS NULL")
        println "... en ${exeChrono.elapsed()/1000.0} secondes"
        println ""
        println "Compactage de la base ..."
        exeChrono.resetMark()
        //faire une fermeture avec compactage de la base
        try {
          //ferme et compacte.
          //si on passe par gsql.execute(), cela envoie une exception que
          //l'on ignore, le compactage est bien fonctionnel.
          //en passant par un statement créé par la connexion directe, il n'y a aucun avertissement.
          gsql.getConnection().createStatement().execute('SHUTDOWN COMPACT') 
        }
        catch (org.h2.jdbc.JdbcSQLNonTransientConnectionException ignored) {}
        println "... en ${exeChrono.elapsed()/1000.0} secondes"
        println ""
        println "Temps total d'execution : ${totalChrono.elapsed()/1000.0} secondes"
    }

    void destructiveCopy() {
        dropTables()
        createTables()
        transferCcamTables()
    }
}

/*
Notes sur manipulations de table pour avoir calcul dt_fin_effet

create index r_acte_cod_acte on r_acte(cod_acte)  -- 2.7 s

update r_acte ra1 set ra1.dt_fin_effet = (
    select lead(dt_effet, 1) ignore nulls over (partition by cod_acte order by dt_effet)
      from r_acte ra2
    where ra2.cod_acte = ra1.cod_acte and ra2.dt_effet = ra1.dt_effet
  )

246 s

-> finit mais ne marche pas (dt_fin_effet est toujours null)

update r_acte ra1 set ra1.dt_fin_effet = (
  select min(dt_effet)
  from r_acte ra2
  where ra2.cod_acte = ra1.cod_acte and ra2.dt_effet > ra1.dt_effet
)

128 s

OK fonctionne.
Même requête mais pour avoir la veille, ce qui permet d'utiliser "between"

update r_acte ra1 set ra1.dt_fin_effet = (
  select dateadd(DAY, -1, min(dt_effet) )
  from r_acte ra2
  where ra2.cod_acte = ra1.cod_acte and ra2.dt_effet > ra1.dt_effet
)

528 s

update r_acte set dt_fin_effet = DATE '2099-12-31' where dt_fin_effet is null

1.2 s

select * from r_acte order by cod_acte, dt_effet

create index r_acte_cod_acte_dt_effet on r_acte(cod_acte, dt_effet)

0.84 s

update r_acte ra1 set ra1.dt_fin_effet = (
  select dateadd(DAY, -1, min(dt_effet) )
  from r_acte ra2
  where ra2.cod_acte = ra1.cod_acte and ra2.dt_effet > ra1.dt_effet
)

107 s -> mieux !

select * from r_acte where cod_acte = 'AAFA001' and DATE '2024-12-31' between dt_effet and dt_fin_effet
   ID  COD_ACTE   DT_MODIF   MENU_COD  FRAIDP_COD  TYPE_COD  COD_STRUCT  NOM_COURT                                  NOM_LONG                                                             SEXE  DT_CREATION  DT_FIN  ENTENTE   DT_EFFET   DT_ARRETE     DT_JO     MFIC_PLACE  PRECEDENT  SUIVANT  REMBOU_COD  DT_FIN_EFFET
-----  --------  ----------  --------  ----------  --------  ----------  -----------------------------------------  -------------------------------------------------------------------  ----  -----------  ------  -------  ----------  ----------  ----------  ----------  ---------  -------  ----------  ------------
18314  AAFA001   2022-03-03        44  N                  0              exérèse T. intraparench cervelet craniot.  Exérèse de tumeur intraparenchymateuse du cervelet, par craniotomie     0  2005-03-01   <null>  N        2022-04-01  2021-09-22  2021-09-25    1.000000                               1   2024-12-31 

select * from r_acte where cod_acte = 'AAFA001' and DATE '2025-01-01' between dt_effet and dt_fin_effet
   ID  COD_ACTE   DT_MODIF   MENU_COD  FRAIDP_COD  TYPE_COD  COD_STRUCT  NOM_COURT                                  NOM_LONG                                                             SEXE  DT_CREATION  DT_FIN  ENTENTE   DT_EFFET   DT_ARRETE     DT_JO     MFIC_PLACE  PRECEDENT  SUIVANT  REMBOU_COD  DT_FIN_EFFET
-----  --------  ----------  --------  ----------  --------  ----------  -----------------------------------------  -------------------------------------------------------------------  ----  -----------  ------  -------  ----------  ----------  ----------  ----------  ---------  -------  ----------  ------------
15930  AAFA001   2024-11-04        44  N                  0              exérèse T. intraparench cervelet craniot.  Exérèse de tumeur intraparenchymateuse du cervelet, par craniotomie     0  2005-03-01   <null>  N        2025-01-01  2024-06-20  2024-06-21    1.000000                               1   2099-12-31 

----------------------------------

C:\Local\CCAM\v78\dbf>%GPMSI_BASE%\v2.1\gpmsi -script %GPMSI_BASE%\v2.1\scripts\groovy\dbf_ccam_tout_vers_h2.groovy  -a:h2uri jdbc:h2:file:./ccam
C:\Local\CCAM\v78\dbf
DROP des tables
Creation des tables
Transfert des dbf vers les tables
3896998 enregistrements
Transfert terminé
3899416 enregistrements transférés en 688.26 secondes

Ajout colonne R_ACTE(DT_FIN_EFFET) ...
... en 2.203 secondes

Ajout index sur R_ACTE(COD_ACTE) ...
... en 0.656 secondes

Ajout index sur R_ACTE(COD_ACTE, DT_EFFET) ...
... en 0.875 secondes

Ajout valeurs dans R_ACTE(DT_FIN_EFFET) ...
... en 69.348 secondes

Ajout valeurs de fin 2099-12-31 dans R_ACTE(DT_FIN_EFFET) ...
... en 4.062 secondes

Compactage de la base ...
nov. 13, 2026 7:28:11 PM groovy.sql.Sql execute
AVERTISSEMENT: Failed to execute: SHUTDOWN COMPACT because: La base de donnÚes est dÚjÓ fermÚe (pour dÚsactiver la fermeture automatique Ó l'arrÛt de la VM, ajoutez "; DB_CLOSE_ON_EXIT = FALSE" Ó l'URL db)
Database is already closed (to disable automatic closing at VM shutdown, add ";DB_CLOSE_ON_EXIT=FALSE" to the db URL) [90121-214]
... en 35.799 secondes

Temps total d'execution : 801.25 secondes



*/