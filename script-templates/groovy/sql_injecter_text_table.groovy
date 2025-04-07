/**:encoding=UTF-8:
 * Injecter un fichier texte (séparateur tabulation) en tant que table dans une base de données.
 * Il faut un fichier .csv qui contient les définitions des colonnes.
 * Cf la doc de sql_create_table.groovy pour le format de cette table.
 * Le fichier excel doit contenir les colonnes définies dans le fichier des
 * définitions de colonne (à part pour les colonnes définies avec le nom
 * spécial "*").
 * Les colonnes qui ont un nom de type sql vide sont ignorées (cela peut être
 * utile pour n'importer que certaines colonnes)
 *
 * Arguments :
 * -a:input chemin_du_fichier_texte
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

linesProcessed = 0

csv {
    input args.input, 'UTF-8'
    csvSeparator '\t' as char

    onInit {
        sqlConn = Sql.newInstance(args.jdbcurl, args.jdbcuser, args.jdbcpwd, args.jdbcdriver)
        println "Demarrage de l'injection de la table $tableName ..."
    }

    onItem {item ->
        if (item.linenr == 1) return //ignorer la première ligne qui sont les titres
        def row = item.row
        //insérer tous les noms et toutes les valeurs
        def names = new StringBuffer()  //noms de colonne pour le sql
        def vals = []                  //valeurs
        def qms =  new StringBuffer() //les points d'interrogation (question marks)
        colmd.each {cd ->
            def col_excel = cd.col_excel
            def col_sql = cd.col_sql
            def type_sql = cd.type_sql
            //n'ajouter que si le nom n'est pas "*" et le type sql n'est pas vide
            if (col_excel != '*' && !isEmpty(type_sql)) {
                def textVal = row."$col_excel"  //lecture indirecte utilisant le nom dans la variable, astuce utile permise par groovy !
                vals << textVal
                if (debug) println "val[$col_excel]:$textVal"
                if (names.length() > 0) names << ", "
                names << col_sql
                if (qms.length() > 0) qms << ","
                qms << "?"
            }
        }
        sqlConn.execute "INSERT INTO $tableName($names) VALUES ($qms)", vals
        linesProcessed++
        //envoyer un petit suivi visuel du nombre de lignes traitées
        if (linesProcessed % 100 == 0) print "\r${linesProcessed}                    "
    }

    onEnd {
        //Effacer le dernier chiffre
        println "\r                                  "
        println "Fin de l'injection."
        println "$linesProcessed enregistrements injectes."
    }
}//csv
