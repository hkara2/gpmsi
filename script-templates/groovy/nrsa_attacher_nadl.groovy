/** :encoding=UTF-8:
 * A partir d'un fichier .csv qui contient des numeros de rsa dans une colonne NRSA,
 * (= une seule colonne dans le fichier, la première ligne doit contenir NRSA)
 * ecrire le meme fichier avec une colonne supplementaire NADL qui contient le
 * numero de dossier administratif qui correspond au NRSA, à
 * l'aide de la table TRA fournie.
 * Paramètres d'entrée :
 * -a:input_csv CHEMIN_FICHIER    Un fichier csv avec en-tetes qui contient une colonne NRSA
 * -a:input_tra CHEMIN_FICHIER    Le fichier tra qui fait correspondre numéros de RSA avec numéro administratif local NADL
 * -a:output CHEMIN_FICHIER       Le fichier de sortie avec la colonne NADL ajoutée à la fin
 * Ex :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2022\M10\RSA
 * c:\app\gpmsi\exec -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\nrsa_attacher_NADL.groovy -a:input_csv NRSAs_chir_uro.txt -a:input_tra 910019447.2022.10.tra.txt -a:output NRSAs_chir_uro_avec_nadl.csv
 *
 */
import fr.gpmsi.CsvDestination
import fr.gpmsi.StringTable

if (!args.containsKey('input_csv')) throw new Exception("Argument manquant input_csv")
if (!args.containsKey('input_tra')) throw new Exception("Argument manquant input_tra")
if (!args.containsKey('output')) throw new Exception("Argument manquant output")

//jusqu'a l'arrivee de druides le format TRA était tra2016
TRA_META = 'tra2016' //par défaut on utilise le format TRA de 2016 qui est encore valable en 2023

nadlParNrsa = [:]

csvOutput = null

traSt = new StringTable('TRA')
//signification des colonnes à revoir (hk mars 2023)
//'FINESS', 'NRSA', 'NADL', 'DDSEJ', 'DFSEJ', 'HASHTRA'
traSt.readFrom(new File(args.input_tra), 'ISO-8859-1', ';' as char)

if (traSt.columnCount > 1) {
    //il y a plusieurs colonnes .csv, c'est au format "Druides"
    //Parcourir la table pour remplir la table nadlParNrsa
    traSt.each { row ->
        def nrsa = row[1].trim()
        def NADL = row[2].trim()
        nadlParNrsa[nrsa] = NADL
    }
}
else {
    //lire le fichier TRA ancien format pour remplir la table nadlParNrsa
    mono {
        input      args.input_tra  //fichier d'entrée est donné dans le paramètre input_tra
        metaName   TRA_META

        onItem {item->
            def tra = item.mono
            nadlParNrsa[tra.txtNRSA] = tra.txtNDOSS //dans le TRA le champ est NDOSS
        }
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
            csvOutput.f 'NADL' //ajouter la colonne NADL
            csvOutput.endRow()
            return //et sortir de cette closure
        }
        def nrsa = row.NRSA ?: ''
        def nadl = nadlParNrsa[nrsa.trim()] ?: '?' //le NADL ou bien '?' si non trouvé
        //ecrire la rangee
        row.values.each { csvOutput.f it }
        csvOutput.f nadl //rajouter le nadl à la fin
        csvOutput.endRow() //finir la rangée
    }

    onEnd {
        csvOutput.close()
    }
}
