/**:encoding=utf-8:
 *
 * RESTE A METTRE A JOUR LES SQL POUR REFLETER LES FORMATS 2023
 *
 * 240829 hk Dans CPAGE pas d'envoi de fichier qui change de format en cours
 *           d'année. (ex : dans un fichier de 2022, format 2021 pour jan-fev,
 *           format 2022 pour mar-dec) -> non supporté par ce script
 * 240904 hk Nouvelle version avec typages différents des colonnes ->
 *           changement des noms de table qui sont maintenant RSFA, RSFB, etc.
 *
 *
 * injecter dans une base de donnée H2 des données de RSF ACE
 * Les tables doivent avoir ete preparees, cf.
 * C:\hkchse\dev\chse-gpmsi\scripts\sql\tables-fsface.txt
 *
 * arguments :
 * -a:input <chemin>    Chemin du fichier RSF             obligatoire
 * -a:url <url jdbc>    URL pour se connecter la base H2  obligatoire
 * -a:user <user>       Utilisateur JDBC                  optionnel   (par défaut : "sa")
 * -a:pw <password>     Mot de passe JDBC                 optionnel   (par défaut : "")
 * -a:meta_hint <hint>  Format metadonnees rsface         optionnel   (par défaut : 2023)
 * Exemple :
 * C:\t>gpmsi -script C:\hkchse\dev\pmsixml\groovy-samples\rsface-vers-bdd.groovy -a:input RSF_corr1 -a:meta_hint 2017 -a:url jdbc:h2:tcp://localhost/C:/Local/h2db/rsface1 -a:user sa -a:pw ""
 * Item mais avec débogage :
 * C:\t>gpmsi -debug -script C:\hkchse\dev\pmsixml\groovy-samples\rsface-vers-bdd.groovy -a:input RSF_corr1 -a:meta_hint 2017 -a:url jdbc:h2:tcp://localhost/C:/Local/h2db/rsface1 -a:user sa -a:pw "" >errs.txt 2>&1
 * Vitesse d'import : 171 s pour 1013500 lignes soit 5927 lignes/s, 168µs par ligne
 *
 * Exemple avec envoi vers une base de donnees en mode fichier (par tcp/ip) :
 * gpmsi -script C:\hkchse\dev\chse-gpmsi\scripts\rsface-vers-bdd.groovy -a:input "C:\Local\e-pmsi\fichiers-rss-mco\2020\M01\RSF200310\RSF.txt" -a:meta_hint 2017 -a:url jdbc:h2://C:/t/rsft1 -a:user sa -a:pw ""
 *
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rsface_vers_h2.groovy -a:input ..\RSF.txt -a:url jdbc:h2:./rsface 
 */
import java.sql.DriverManager
import fr.karadimas.gpmsi.StringTable
import fr.karadimas.gpmsi.CsvDestination
import fr.karadimas.gpmsi.StringTransformable
import groovy.sql.Sql

ps = null //PreparedStatement
lettresRsf = ['a', 'b', 'c', 'h', 'l', 'm', 'p', ]
scanner = new Scanner(System.in)
scriptDir = new File(scriptPath).parent //scriptPath est fournir par Gpmsi
sqlDir = new File(scriptDir, "../sql/rsface") //chemin relatif des fichiers sql pour les rsface
linesCount = 0

rsface {
    name 'Envoi des rsf-ace vers une base de donnee'
    input args['input']
    metaHint args['meta_hint'] ?: "2023"

    onInit {
        url = args['url']
        user = args['user']
        if (user == null) user = "sa"
        pw = args['pw']
        if (pw == null) pw = ""
        cxn = DriverManager.getConnection(url, user, pw)
        gsql = Sql.newInstance(url, user, pw, 'org.h2.Driver')
        lettresRsf.each {letter->
            def tableName = 'RSF' + letter
            def r = gsql.firstRow('SELECT COUNT(*) AS count FROM information_schema.tables WHERE table_name = ?', [tableName.toUpperCase()])
            if (r['count'] == 0) {
                def sqlFile = new File(sqlDir, "create-table-rsface${letter}.sql")
                //def url = getClass().getResource("/sql/create-table-${tableName}.sql")
                def createTableScript = sqlFile.text
                println("Creation de la table $tableName")
                gsql.execute(createTableScript)
            }
            else {
                println("La table $tableName doit être vidée. Entrez Y + Entrée pour confirmer, ou N + Entrée pour interrompre ce script.")
                def ur = scanner.nextLine() //get user response
                if (ur.equalsIgnoreCase('Y')) {
                    //truncate table
                    gsql.execute('TRUNCATE TABLE ' + tableName)
                }
                else {
                    throw new Exception("Programme interrompu par l'utilisateur")
                }
            }
        }
    }

    onItem {item->
        def rsf = item.rsf
        def tableName = 'RSF' + rsf.txtTENR
        ps = rsf.makeInsertPs(cxn, tableName)
        rsf.fillInsertPs(ps)
        ps.executeUpdate()
        ps.close()
        //ps.addBatch()
        if (item.linenr % 100 == 0) {
            //ps.executeBatch()
            cxn.commit()
            print "\r           "
            print "\r${item.linenr}"
        }
        //println "name : $tableName"
        linesCount++
    }//onItem

    onEnd {
        print "\r           "
        println "$linesCount lignes RSFACE traitees."
        cxn.commit()
        cxn.close()
    }
}
