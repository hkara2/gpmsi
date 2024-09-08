/**☺:encoding=UTF-8:
 * Recherche des RSAs qui concernent de la chirurgie ORL critères INCA 2011
 * Ex :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2022\M10\RSA
 * c:\app\gpmsi\exec -script c:\app\gpmsi\v1.0\scripts\groovy\regles\rsa_chir_orl_onco.groovy -a:input 910019447.2022.10.rsa -a:output NRSAs_chir_orl_onco.txt
 */
package regles
import fr.gpmsi.DateUtils
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

//Codes INCA critères chir uro 2011 ("C:\hkchse\pmsi\cancero-seuils\docs\FICHE_Actualisation_Methodologie_ Calcul_seuils_Mai_2011.pdf")
cimChirOrl =
("C000, C001, C002, C003, C004, C005, C006, C008, C009, C01, C020, C021, C022,"+
"C023, C024, C028, C029, C030, C031, C039, C040, C041, C048, C049, C050, C051,"+
"C052, C058, C059, C060, C061, C062, C068, C069, C07, C080, C081, C088, C089,"+
"C090, C091, C098, C099, C100, C101, C102, C103, C104, C108, C109, C110, C111,"+
"C112, C113, C118, C119, C12, C130, C131, C132, C138, C139, C140, C142, C148,"+
"C300, C301, C310, C311, C312, C313, C318, C319, C320, C321, C322, C323, C328,"+
"C329, C462, C4670, C770, D000, D020, D370, D380, C73, C750, D093, D440, D442"
).split(',')

estCimChirOrl = new CimCodePresence('DPA', cimChirOrl) //est-ce que le diag est present dans le DP du RSA ?

criteres = new PmsiAllCriteria()
criteres << estCimChirOrl << age18 << ghmC

regleCanceroChirOrl = new PmsiCriterionRule(criteres)

eng = new PmsiRuleEngine()
eng.add(regleCanceroChirOrl)

nrsas = [] as Set

rsa {
    input args.input
    output args.output

    onInit {
        outf = new FileWriter(outputFilePath)
        outf << 'NRSA\r\n'
    }

    onItem {item->
        rsa = item.rsa
        int n = eng.evalRsa(rsa)
        def nrsa = rsa.txtNRSA
        if (n > 0) nrsas << (nrsa)
    }

    onEnd {
        outf << nrsas.join('\r\n')
        outf.close()
    }
}

