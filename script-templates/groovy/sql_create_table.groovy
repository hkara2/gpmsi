/**:encoding=UTF-8:
 * A partir de définitions de colonnes, créer une instruction sql de création
 * de table.
 *
 * Arguments :
 * -a:table nom_de_la_table_a_creer
 * -a:colmd chemin_du_fichier_de_definitions_de_colonnes
 * -a:jdbcdriver nom_du_pilote_jdbc  (optionnel)
 * -a:jdbcurl url_de_la_base         (optionnel)
 * -a:jdbcuser user_de_la_base       (optionnel)
 * -a:jdbcpwd mot_de_passe_du_user   (optionnel)
 *
 * Format des définitions de colonne :
 * Fichier .csv, encodage ISO-8859-1
 * Nom des colonnes :
 * col_excel        Nom de la colonne dans le fichier excel
 * col_sql          Nom de la colonne dans la base de donnees
 * type_sql         Type SQL de la colonne
 * type_jdbc        Type JDBC de la colonne (cf. java.sql.Types). Si vide, on utilise type_sql à la place
 * longueur_sql     Longueur (pour VARCHAR, ou NUMERIC)
 * echelle_sql      Echelle (correspond à SCALE pour NUMERIC ou DECIMAL)
 * contrainte1_sql  Texte de contrainte a insérer avant la déclaration de type
 * contrainte2_sql  Texte de contrainte a insérer après la déclaration de type
 * format_donnee    Formatage de la donnée que l'on va retrouver dans le fichier (Date ou numérique, selon le type_sql). Optionnel.
 * remarques        Remarques additionnelles sur la colonne
 *
 * Si col_sql est vide, la colonne est ignorée.
 *
 * Les noms de colonnes sont utilisés tels quels dans la base. Si il faut les
 * mettre entre guillemets, mettre les guillemets dans le fichier de métadonnées.
 *
 * Exemple :
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\sql_create_table.groovy -a:table mouv -a:colmd etude_mouvements_I_PATIEN-v1_2_colonnes.csv -a:jdbcurl "jdbc:h2:./test" -a:jdbcuser sa -a:jdbcpwd "" -a:jdbcdriver "org.h2.Driver"
 */
import fr.gpmsi.StringTable
import static fr.gpmsi.StringUtils.isEmpty
import groovy.sql.Sql

def dblq(s) { s.replaceAll("'", "''") }

tableName = args.table
colmd = new StringTable("COLMD", new File(args.colmd))

sb = new StringBuilder()

sb << "CREATE TABLE $tableName (\n"
boolean first = true
colmd.each {coldef ->
    def colName = coldef.col_sql
    def colType = coldef.type_sql
    if (isEmpty(colType)) return //ignorer les colonnes sans type SQL déclaré
    def len = coldef.longueur_sql ? coldef.longueur_sql as int : null
    def scale = coldef.echelle_sql ? coldef.echelle_sql as int : null
    def typeArgs = ""
    if (scale != null) {
        if (len == null) len = 0
        typeArgs = "($len, $scale)"
    }
    else if (len != null) {
        typeArgs = "($len)"
    }
    def constr1 = coldef.contrainte1_sql
    def constr2 = coldef.contrainte1_sql
    if (first) first = false
    else sb << ",\n" //finir ligne précédente
    sb << "  $colName $constr1 $colType$typeArgs $constr2 /* $coldef.col_excel */"
}
sb << "\n)"
println sb

//maintenant si le jdbc url existe, se connecter à la base et créer la table
if (args.jdbcurl != null) {
    println "Creation de la table $tableName ..."
    def sqlConn = Sql.newInstance(args.jdbcurl, args.jdbcuser, args.jdbcpwd, args.jdbcdriver)
    sqlConn.execute sb.toString()
    //si c'est une table H2, ajouter en plus les commentaires pour chaque
    //colonne
    if (args.jdbcdriver == 'org.h2.Driver') {
        println "Ajout des commentaires H2"
        colmd.each {coldef ->
            def colType = coldef.type_sql
            if (isEmpty(colType)) return //ignorer les colonnes sans type SQL déclaré
            sb = new StringBuilder()
            def colName = coldef.col_sql
            sb << "COMMENT ON COLUMN ${tableName}.${colName} IS '"
            def colExcelName = coldef.col_excel
            if (!isEmpty(colExcelName) && colExcelName != '*') sb << dblq(colExcelName)
            def remarks = coldef.remarques
            if (!isEmpty(remarks)) {
                if (!isEmpty(colExcelName) && colExcelName != '*') sb << ", "
                sb << dblq(remarks)
            }
            sb << "'"
            println "$sb"
            sqlConn.execute sb.toString()
        }//colmd.each
    }
}
