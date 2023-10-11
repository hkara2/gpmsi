/**☺:encoding=UTF-8:
 * Recherche des RSAs qui concernent de la chirurgie Oncologique critères INCA 2022
 * https://www.e-cancer.fr/Professionnels-de-sante/L-organisation-de-l-offre-de-soins/Traitements-du-cancer-les-etablissements-autorises/Les-autorisations-de-traitement-du-cancer
 * https://www.e-cancer.fr/content/download/470703/7131043/file/MESURE%20DES%20ACTIVITES%20CANCER%20SOUMISES%20A%20SEUILS_2022_Vdef.pdf
 *
 * Ex :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2022\M10\RSA
 * c:\app\gpmsi\exec -script c:\app\gpmsi\v1.0\scripts\groovy\regles\rsa_chir_digestive_onco_crit2017.groovy -a:input 910019447.2022.10.rsa -a:output NRSAs_chir_dig_onco.txt
 */
package regles
import fr.karadimas.gpmsi.DateUtils
import fr.karadimas.gpmsi.StringTable
import fr.karadimas.pmsixml.MonoLevelReader
import fr.karadimas.gpmsi.pmsi_rules.*
import fr.karadimas.gpmsi.pmsi_rules.cim.*
import fr.karadimas.gpmsi.pmsi_rules.ccam.*
import fr.karadimas.gpmsi.pmsi_rules.ghm.*
import fr.karadimas.gpmsi.pmsi_rules.rss.*

/*
 * Les critères sont :
 * Âge supérieur ou égal à 18 ans
 * ET diagnostic principal spécifique de la chirurgie concernée par le seuil
 * ET acte spécifique de la chirurgie concernée par le seuil
 *
 * Formule A1-B1 (hors organes)
 * ----------------------------
 * Âge supérieur ou égal à 18 ans
 * ET diagnostic principal de la liste « CIM10 tumeur maligne digestif » ou (diagnostic principal de métastase péritonéale SANS tumeur maligne gynécologique en DAS)
 * ET acte de la liste « CCAM Exérèse abdominopelvienne hors exérèse oesophage, foie, estomac, pancréas et rectum »
 */

/** Critère GHM en C (groupé en chir) */
ghmC = new GhmRsaCodePresence('..C...')

/** Critère âge est 18 ans et plus */
age18 = new GenericPmsiCriterion( {context->
    def rsa = context.rsa
    def agea = rsa.AGEA.toInt()
    //println "age:$age"
    agea >= 18
})

//Codes INCA critères chir dig 2022
CIM10_tumeur_maligne_digestif = ("""
C150 C151 C152 C153 C154 C155 C158 C159 C160 C161 C162 C163 C164 C165 C166 C168
C169 C169+0 C169+8 C170 C171 C172 C173 C178 C179 C180 C181 C182 C183 C184 C185
C186 C187 C188 C189 C189+0 C189+8 C19 C20 C210 C211 C212 C218 C220 C221 C222
C223 C224 C227 C229 C23 C240 C241 C248 C249 C250 C251 C252 C253 C254 C254+0
C254+8 C257 C258 C259 C259+0 C259+8 C261 C268 C451 C4671 C481 C482 C488 C784
C785 C786 C787 C788 D001 D002 D010 D011 D012 D013 D014 D015 D017 D371 D372 D373
D374 D375 D376 D377 D4830 D484
""").tokenize(' \n')

