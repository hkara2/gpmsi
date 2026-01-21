/**:encoding=UTF-8:
 * Recherche des séjours qui ont un CCAM parmis les CCAM recherchés.
 *
 * Ex :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2024\M12\RSS+VH\INOUT
 * %GPMSI_BASE%\v2.1\gpmsi -extracp file:/C:/hkgh/gpmsi/script-templates/groovy -scripturi regles/selection_cures_incontinence_urinaire_femme_2026.groovy -a:input 910019447.2024.12.rsa -a:output rsas-cures-incontinence-u.csv
 */
package regles
import java.time.LocalDate
import fr.gpmsi.DateUtils
import fr.gpmsi.StringTable
import fr.gpmsi.pmsi_rules.*
import fr.gpmsi.pmsi_rules.cim.*
import fr.gpmsi.pmsi_rules.ccam.*
import fr.gpmsi.pmsi_rules.ghm.*
import fr.gpmsi.pmsi_rules.rss.*

/* Actes de pose de dispositif pour cure d'incontinence urinaire d'effort
 *
 * https://www.legifrance.gouv.fr/jorf/id/JORFTEXT000051533063
 */

ccamRech = [ 'JDFA004', 'JDDC002', 'JDDA002', 'JDDA003', 'JDDA007', 'JDDA006',
'JDDB005', 'JDDB007', 'JDLE332', 'JELA001', 'JELE001', 'AHLB018' ]

/** Critère le RUM contient au moins un des codes CCAM de la liste */
estCcamRech = new CcamCodePresence(ccamRech)

regleCcamRech = new PmsiCriterionRule(estCcamRech)

eng = new PmsiRuleEngine(regleCcamRech) //moteur de règles basé sur cette règle

tra = null

traPath = args.input.replace('.rsa', '.tra.txt')
tra = new StringTable("TRA")
//lire le TRA dans la StringTable
tra.readFrom(new File(traPath), ["nrsa", "nrss", "nadl", "ddsej", "dfsej", "ghm", "hash_tra"] as String[], "ISO-8859-1", ';' as char)

nrsas = [] as Set

rsa {
    input args.input
    output args.output

    onInit {
        outf = new FileWriter(outputFilePath)
        outf << 'NRSA;NDA\r\n'
    }

    onItem {item->
        rsa = item.rsa
        int n = eng.evalRsa(rsa)
        def nrsa = rsa.txtNRSA
        def nadl = tra.find('nrsa', nrsa, 'nadl')
        if (n > 0) nrsas << (nrsa + ';' + nadl?.trim())
    }

    onEnd {
        outf << nrsas.join('\r\n')
        outf.close()
    }
} //rsa

