/**☺:encoding=UTF-8:
 * Recherche des RSAs qui concernent de la Dermato Oncologique critères A7 INCA 2023
 * https://www.e-cancer.fr/Professionnels-de-sante/L-organisation-de-l-offre-de-soins/Traitements-du-cancer-les-etablissements-autorises/Les-autorisations-de-traitement-du-cancer
 * https://www.e-cancer.fr/content/download/470703/7131043/file/MESURE%20DES%20ACTIVITES%20CANCER%20SOUMISES%20A%20SEUILS_2023.pdf
 *
 * Ex :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2022\M10\RSA
 * c:\app\gpmsi\v1.3\gpmsi -script c:\app\gpmsi\v1.3\scripts\groovy\regles\rsa_chir_digestive_onco_crit2023.groovy -a:input 910019447.2022.10.rsa -a:output NRSAs_chir_dig_onco.txt
 */
package regles
import fr.gpmsi.DateUtils
import fr.gpmsi.StringTable
import fr.karadimas.pmsixml.MonoLevelReader
import fr.gpmsi.pmsi_rules.*
import fr.gpmsi.pmsi_rules.cim.*
import fr.gpmsi.pmsi_rules.ccam.*
import fr.gpmsi.pmsi_rules.ghm.*
import fr.gpmsi.pmsi_rules.rss.*

/*
 * Les critères sont :
 Dermatologique ;
Mélanome malin et autres tumeurs malignes de la peau
        C43* / C44*
        Tumeur de sièges autres et non précisés à évolution imprévisible ou inconnue
        D48.5
        Sarcome de Kaposi de la peau
        C46.0
        Mélanome in situ du cuir chevelu et du cou
        D03.4
        Mélanome in situ de la paupière, y compris le canthus
        D03.1
        Carcinome in situ de la peau
        D04*

"C43*","C44*","D485","C460","D034","D031 ","D04*"

 */



/** Critère diagnostic principal du RSA est un des DP de la liste */
estDpCimDermato = new CimCodePresence('DPA',
    ["C43.*","C44.*","D485","C460","D034","D031","D04.*"]
) //est-ce que le diag est present dans le DP du RSA ?

regleCanceroDermato = new PmsiCriterionRule(estDpCimDermato)

eng = new PmsiRuleEngine()
eng.add(regleCanceroDermato)

tra = null

//si il y a un argument "tracsv" lire les TRA au format csv
if (args.containsKey("tracsv")) {
    tra = new StringTable("TRA")
    //lire le TRA dans la StringTable
    tra.readFrom(new File(args.tracsv), ["nrsa", "nrss", "nadl", "ddsej", "dfsej", "ghm", "hash_tra"] as String[], "ISO-8859-1", ';' as char)
}

//si il y a un argument "tra" (fichiers TRA issus de GENRSA, avant DRUIDES en mars 2023)
//lire les TRA au format TRA2016 et les convertir aux nouveaux noms
if (args.containsKey("tra")) {
    def traFile = new File(args.tra)
    //créer une StringTable intermédiaire pour lire le contenu du TRA
    def traTbl = new StringTable('TRA')
    MonoLevelReader trar = new MonoLevelReader()
    trar.setMetaName("tra2016");
    //La StringTable sait s'auto-alimenter à partir d'un fichier à champs fixes en passant sa définition !
    traTbl.readFrom(traFile, trar.getOrLoadMeta().getChildMetas().get(0)) //pas tres elegant, à améliorer
    traTbl.addIndex('NRSA') //ajout d'un index pour accélérer la recherche par numéro de RSA
    colNames = traTbl.columnNames
    //println "Noms col TRA : $colNames"
    //NRSA, NRSS, NRUM, NDOSS, DENTR, CMD, DSOR
    tra = new StringTable("TRA")
    tra.declareColumnNames(["nrsa", "nrss", "nadl", "ddsej", "dfsej", "ghm", "hash_tra"])
    traTbl.each {row->
        destRow = []
        destRow << row['NRSA'] << row['NRSS'] << row['NDOSS'] << row['DENTR'] << row['DSOR'] << row['CMD'] << '??????????????????????????????'
        tra.addRow destRow
    }
}

if (tra != null) {
    //enlever les espaces de début et fin des nadl(s)
    tra.transform('nadl') {s-> s?.trim()}
    //idem pour les nrss
    tra.transform('nrss') {s-> s?.trim()}

    //ajouter un index sur le nrsa pour retrouver plus vite les nadl
    tra.addIndex('nrsa')
}

rsa {
    input args.input
    output args.output

    onInit {
        outf = new FileWriter(outputFilePath)
        outf << 'NRSA;NADL\r\n'
    }

    onItem {item->
        rsa = item.rsa
        int n = eng.evalRsa(rsa)
        def nrsa = rsa.txtNRSA
        def nadl = ''
        if (tra != null) nadl = tra.find('nrsa', rsa.txtNRSA, 'nadl') //recuperer le numero de dossier grace a la table des tra
        if (n > 0) outf << "$nrsa;${nadl}\r\n"
    }

    onEnd {
        outf.close()
    }
}

