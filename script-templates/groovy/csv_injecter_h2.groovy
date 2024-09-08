/**:encoding=UTF-8:
 * Injecter un fichier .csv dans une table d'une base H2
 * C'est en fait un fichier exemple à adapter.
 * Ne rien changer dans ce fichier car il est aussi utilisé pour vérifier que
 * les scripts d'injection dans H2 fonctionnent bien.
 * Faire une copie de ce fichier et adapter la copie.
 *
 * Ici on injecte dans une table TABLEFICTIVE qui possède les champs suivants :
 * CREATE TABLE TABLEFICTIVE (
 * ID_TABLEFICTIVE IDENTITY PRIMARY KEY, -- clé primaire générée automatiquement lors de l'INSERT
 * C_TEXTE VARCHAR(64), -- exemple de champ texte
 * C_DATE DATE,  -- exemple de champ date
 * C_TIMESTAMP DATETIME,  -- exemple de champ DATETIME pouvant contenir un Timestamp
 * C_NB_ENTIER INTEGER,  -- exemple de champ contenant un nombre entier
 * C_NB_DEC NUMERIC(12,3) -- exemple de nombre décimal
 * )
 *
 * La table doit exister dans la base avant l'appel à ce script.
 *
 * Exemple :
 * cd C:\hkgh\gpmsi
 * c:\app\gpmsi\v@PROJECT_VERSION@\scripts\gpmsi.bat ^
 *    -script csv_injecter_h2.groovy ^
 *    -a:jdbcurl jdbc:h2:./test-files/tmp-out/csv_injecter_h2
 *    -a:jdbcuser sa ^
 *    -a:jdbcpwd "" ^
 *    -a:input test-files/in/TABLEFICTIVE.csv
 *
 * Arguments :
 * a:jdbcurl        url pour se connecter à la base de données
 * a:jdbcuser       user pour se connecter à la base de données
 * a:jdbcpwd        mot de passe pour se connecter à la base de données
 * a:input          fichier nip;ndoss à injecter
 *
 * Cette injection commence par effacer la table
 *
 * URL pour voir le résultat :
 * jdbc:h2:C:/hkgh/gpmsi/test-files/tmp-out/csv_injecter_h2
 */
import groovy.sql.Sql
import fr.karadimas.pmsixml.MonoLevelReader
import fr.gpmsi.StringTable
import fr.karadimas.pmsixml.FszField
import fr.gpmsi.StringTransformable
import fr.gpmsi.Chrono
import java.sql.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.LocalDateTime
import static fr.gpmsi.StringUtils.isEmpty

//nom de la table, changer pour un autre nom ici
tableName = 'TABLEFICTIVE'

//formats de nombre US et Français
us_nf = NumberFormat.getInstance(Locale.US)
us_nf.parseBigDecimal = true
fr_nf = NumberFormat.getInstance(Locale.FRENCH)
fr_nf.parseBigDecimal = true

//formats de date iso et francais
formatDateIso = DateTimeFormatter.ofPattern('yyyy-MM-dd')
formatDateFr = DateTimeFormatter.ofPattern('dd/MM/yyyy')
formatDateTimeFr = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm:ss')

//ne marche pas ?
def printErr = System.err.&println

//contexte d'erreur pour avoir la ligne courante
errContext = ""

//chrono pour mesurer le temps total
lip = new Chrono()

csv {
  name "Injection d un fichier de $tableName dans la base"
  input args.input //si l'encodage n'est pas "windows-1252", le rajouter ici, ex : input args.input, "UTF-8"
  // setCsvSeparator '\t' as char // décommenter cette ligne si le séparateur tabulation est utilisé au lieu de ';'
  
  onInit {
    def db = [driver:'org.h2.Driver',
              url: args.jdbcurl, user: args.jdbcuser, password: args.jdbcpwd]
    gsql = Sql.newInstance(db.url, db.user, db.password, db.driver)
    gsql.connection.autoCommit = false
    injectedCount = 0
    displayTime = 0
    now = new Timestamp(System.currentTimeMillis())
    //supprimer les deux lignes suivantes si on ne veut pas vider la table à chaque fois
    println("Vidage de la table $tableName .")
    gsql.execute ("DELETE FROM $tableName" as String) //il faut mettre "as String", car l'interpolation de noms est inhibée dans gsql par sécurité
    println("Demarrage de l'import de $tableName .")
  }

  onItem {item->
    if (item.linenr == 1) return //ignorer l'en tete
    if (item.row.empty) return //ignorer lignes vides
    def row = item.row
    //ici on traite champ par champ. C'est fastidieux, mais ainsi on a tout le
    //contrôle, sur un format particulier, ou bien si le champ est vide de
    //mettre null, ou 0, ou autre chose
    def c_texte = row.C_TEXTE //N.B. dans H2 une String vide n'est pas stocké en tant que null.
    def c_date = isEmpty(row.C_DATE) ? null : LocalDate.parse(row.C_DATE, formatDateFr)
    def c_timestamp = isEmpty(row.C_TIMESTAMP) ? null : LocalDateTime.parse(row.C_TIMESTAMP, formatDateTimeFr)
    def c_nb_entier = isEmpty(row.C_NB_ENTIER) ? null : fr_nf.parse(row.C_NB_ENTIER.trim())
    def c_nb_dec = isEmpty(row.C_NB_DEC) ? null : fr_nf.parse(row.C_NB_DEC.trim())

    def vals =                            [c_texte,c_date,c_timestamp,c_nb_entier,c_nb_dec]
    def qms = "?"+",?"*(vals.size()-1)
    gsql.execute """INSERT INTO $tableName(C_TEXTE,C_DATE,C_TIMESTAMP,C_NB_ENTIER,C_NB_DEC) VALUES ($qms)""", vals
    gsql.commit()
    injectedCount++
    def dt = System.currentTimeMillis()
    if (dt - displayTime > 1000) {
        print "\r$injectedCount enregistrements injectes                               "
        displayTime = dt
    }
  }

  onEnd {
    def dur = lip.elapsed() / 1000
    println("                                            ")
    println "Injection de $tableName terminee. $injectedCount enregistrements injectes en $dur secondes."
    gsql.close()
  }
}

