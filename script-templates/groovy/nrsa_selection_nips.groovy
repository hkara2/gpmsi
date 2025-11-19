/**☺:encoding=UTF-8:
 * A partir d'un fichier .csv qui contient des numeros de rsa dans une colonne NRSA,
 * (= une seule colonne dans le fichier, la première ligne doit contenir NRSA)
 * ecrire un fichier qui contient trois colonnes : NRSA, NADL et IPP.
 * Il faut fournir pour cela en plus un fichier TRA et un fichier VIDHOSP.
 * Paramètres d'entrée :
 * -a:input_csv CHEMIN_FICHIER    Un fichier csv avec en-tetes qui contient une
 *                                colonne NRSA
 * -a:input_tra CHEMIN_FICHIER    Le fichier tra qui fait correspondre numéros
 *                                de RSA avec numéro administratif local
 *                                (s'appelle NDOSS au lieu de NADL dans TRA)
 * -a:input_vh CHEMIN_FICHIER     Le fichier vidhosp qui permet de recuperer
 *                                l'IPP pour un nadl donné
 * -a:output CHEMIN_FICHIER       Le fichier de sortie avec la colonne NADL
 *                                ajoutée à la fin
 * -f:dnais                       Si present une colonne DNAIS avec la date de
 *                                naissance est ajoutee
 * -f:dsej                        Si present trois colonnes DENT DSOR DSP avec
 *                                date entrée, date sortie de l'établissement
 *                                (qui sont dans le VIDHOSP) et durée de séjour
 *                                PMSI (date sortie - date entrée) sont émises
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
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Fonction pour formater une date au format francais ou une chaine vide si null */
def dateFrancaise(LocalDate ld) { ld ? ld.format(formatFrancais) : '' }

formatFrancais = DateTimeFormatter.ofPattern("dd/MM/yyyy");

if (!args.containsKey('input_csv')) throw new Exception("Argument manquant input_csv");
if (!args.containsKey('input_tra')) throw new Exception("Argument manquant input_tra");
if (!args.containsKey('input_vh')) throw new Exception("Argument manquant input_vh");
if (!args.containsKey('output')) throw new Exception("Argument manquant output");

//jusqu'a l'arrivee de druides le format TRA était tra2016
TRA_META = 'tra2016'; //par défaut on utilise le format TRA de 2016 qui est encore valable en 2023

nadlParNrsa = [:];

ippsParNadl = [:];

dnaisParNadl = [:];

dentParNadl = [:];
dsorParNadl = [:];

csvOutput = null;

emettreDnais = flags.contains('dnais');
emettreDsej = flags.contains('dsej');

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
        dnaisParNadl[nadl] = vh.DNAIS.toLocalDate()
        dentParNadl[nadl] = vh.DENT.toLocalDate()
        dsorParNadl[nadl] = vh.DSOR.toLocalDate()
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
        if (emettreDnais) csvOutput.f 'DNAIS';
        if (emettreDsej) {
            csvOutput.f 'DENT';
            csvOutput.f 'DSOR';
            csvOutput.f 'DSP';
        }
        csvOutput.endRow();
    }

    onItem {item->
        def row = item.row;
        if (item.linenr == 1) return; //sortir de cette closure car on ignore la ligne d'en-tête
        def nrsaStr = row.NRSA ?: '-1';
        def nrsa = Integer.valueOf(nrsaStr);
        def nadl = nadlParNrsa[nrsa] ?: '?'; //le NADL ou bien '?' si non trouvé
        def nadlt = nadl.trim(); //le même mais 'trimmé'
        def ipp = ippsParNadl[nadlt] ?: '?'; //l'IPP ou bien '?' si non trouvé
        def dnais = dnaisParNadl[nadlt]
        def dnaisStr = dateFrancaise(dnais);
        def dent = dentParNadl[nadlt]
        def dentStr = dateFrancaise(dent);
        def dsor = dsorParNadl[nadlt]
        def dsorStr = dateFrancaise(dsor);
        def durs = (dent && dsor) ? ChronoUnit.DAYS.between(dent, dsor) : null; //calcul jours (calendaires) entre deux dates
        csvOutput.f nrsaStr;
        csvOutput.f nadl.trim();
        csvOutput.f ipp.trim();
        if (emettreDnais) csvOutput.f dnaisStr;
        if (emettreDsej) {
            csvOutput.f dentStr;
            csvOutput.f dsorStr;
            csvOutput.f durs ?: '';
        }
        csvOutput.endRow(); //finir la rangée
    }

    onEnd {
        csvOutput.close();
    }
}