//Codes CCAM INCA
CCAM_exerese_abdominopelvienne = ("""
FCFA002 FCFA004 FCFA005 FCFA006 FCFA008 FCFA009 FCFA010 FCFA011 FCFA013 FCFA016
FCFA019 FCFA020 FCFA022 FCFA025 FCFA027 FCFA029 FCFC001 FCFC003 FCFC004 FCFC005
FFFA001 FFFA002 FFFC001 HEFA001 HEFA002 HEFA003 HEFA004 HEFA005 HEFA006 HEFA007
HEFA008 HEFA009 HEFA011 HEFA012 HEFA013 HEFA016 HEFA017 HEFA018 HEFA019 HEFA020
HEFA022 HEFC002 HEFC800 HEFC801 HFFA002 HFFA003 HFFA005 HFFA006 HFFA008 HFFA009
HFFC001 HFFC002 HFFC012 HFFC017 HFMA005 HGFA001 HGFA003 HGFA004 HGFA005 HGFA007
HGFA014 HGFC014 HGFC016 HGFC021 HHFA001 HHFA002 HHFA004 HHFA005 HHFA006 HHFA008
HHFA009 HHFA010 HHFA011 HHFA014 HHFA016 HHFA017 HHFA018 HHFA020 HHFA021 HHFA022
HHFA023 HHFA024 HHFA025 HHFA026 HHFA028 HHFA029 HHFA030 HHFA031 HHFC040 HHFC296
HJFA001 HJFA002 HJFA003 HJFA004 HJFA005 HJFA006 HJFA007 HJFA011 HJFA012 HJFA017
HJFA018 HJFA019 HJFC023 HJFC031 HJFD002 HKFA007 HLEA001 HLEA002 HLFA003 HLFA004
HLFA005 HLFA006 HLFA007 HLFA009 HLFA010 HLFA011 HLFA017 HLFA018 HLFA019 HLFA020
HLFC002 HLFC003 HLFC004 HLFC027 HLFC032 HLFC037 HLFC801 HMFA001 HMFA002 HMFA005
HMFA006 HMFA007 HMFA009 HMFA010 HMFC003 HMFC004 HMFC005 HNFA001 HNFA002 HNFA004
HNFA005 HNFA006 HNFA007 HNFA008 HNFA010 HNFA011 HNFA013 HNFC001 HNFC002 HNFC028
HPBA001 HPFA003 HPFA004 HPFC001 HPFC002 JAFA001 JAFA002 JAFA003 JAFA005 JAFA008
JAFA009 JAFA010 JAFA011 JAFA012 JAFA014 JAFA016 JAFA019 JAFA021 JAFA022 JAFA023
JAFA024 JAFA025 JAFA027 JAFA028 JAFA029 JAFA030 JAFA031 JAFA032 JAFC001 JAFC002
JAFC004 JAFC005 JAFC006 JAFC007 JAFC010 JAFC019 JBFA001 JBFA002 JBFC001 JCFA001
JCFA002 JCFA003 JCFA008 JCFA009 JCFA010 JCFC001 JCFD001 JCKA001 JCKA002 JCND001
JDFA001 JDFA002 JDFA003 JDFA004 JDFA005 JDFA006 JDFA008 JDFA009 JDFA011 JDFA014
JDFA016 JDFA017 JDFA019 JDFA020 JDFA021 JDFA022 JDFA023 JDFA024 JDFA025 JDFC001
JDFC023 JEFA004 JEFA007 JEFA008 JFFA001 JFFA002 JFFA003 JFFA004 JFFA005 JFFA006
JFFA008 JFFA009 JFFA010 JFFA011 JFFA013 JFFA015 JFFA016 JFFA017 JFFA018 JFFA019
JFFA020 JFFA021 JFFA022 JFFC002 JGFA006 JGFA011 JGFC001 JHFA005 JHFA006 JHFA007
JHFA008 JHFA011 JHFA015 JHFA016 JHFA018 JJFA002 JJFA003 JJFA004 JJFA005 JJFA050
JJFC004 JJFC008 JJFC009 JJFC010 JKFA005 JKFA006 JKFA008 JKFA009 JKFA011 JKFA015
JKFA018 JKFA019 JKFA020 JKFA023 JKFA026 JKFA027 JKFA028 JKFA030 JKFC003 JKFC005
JKFD002 JLFA002 JLFA003 JLFA004 JMFA003 JMFA004 JMFA005 JMFA007 JMFA008 JMFA009
JMFA010 KEFA001 KEFA002 KEFC001 KEFC002 KZFA001 KZFC001
""").tokenize(' \n') as Set

/** Critère diagnostic principal du RSA est un des DP de la liste */
estCimChirDig = new CimCodePresence('DPA', CIM10_tumeur_maligne_digestif) //est-ce que le diag est present dans le DP du RSA ?

/** Critère le RSA contient au moins un des codes CCAM de la liste */
estCcamExerese = new CcamCodePresence(CCAM_exerese_abdominopelvienne)

criteres = new PmsiAllCriteria()
//criteres << estCimChirDig << age18 << ghmC << estCcamExerese
criteres  << age18 << ghmC << estCimChirDig << estCcamExerese

regleCanceroChirDig = new PmsiCriterionRule(criteres)

eng = new PmsiRuleEngine()
eng.add(regleCanceroChirDig)

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

