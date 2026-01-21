/**:encoding=UTF-8:
 * Recherche des séjours qui ont un CCAM parmis les CCAM recherchés.
 *
 * Ex :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2024\M12\RSS+VH\INOUT
 * %GPMSI_BASE%\v2.1\gpmsi -extracp file:/C:/hkchse/dev/chse-gpmsi/scripts -scripturi pmsi_rules/selection_cures_prolapsus_2026.groovy -a:input 910019447.2024.12.rsa -a:output rsas-cures-prolapsus.csv
 */
package pmsi_rules
import java.time.LocalDate
import fr.gpmsi.DateUtils
import fr.gpmsi.StringTable
import fr.gpmsi.pmsi_rules.*
import fr.gpmsi.pmsi_rules.cim.*
import fr.gpmsi.pmsi_rules.ccam.*
import fr.gpmsi.pmsi_rules.ghm.*
import fr.gpmsi.pmsi_rules.rss.*

/* Actes de pose de dispositif pour cure de prolapsus pelvien, activité
 * soumise à seuil (25)
 * https://www.legifrance.gouv.fr/jorf/id/JORFTEXT000051533101
 */

ccamRech = [ 'JLCA005', 'JLCA003', 'JLSD001', 'JKFA002', 'JKFA007', 'JKFA021',
'HJDC001', 'HJDA001', 'HPFC007', 'HKCA005', 'HJBA001', 'JLDC015', 'JMBA001',
'JMDA001', 'JKDA042', 'JKDC001', 'JKDA003', 'JKDA002', 'JKDA001', 'JLDA002',
'JLDA001', 'JLDA004', 'JLDA003', 'JLCA004', 'JLCA009', 'JLCA007', 'JKDC015']

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

