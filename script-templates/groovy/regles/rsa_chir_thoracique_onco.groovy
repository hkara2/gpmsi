/**☺:encoding=UTF-8:
 * Recherche des RSAs qui concernent de la chirurgie Thoracique critères INCA 2011
 * Ex :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2022\M10\RSA
 * c:\app\gpmsi\exec -script c:\app\gpmsi\v1.0\scripts\groovy\regles\rss_chir_thoracique_onco.groovy -a:input MCO_RSSG_20221207214447.txt -a:output NDAs_chir_tho_onco.txt
 */
package regles
import fr.karadimas.gpmsi.DateUtils
import fr.karadimas.gpmsi.pmsi_rules.*
import fr.karadimas.gpmsi.pmsi_rules.cim.*
import fr.karadimas.gpmsi.pmsi_rules.ghm.*
import fr.karadimas.gpmsi.pmsi_rules.rss.*

ghmC = new GhmRsaCodePresence('..C...')

age18 = new GenericPmsiCriterion( {context->
    def rsa = context.rsa
    def agea = rsa.AGEA.toInt()
    //println "age:$age"
    agea >= 18
})

//Codes INCA critères chir thoracique 2011 ("C:\hkchse\pmsi\cancero-seuils\docs\FICHE_Actualisation_Methodologie_ Calcul_seuils_Mai_2011.pdf")
cimChirTho =
("C150,C151,C152,C153,C154,C155,C158,C159,C33,C340,C341,C342,C343,C348,C349,"+
"C37,C380,C381,C382,C383,C384,C388,C398,C450,C452,C4672,C771,C780,C781,C782,"+
"C783,D001,D021,D022,D023,D381,D382,D383,D384,D385").split(',')

estCimChirTho = new CimCodePresence('DPA', cimChirTho) //est-ce que le diag est present dans le DP du RSA ?

criteres = new PmsiAllCriteria()
criteres << estCimChirTho << age18 << ghmC

regleCanceroChirTho = new PmsiCriterionRule(criteres)

eng = new PmsiRuleEngine()
eng.add(regleCanceroChirTho)

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

