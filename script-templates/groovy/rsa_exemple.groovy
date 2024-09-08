// :encoding=utf-8:
/**
 * Exemple de traitement d'un fichier RSA. Peut être utilisé comme gabarit
 * pour créer de nouveaux scripts
 * arguments :
 * -a:input chemin_du_fichier_rsa_en_entree
 * -a:output chemin_du_fichier_csv_en_sortie
 *
 * Exemple d'exécution :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2021\M12\RSA
 * c:\app\gpmsi\exec.bat -script c:\app\gpmsi\v1.0\scripts\groovy\rsa_exemple.groovy -a:input 910019447.2021.12.rsa -a:output 910019447.2021.12_rsa.csv
 *
 * #230112 hk Création du fichier exemple
 */

import fr.gpmsi.CsvDestination

rsa {
    name 'Exemple de traitement d un fichier RSA'

    input args['input']
    output args['output']

    onInit {
        //Créer le fichier csv avec un encodage Windows comme les csv produits par Excel
        outp = new CsvDestination(new File(outputFilePath), "cp1252")
        outp.f 'NRSA'
        outp.endRow()
    }

    onItem {item->
        def rsa = item.rsa
        //Envoyer le nrsa dans le premier (et unique) champ
        def nrsa = rsa.txtNRSA
        outp.f nrsa
        outp.endRow()
    }//onItem

    onEnd {
        //clôturer le fichier csv
        outp.close()
    }
}
