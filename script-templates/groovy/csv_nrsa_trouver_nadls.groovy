// :encoding=utf-8:
/**
 * A partir d'un fichier csv qui a une colonne NRSA et d'un fichier TRA associé,
 * extraire une liste des numéros de RSA avec leur numéro NADL correspondant.
 *
 * arguments :
 * -a:input chemin_du_fichier_csv_en_entree
 * -a:tra chemin_du_fichier_tra
 * -a:output chemin_du_fichier_csv_en_sortie
 *
 * Exemple d'exécution :
 * C:\Local\e-pmsi\fichiers-rss-mco\2021\M12\RSA
 * c:\app\gpmsi\exec.bat -script c:\app\gpmsi\v1.0\scripts\groovy\csv_nrsa_trouver_nadls.groovy -a:input NRSAs_chir_dig_onco.txt -a:tra 910019447.2021.12.tra.txt -a:output NRSAs_chir_dig_onco_avec_NADLs.csv
 *
 * #230112 hk Création du fichier exemple
 */

import fr.karadimas.gpmsi.CsvDestination

//table des NADL par NSRA
nadlParNrsa = [:]

//parcourir le fichier des TRA pour remplir la table des NADLs par NRSA
mono {
    name ''
    metaName 'tra2016'
    input args['tra']
    onItem {item->
        def mono = item.mono
        def nrsa = mono.txtNRSA
        def nadl = mono.txtNDOSS //dans le TRA c'est NDOSS pas NADL
        nadlParNrsa[nrsa] = nadl
    }
}

csv {
    name 'Traitement du fichier CSV d entrée'

    input args['input']
    output args['output']

    onInit {
        //Créer le fichier csv avec un encodage Windows comme les csv produits par Excel
        outp = new CsvDestination(new File(outputFilePath), "cp1252")
        outp.f 'NRSA'
        outp.f 'NADL'
        outp.endRow()
    }

    onItem {item->
        if (item.linenr == 1) return //ne pas traiter la ligne 1 (qui contient les titres)
        def row = item.row
        def nrsa = row.NRSA
        //trouver le nadl grâce à la table
        def nadl = nadlParNrsa[nrsa]
        //Envoyer le nrsa dans le premier champ
        outp.f nrsa
        //envoyer le nadl dans le deuxième champ
        outp.f nadl
        outp.endRow()
    }//onItem

    onEnd {
        //clôturer le fichier csv
        outp.close()
    }
}
