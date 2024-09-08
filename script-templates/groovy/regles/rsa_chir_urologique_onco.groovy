/**:encoding=UTF-8:
 * Recherche des RSAs qui concernent de la chirurgie Urologique critères INCA 2011
 * Ex :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2022\M10\RSA
 * c:\app\gpmsi\exec -script c:\app\gpmsi\v1.0\scripts\groovy\regles\rsa_chir_urologique_onco.groovy -a:input 910019447.2022.10.rsa -a:output NRSAs_chir_uro_onco.txt
 */
package regles
import fr.gpmsi.DateUtils
import fr.gpmsi.pmsi_rules.*
import fr.gpmsi.pmsi_rules.cim.*
import fr.gpmsi.pmsi_rules.ghm.*
import fr.gpmsi.pmsi_rules.rss.*

ghmC = new GhmRsaCodePresence('..C...')

// GHMs à exclure :
// 11C05J Interventions transurétrales ou par voie transcutanée, en ambulatoire
// 11C051 Interventions transurétrales ou par voie transcutanée, niveau 1
// 11C052 Interventions transurétrales ou par voie transcutanée, niveau 2
// 11C053 Interventions transurétrales ou par voie transcutanée, niveau 3
// 11C054 Interventions transurétrales ou par voie transcutanée, niveau 4
// 12C041 Prostatectomies transurétrales, niveau 1
// 12C042 Prostatectomies transurétrales, niveau 2
// 12C043 Prostatectomies transurétrales, niveau 3
// 12C044 Prostatectomies transurétrales, niveau 4

ghmInSitu = new GhmRsaCodePresence('11C05J','11C051','11C052','11C053','11C054','12C041','12C042','12C043','12C044')

ghmNonInSitu = new PmsiNotCriterion(ghmInSitu)

age18 = new GenericPmsiCriterion( {context->
    def rsa = context.rsa
    def agea = rsa.AGEA.toInt()
    //println "age:$age"
    agea >= 18
})

//Codes INCA critères chir uro 2011 ("C:\hkchse\pmsi\cancero-seuils\docs\FICHE_Actualisation_Methodologie_ Calcul_seuils_Mai_2011.pdf")
cimChirUro =
("C480,C600,C601,C602,C608,C609,C61,C620,C621,C629,C630,C631,C632,C637,C638,"+
"C64,C65,C66,C670,C671,C672,C673,C674,C675,C676,C677,C678,C679,C680,C681,"+
"C688,C740,C741,C749,C790,C791,C797,D074,D075,D090,D400,D401,D407,D409,D410,"+
"D411,D412,D413,D414,D417,D441,D483,D4838").split(',')

//estCimChirUro = new CimCodePresence('DP', cimChirUro) //attention ne marche pas sur RUMS/RSS ; il faut le DP du SEJOUR !!
estCimChirUro = new CimCodePresence('DPA', cimChirUro) //est-ce que le diag est present dans le DP du RSA ?

criteres = new PmsiAllCriteria()
criteres << estCimChirUro << age18 << ghmC << ghmNonInSitu

regleCanceroChirUro = new PmsiCriterionRule(criteres)

eng = new PmsiRuleEngine()
eng.add(regleCanceroChirUro)

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

