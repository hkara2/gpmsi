/**☺:encoding=UTF-8:
 * Recherche des RSAs qui concernent de la chirurgie digestive Oncologique critères INCA 2017
 * Mais avec critères élargis pour rechercher des diagnostics oncologiques qui
 * auraient été mal classés.
 * Arguments :
 * -a:input chemin_du_fichier_des_RSA
 * -a:output chemin_du_fichier_a_produire
 * -a:tra chemin du fichier TRA (optionnel, ne marche que pour les TRA 2023 (DRUIDES))
 * Ex :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2021\M12\RSA
 * c:\app\gpmsi\exec -script c:\app\gpmsi\v1.0\scripts\groovy\regles\rsa_chir_digestive_onco_crit2017_elargis.groovy -a:input 910019447.2021.12.rsa -a:output NRSAs_chir_dig_onco_criteres_elargis.csv
 */
package regles
import fr.gpmsi.DateUtils
import fr.gpmsi.StringTable
import fr.gpmsi.pmsi_rules.*
import fr.gpmsi.pmsi_rules.cim.*
import fr.gpmsi.pmsi_rules.ghm.*
import fr.gpmsi.pmsi_rules.rss.*

ghmC = new GhmRsaCodePresence('..C...')

age18 = new GenericPmsiCriterion( {context->
    def rsa = context.rsa
    def agea = rsa.AGEA.toInt()
    //println "age:$age"
    agea >= 18
})

//Codes INCA critères chir dig 2017 (https://www.e-cancer.fr/Professionnels-de-sante/L-organisation-de-l-offre-de-soins/Traitements-du-cancer-les-etablissements-autorises/Les-autorisations-de-traitement-du-cancer)
cimChirDig =
("C150,C151,C152,C153,C154,C155,C158,C159,C160,C161,C162,C163,C164,"+
"C165,C166,C168,C169,C169+0,C169+8,C170,C171,C172,C173,C178,C179,"+
"C180,C181,C182,C183,C184,C185,C186,C187,C188,C189,C189+0,C189+8,C19,"+
"C20,C210,C211,C212,C218,C220,C221,C222,C223,C224,C227,C229,C23,"+
"C240,C241,C248,C249,C250,C251,C252,C253,C254,C254+0,C254+8,C257,"+
"C258,C259,C259+0,C259+8,C261,C268,C451,C4671,C481,C482,C488,C784,"+
"C785,C786,C787,C788,D001,D002,D010,D011,D012,D013,D014,D015,D017,"+
"D371,D372,D373,D374,D375,D376,D377,D4830,D484").split(',')

nEstPasDpaCimChirDig = new PmsiNotCriterion(new CimCodePresence('DPA', cimChirDig)) //on ne veut pas de code critère INCA dans le DPA
//DRA,RADP,RADR,
estCimChirDig = new CimCodePresence('DRA,RADP,RADR,RADAS', cimChirDig) //est-ce que le diag est present quelque part dans RSA (sauf dans le DP du RSA) ?

criteres = new PmsiAllCriteria()
criteres << nEstPasDpaCimChirDig << estCimChirDig << age18 << ghmC // on peut aussi supprimer ghmC comme critère pour inspecter aussi des séjours qui n'auraient pas eu l'acte codé, mais le nombre de séjour à contrôler devient énorme

regleCanceroChirDig = new PmsiCriterionRule(criteres)

eng = new PmsiRuleEngine()
eng.add(regleCanceroChirDig)

results = [] as Set

tra = null

if (args.containsKey("tra")) {
    tra = new StringTable("TRA")
    //lire le TRA dans la StringTable
    tra.readFrom(new File(args.tra), ["nrsa", "nrss", "nadl", "ddsej", "dfsej", "ghm", "hash_tra"] as String[], "ISO-8859-1", ';' as char)
}

rsa {
    input args.input
    output args.output

    onInit {
        outf = new FileWriter(outputFilePath)
        outf << 'nrsa'
        if (tra != null) {
            outf << ';nadl'
        }
        outf << '\r\n'
    }

    onItem {item->
        rsa = item.rsa
        int n = eng.evalRsa(rsa)
        def nrsa = rsa.txtNRSA
        if (n > 0) {
            def nadl = tra.find('nrsa', nrsa, 'nadl')
            results << (nrsa + ';' + nadl)
        }
    }

    onEnd {
        outf << results.join('\r\n')
        outf.close()
    }
}

