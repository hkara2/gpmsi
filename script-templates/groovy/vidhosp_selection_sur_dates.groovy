/**☺:encoding=UTF-8:
 * Filtrer les lignes VIDHOSP en fonction de critères date entree et/ou date de sortie
 * Arguments :
 * -a:input chemin_du_fichier_vidhosp
 * -a:dent_min date_entree_minimum_JJMMAAA_(incluse)                   optionnel
 * -a:dent_max date_entree_maximum_JJMMAAA_(incluse)                   optionnel
 * -a:dsor_min date_sortie_minimum_JJMMAAA_(incluse)                   optionnel
 * -a:dsor_max date_sortie_maximum_JJMMAAA_(incluse)                   optionnel
 * -a:output chemin_du_fichier_vidhhosp_en_sortie
 * Exemple :
 *
 * Ne garder que les enregistrements pour lesquels la date de sortie est inferieure ou egale au 30/11/2025 :
 * %GPMSI_BASE%\v@PROJECT_VERSION@\gpmsi -script %GPMSI_BASE%\v@PROJECT_VERSION@\scripts\groovy\vidhosp_selection_sur_dates.groovy -a:dsor_max 30112025 -a:input VIDHOSP_MCO.txt -a:output VIDHOSP_MCO_filtre.txt
 */

def traiteDatePmsi(str) {
    if (str == null || str.trim() == '') null
    else pmsiDateFormat.parse(str)
}

//n.b. date d'entrée = DENT, date de sortie = DSOR
dent_min = traiteDatePmsi(args.dent_min)
dent_max = traiteDatePmsi(args.dent_max)
dsor_min = traiteDatePmsi(args.dsor_min)
dsor_max = traiteDatePmsi(args.dsor_max)

vidhosp {
    input args.input
    output args.output
    onInit {
        //ouvrir la destination
        destFw = new FileWriter(outputFilePath);
    }
    onItem {item ->
        vh = item.vidhosp //l'enregistrement VIDHOSP décodé
        line = item.line //la ligne qui contient tout le VIDHOSP brut tel que lu en texte
        dent = vh.DENT.toDate()
        dsor = vh.DSOR.toDate()
        if (dent_min && dent.before(dent_min)) return 
        if (dent_max && dent.after(dent_max)) return 
        if (dsor_min && dsor.before(dsor_min)) return 
        if (dsor_max && dsor.after(dsor_max)) return 
        //ok tous les critères temporels sont remplis, on peut émettre la ligne du VIDHOSP que l'on garde
        destFw << line << '\r\n'
    }
    onEnd {
        destFw.close()
    }
}
