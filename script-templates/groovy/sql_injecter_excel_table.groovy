/**:encoding=UTF-8:
 * Injecter un fichier excel en tant que table dans une base de données.
 * Il faut un fichier .csv qui contient les définitions des colonnes.
 * Cf la doc de sql_create_table.groovy pour le format de cette table.
 * Le fichier excel doit contenir les colonnes définies dans le fichier des
 * définitions de colonne (à part pour les colonnes définies avec le nom
 * spécial "*").
 * Les colonnes qui ont un nom de type sql vide sont ignorées (cela peut être
 * utile pour n'importer que certaines colonnes)
 *
 * Arguments :
 * -a:input chemin_du_fichier_excel
 * -a:table le_nom_de_la_table
 * -a:colmd chemin_vers_le_fichier_de_definition_des_colonnes
 * -a:jdbcdriver nom_du_pilote_jdbc
 * -a:jdbcurl url_de_la_base
 * -a:jdbcuser user_de_la_base
 * -a:jdbcpwd mot_de_passe_du_user
 *
 * Exemple :
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\sql_injecter_excel_table.groovy -a:input "C:\t\tests-bi\etude_mouvements_I_PATIEN-v1_2.xlsx" -a:table mouv -a:colmd etude_mouvements_I_PATIEN-v1_2_colonnes.csv -a:jdbcurl "jdbc:h2:./test" -a:jdbcuser sa -a:jdbcpwd "" -a:jdbcdriver "org.h2.Driver"
 */
import fr.gpmsi.StringTable
import static fr.gpmsi.StringUtils.isEmpty
import groovy.sql.Sql

tableName = args.table
colmd = new StringTable("COLMD", new File(args.colmd))

xlpoi {
    input args.input

    onInit {
        sqlConn = Sql.newInstance(args.jdbcurl, args.jdbcuser, args.jdbcpwd, args.jdbcdriver)
        println "Demarrage de l'injection de la table $tableName ..."
    }

    onItem {item ->
        if (item.linenr == 1) return //ignorer la première ligne
        def row = item.row
        //insérer tous les noms et toutes les valeurs
        def names = new StringBuffer()
        def vals = []
        def qms =  new StringBuffer()
        colmd.each {cd ->
            def col_excel = cd.col_excel
            def col_sql = cd.col_sql
            def type_sql = cd.type_sql
            //n'ajouter que si le nom n'est pas "*" et le type sql n'est pas vide
            if (col_excel != '*' && !isEmpty(type_sql)) {
                def cellObj = null
                if (!row.isBlankOrEmpty(col_excel)) {
                    cellObj = row.getCellObject(col_excel)
                }
                vals << cellObj
                if (debug) println "val[$col_excel]:$cellObj"
                if (names.length() > 0) names << ", "
                names << col_sql
                if (qms.length() > 0) qms << ","
                qms << "?"
            }
        }
        sqlConn.execute "INSERT INTO $tableName($names) VALUES ($qms)", vals
        //envoyer un petit suivi visuel de la ligne en cours
        if (item.linenr % 100 == 0) print "\r${item.linenr}                    "
    }

    onEnd {
        //Effacer le dernier chiffre
        println "\r                                  "
        println "Fin de l'injection."
    }
}//xlpoi
