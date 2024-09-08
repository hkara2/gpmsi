/**☺:encoding=UTF-8:
 * A partir d'un fichier .csv qui contient des numeros de rsa dans une colonne NRSA,
 * (= une seule colonne dans le fichier, la première ligne doit contenir NRSA)
 * ecrire un fichier qui contient trois colonnes : NRSA, NADL et IPP.
 * Il faut fournir pour cela en plus un fichier TRA et un fichier VIDHOSP.
 * Paramètres d'entrée :
 * -a:input_csv CHEMIN_FICHIER    Un fichier csv avec en-tetes qui contient une colonne NRSA
 * -a:input_tra CHEMIN_FICHIER    Le fichier tra qui fait correspondre numéros de RSA avec numéro administratif local (s'appelle NDOSS au lieu de NADL dans TRA)
 * -a:input_vh CHEMIN_FICHIER     Le fichier vidhosp qui permet de recuperer le IPP pour un nadl donné
 * -a:output CHEMIN_FICHIER       Le fichier de sortie avec la colonne NADL ajoutée à la fin
 * Ex :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2022\M10\RSA
 * c:\app\gpmsi\exec -script c:\app\gpmsi\v1.2\scripts\groovy\nrsa_selection_nips.groovy ^
 * -a:input_csv NRSAs_chir_uro.txt ^
 * -a:input_tra 910019447.2022.10.tra.txt ^
 * -a:input_vh VIDHOSP_MCO.txt ^
 * -a:output NRSAs_chir_uro_avec_nadl.csv
 *
 */
import fr.gpmsi.CsvDestination;
import fr.gpmsi.StringTable;

if (!args.containsKey('input_csv')) throw new Exception("Argument manquant input_csv");
if (!args.containsKey('input_tra')) throw new Exception("Argument manquant input_tra");
if (!args.containsKey('input_vh')) throw new Exception("Argument manquant input_vh");
if (!args.containsKey('output')) throw new Exception("Argument manquant output");

//jusqu'a l'arrivee de druides le format TRA était tra2016
TRA_META = 'tra2016'; //par défaut on utilise le format TRA de 2016 qui est encore valable en 2023

nadlParNrsa = [:];

ippsParNadl = [:];

csvOutput = null;

traSt = new StringTable('TRA');
//signification des colonnes à revoir (hk mars 2023)
//'FINESS', 'NRSA', 'NDOSS', 'DDSEJ', 'DFSEJ', 'HASHTRA'
traSt.readFrom(new File(args.input_tra), 'ISO-8859-1', ';' as char);

//remplir maintenant la table de correspondance nrsa vers nadl
if (traSt.columnCount > 1) {
    //il y a plusieurs colonnes .csv, c'est au format "Druides" (qui peut changer ... Vérifier !)
    //Parcourir la table pour remplir la table nadlParNrsa
    traSt.each { row ->
        def nrsa = Integer.valueOf(row[0].trim()); //convertir le NRSA en nombre
        def ndoss = row[2].trim();
        //println("nrsa:'$nrsa',ndoss:'$ndoss'");
        nadlParNrsa[nrsa] = ndoss;
    }
}
else {
    //si le décodage csv n'a ramené qu'une colonne, c'est que c'est à l'ancien
    //format TRA (champs fixes mono-niveau).
    //lire le fichier TRA ancien format pour remplir la table nadlParNrsa
    mono {
        input      args.input_tra;  //fichier d'entrée est donné dans le paramètre input_tra
        metaName   TRA_META;

        onItem {item->
            def nrsa = Integer.valueOf(tra.txtNRSA); //convertir le NRSA en nombre
            def tra = item.mono;
            nadlParNrsa[nrsa] = tra.txtNDOSS;
        }
    }
}

//fabriquer la table qui permet de retrouver les IPP via le NADL
vidhosp {
    input args.input_vh;
    onItem { item ->
        vh = item.vidhosp;
        def nadl = vh.txtNADL;
        def ipp = vh.txtIPP;
        ippsParNadl[nadl] = ipp;
    }
}

//parcourir tous les numéros de RSA du fichier d'entrée
csv {
    input  args.input_csv;
    output args.output;

    onInit {
        csvOutput = new CsvDestination(new File(outputFilePath), "cp1252");
        //envoyer l'en-tete
        csvOutput.f 'NRSA';
        csvOutput.f 'NADL';
        csvOutput.f 'IPP';
        csvOutput.endRow();
    }

    onItem {item->
        def row = item.row;
        if (item.linenr == 1) return; //sortir de cette closure car on ignore la ligne d'en-tête
        def nrsaStr = row.NRSA ?: '-1';
        def nrsa = Integer.valueOf(nrsaStr);
        def nadl = nadlParNrsa[nrsa] ?: '?'; //le NADL ou bien '?' si non trouvé
        def ipp = ippsParNadl[nadl.trim()] ?: '?'; //l'IPP ou bien '?' si non trouvé
        csvOutput.f nrsaStr;
        csvOutput.f nadl.trim();
        csvOutput.f ipp.trim();
        csvOutput.endRow(); //finir la rangée
    }

    onEnd {
        csvOutput.close();
    }
}
