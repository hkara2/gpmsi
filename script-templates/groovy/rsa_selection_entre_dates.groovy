/**☺:encoding=utf-8:
 * Sélection de RSAs entre dates.
 * 
 * arguments :
 * -a:datemin AAAAMM
 * -a:datemax AAAAMM
 *
 * Exemple d'exécution :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2021\M12\RSA
 * %GPMSI_BASE%\v@PROJECT_VERSION@\gpmsi -script %GPMSI_BASE%\v@PROJECT_VERSION@\scripts\groovy\rsa_selection_entre_dates.groovy -a:input 910019447.2025.12.rsa -a:datemin 202508 -a:datemax 202512 -a:output 910019447.2025.12_sel_08_12.rsa
 * #260217 hk Création du script
 */

import fr.gpmsi.CsvDestination

datemin = args.datemin
datemax = args.datemax
anneemin = datemin[0..3] as int
anneemax = datemax[0..3] as int
moismin = datemin[4..5] as int
moismax = datemax[4..5] as int

rsa {
    name 'Selection de RSAs entre dates'

    input args['input']
    output args['output']

    onInit {
        //Créer le fichier csv avec un encodage Windows comme les csv produits par Excel
        outf = new FileWriter(outputFilePath)
    }

    onItem {item->
        def rsa = item.rsa
        def asor = rsa.ASOR.toInt()
        def msor = rsa.MSOR.toInt()
        if (anneemin <= asor && moismin <= msor && asor <= anneemax && msor <= moismax) {
            //emettre la ligne de RSA
            outf.write(item.line + "\r\n")
        }
    }//onItem

    onEnd {
        //clôturer le fichier
        outf.close()
    }
}
