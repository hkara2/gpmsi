// :encoding=utf-8:
/**
 * A partir d'un fichier csv qui a une colonne NADL et d'un fichier csv associé
 * qui a aussi une colonne NADL, ne garder que les enregistrements qui sont
 * dans la liste associée.
 *
 * arguments :
 * -a:input chemin_du_fichier_csv_en_entree
 * -a:nadls chemin_du_fichier_csv_des_nadls_pour_selectionner
 * -a:output chemin_du_fichier_csv_en_sortie
 *
 * Exemple d'exécution :
 *
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi.bat ^
   -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\csv_selectionner_sur_nadls.groovy ^
   -a:input monfichier.csv ^
   -a:nadls lesnadls.csv ^
   -a:output selection.csv
 *
 * #240805 creation
 */

import fr.gpmsi.CsvDestination
import fr.gpmsi.StringTable

nadlsFilePath = args['nadls']
if (nadlsFilePath == null) throw Exception('argument nadls obligatoire non trouve')

//table des NADL par NSRA
tableNadls = new StringTable('NADLS', new File(nadlsFilePath))

//parcourir le fichier des TRA pour remplir la table des NADLs par NRSA


csv {
    name 'Selection du fichier CSV d entrée par les NADLs du fichier des NADLs'

    input args['input']
    output args['output']

    onInit {
        //Créer le fichier csv avec un encodage Windows comme les csv produits par Excel
        outp = new CsvDestination(new File(outputFilePath), "cp1252")
    }

    onItem {item->
        row = item.row
        if (item.linenr == 1) {
            def titlesRow = row.row
            titlesRow.each {title -> outp.f title }
            outp.endRow()
            return //fin du traitement de la ligne 1 (qui contient les titres)
        }
        def nadl = row.NADL
        //regarder si le NADL est dans la table de sélection
        if (tableNadls.contains('NADL', nadl)) {
            def valuesRow = row.row
            valuesRow.each {value -> outp.f value }
            outp.endRow()
        }
    }//onItem

    onEnd {
        //clôturer le fichier csv de sortie
        outp.close()
    }

}
