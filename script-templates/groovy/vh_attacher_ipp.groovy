/** :encoding=UTF-8:
 * A partir d'un fichier qui contient des numeros de dossier NADL, ecrire le numero
 * IPP correspondant à l'aide du fichier VIDHOSP fourni.
 * Paramètres d'entrée :
 * -a:input_csv CHEMIN_FICHIER    Un fichier csv avec en-tetes qui contient une colonne NADL
 * -a:input_vh  CHEMIN_FICHIER    Le fichier VIDHOSP
 * -a:output    CHEMIN_FICHIER    Le fichier de sortie avec la colonne IPP ajoutée à la fin
 * Ex :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2022\M10\RSA
 * c:\app\gpmsi\exec -script c:\app\gpmsi\v1.0\scripts\groovy\vh_attacher_ipp.groovy -a:input_csv NRSAs_chir_uro_avec_nadl.csv -a:input_vh ..\RSS+VH\VIDHOSP_MCO.txt -a:output NRSAs_chir_uro_avec_nadl_ipp.csv
 *
 */
import fr.gpmsi.CsvDestination

ippParNadl = [:]

csvOutput = null

//lire le fichier VIDHOSP pour remplir la table ippParNadl
vidhosp {
    input      args.input_vh  //fichier d'entrée est donné dans le paramètre input_vh

    onItem {item->
        def vh = item.vidhosp
        ippParNadl[vh.txtNADL] = vh.txtIPP
    }
}


csv {
    input  args.input_csv
    output args.output

    onInit {
        csvOutput = new CsvDestination(new File(outputFilePath), "cp1252")
    }

    onItem {item->
        def row = item.row
        if (item.linenr == 1) {
            //envoyer les en-tetes
            csvHeaderRow.each { csvOutput.f it }
            csvOutput.f 'IPP' //ajouter la colonne IPP
            csvOutput.endRow()
            return //et sortir de cette closure
        }
        def nadl = row.NADL ?: ''
        def ipp = ippParNadl[nadl.trim()] ?: '?' //le NADL ou bien '?' si non trouvé
        //ecrire la rangee
        row.values.each { csvOutput.f it }
        csvOutput.f ipp //rajouter le ipp à la fin
        csvOutput.endRow() //finir la rangée
    }

    onEnd {
        csvOutput.close()
    }
}
