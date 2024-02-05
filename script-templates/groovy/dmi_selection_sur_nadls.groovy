//☺:encoding=UTF-8:

/**
 * Garder les DMIs qui sont dans la liste des sejours (NADLS).
 * Arguments :
 * -a:input chemin_du_fichier_entree
 * -a:meta nom_du_fichier_de_metadonnees_a_utiliser (par ex. fichcompdmi2020)
 * -a:metasDir chemin_du_repertoire_qui_contient_les_metadonnees (optionnel)
 * -a:nadls chemin_du_fichier_nadls (la première ligne doit être NADL)
 * -a:output chemin_du_fichier_sortie
 * Exemples :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi ^
  -script "C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\dmi_selection_sur_nadls.groovy" ^
  -a:input FichComp2022.txt ^
  -a:meta fichcompdmi2020 ^
  -a:nadls sej-a-cheval.csv ^
  -a:output FichComp2022_a_cheval_sur_2023.txt
 */
import fr.karadimas.gpmsi.StringTable

mono {
    input args.input
    output args.output
    metaName args.meta
    metasDir args.metasDir

    onInit {
        //ouvrir la liste des nadl
        nadls = new StringTable("NADLS", new File(args.nadls))
        //ouvrir la destination
        outf = new FileWriter(outputFilePath)
    }
    onItem {item->
        def dmi = item.mono
        def nadl = dmi.txtNADL
        if (nadls.contains('NADL', nadl)) {
            //le NADL du séjour fait partie des séjours listés, envoyer l'enregistrement
            outf << item.line << nl
        }
    }
    onEnd {
        //fermer la destination
        outf.close()
    }
}