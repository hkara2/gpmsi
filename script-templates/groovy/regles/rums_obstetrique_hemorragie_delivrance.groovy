/**☺:encoding=UTF-8:
 * Recherche des RUMs qui concernent un code d'hémorragie de la délivrance (O720)
 *
 * Ex :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2022\M12\
 * c:\app\gpmsi\v1.3\gpmsi.bat -script c:\app\gpmsi\v1.3\scripts\groovy\regles\rums_obstetrique_hemorragie_delivrance.groovy -a:input 910019447.2022.12.rum -a:output NRUMs_hemorragies_delivrance.txt
 */
package regles
import fr.karadimas.pmsixml.MonoLevelReader
import fr.karadimas.gpmsi.DateUtils
import fr.karadimas.gpmsi.StringTable
import fr.karadimas.gpmsi.pmsi_rules.*
import fr.karadimas.gpmsi.pmsi_rules.cim.*
import fr.karadimas.gpmsi.pmsi_rules.ccam.*
import fr.karadimas.gpmsi.pmsi_rules.ghm.*
import fr.karadimas.gpmsi.pmsi_rules.rss.*

/*
 * Les critères sont :
 * diagnostic principal accouchement voie basse ou accouchement par césarienne
 *
 */

//Codes accouchement sans complication
CIM10_hemorragie_delivrance = ("O720").tokenize(' \n')

/** Critère diagnostic principal ou diagnostic associé d'un RUM du RUM est un de la liste */
estHemorragieDelivrance = new CimCodePresence('DP,DAS', CIM10_hemorragie_delivrance) //est-ce que le diag est present dans le DP ou un des DAS du RUM ?

criteres = new PmsiAllCriteria()
criteres << estHemorragieDelivrance

regleDetectionHd = new PmsiCriterionRule(criteres)

eng = new PmsiRuleEngine()
eng.add(regleDetectionHd)

//sb = new StringBuffer()
//tra.prettyPrintTo(sb)
//println "$sb"

rss {
    input args.input
    output args.output

    onInit {
        outf = new FileWriter(outputFilePath) //un simple FileWriter suffit
        outf << 'NRUM;NDOSSIER\r\n'
    }

    onItem {item->
        rum = item.rum
        int n = eng.evalRum(rum) //evaluer ce RUM, n sera >0 si l'évaluation est positive
        def nrum = rum.txtNRUM
        def nadl = rum.txtNADL
        if (n > 0) outf << "$nrum;${nadl}\r\n"
    }

    onEnd {
        outf.close()
    }
}

