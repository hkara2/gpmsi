/**☺:encoding=UTF-8:
 * Recherche des RSAs qui concernent un code d'hémorragie de la délivrance (O720)
 *
 * Ex :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2022\M12\RSA
 * c:\app\gpmsi\v1.3\gpmsi.bat -script c:\app\gpmsi\v1.3\scripts\groovy\regles\rsa_obstetrique_hemorragie_delivrance.groovy -a:input 910019447.2022.12.rsa -a:tra 910019447.2022.12.tra.txt -a:output NRSAs_hemorragies_delivrance.txt
 */
package regles
import fr.gpmsi.pmsixml.MonoLevelReader
import fr.gpmsi.DateUtils
import fr.gpmsi.StringTable
import fr.gpmsi.pmsi_rules.*
import fr.gpmsi.pmsi_rules.cim.*
import fr.gpmsi.pmsi_rules.ccam.*
import fr.gpmsi.pmsi_rules.ghm.*
import fr.gpmsi.pmsi_rules.rss.*

/*
 * Les critères sont :
 * diagnostic principal accouchement voie basse ou accouchement par césarienne
 *
 */

//Codes accouchement sans complication
CIM10_hemorragie_delivrance = ("O720").tokenize(' \n')

/** Critère diagnostic principal ou diagnostic associé d'un RUM du RSA est un de la liste */
estHemorragieDelivrance = new CimCodePresence('RADP,RADAS', CIM10_hemorragie_delivrance) //est-ce que le diag est present dans le DP ou un des DAS d'un des RUMs du RSA ?

criteres = new PmsiAllCriteria()
criteres << estHemorragieDelivrance

regleDetectionHd = new PmsiCriterionRule(criteres)

eng = new PmsiRuleEngine()
eng.add(regleDetectionHd)

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
    //créer une StringTable pour lire le contenu du TRA
    def traTbl = new StringTable('TRA')
    MonoLevelReader trar = new MonoLevelReader()
    trar.setMetaName("tra2016");
    //La StringTable sait s'auto-alimenter à partir d'un fichier à champs fixes en passant sa définition !
    traTbl.readFrom(traFile, trar.getOrLoadMeta().getChildMetas().get(0)) //pas tres elegant, à améliorer
    traTbl.addIndex('NRSA') //ajout d'un index pour accélérer la recherche par numéro de RSA
    colNames = traTbl.columnNames
    println "Noms col TRA : $colNames"
    //NRSA, NRSS, NRUM, NDOSS, DENTR, CMD, DSOR
    tra = new StringTable("TRA")
    tra.declareColumnNames(["nrsa", "nrss", "nadl", "ddsej", "dfsej", "ghm", "hash_tra"])
    traTbl.each {row->
        destRow = []
        destRow << row['NRSA'] << row['NRSS'] << row['NDOSS'] << row['DENTR'] << row['DSOR'] << row['CMD'] << '??????????????????????????????'
        tra.addRow destRow
    }
}

//enlever les espaces de début et fin des nadl(s)
tra.transform('nadl') {s-> s?.trim()}
//idem pour les nrss
tra.transform('nrss') {s-> s?.trim()}

//ajouter un index sur le nrsa pour retrouver plus vite les nadl
tra.addIndex('nrsa')

//sb = new StringBuffer()
//tra.prettyPrintTo(sb)
//println "$sb"

rsa {
    input args.input
    output args.output

    onInit {
        outf = new FileWriter(outputFilePath) //un simple FileWriter suffit
        outf << 'NRSA;NDOSSIER\r\n'
    }

    onItem {item->
        rsa = item.rsa
        int n = eng.evalRsa(rsa) //evaluer ce RSA, n sera >0 si l'évaluation est positive
        def nrsa = rsa.txtNRSA
        def nadl = tra.find('nrsa', rsa.txtNRSA, 'nadl') //recuperer le numero de dossier grace a la table des tra
        if (n > 0) outf << "$nrsa;${nadl}\r\n"
    }

    onEnd {
        outf.close()
    }
}

