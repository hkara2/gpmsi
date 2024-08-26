/**☺:encoding=UTF-8:
 * Difference entre dossiers VIDHOSP et DMI. Indique les enregistrements DMI
 * pour lesquels le NADL n'est pas retrouvé dans le VIDHOSP
 * Arguments :
 * input_dmi : le chemin du fichier qui contient le FICHCOMP DMI
 * input_vh : le chemin du fichier qui contient les VID HOSP
 * output : le chemin du fichier qui contient les différences
 * meta  : nom_du_fichier_de_metadonnees_a_utiliser (par ex. fichcompdmi2020)
 * metasDir  : chemin_du_repertoire_qui_contient_les_metadonnees (optionnel)
 *
 * Exemple d'exécution :
  c:\app\gpmsi\v@PROJECT_VERSION@\scripts\gpmsi.bat ^
  -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\diff-vidhosp-dmi.groovy ^
  -a:input_vh vh.txt ^
  -a:input_dmi fichcompdmi.txt ^
  -a:meta fichcompdmi2020 ^
  -a:output diffs_dmi_vh.txt
 * 
 * (C) Harry Karadimas 2024, CHSE
 */

import fr.karadimas.gpmsi.CsvDestination

nadls_vh = [] as Set

vidhosp {
  name 'parcourir les vidhosp'
  input args['input_vh']
  
  onInit {
  }
  
  onItem {item->
  	def vh = item.vidhosp
  	def nadl = vh.txtNADL //numero de dossier
  	nadls_vh.add(nadl)
	//println "$nadl\t$nia\t$dent\t$dsor"
  }
  
  onEnd {
    println "Fin lecture vidhosp."
  }
}


/* Champs FICHCOMP DMI (2020) :
 *
 *      Typ Libellé                           Nomc  Taille  Début Fin Obligatoire [1] Type[2] TypePref  Cadrage/Remplissage Remarques               Compteur Format
 *      FCD N° FINESS                         FINESS   9      1    9  O               A       A         Gauche/Espace
 *      FCD Type de prestation                TYPPR    2     10   11  O               A       A         NA/NA               Fixe, 02
 *      FCD N° Identifiant de séjour          NADL    20     12   31  O               A       A         Gauche/Espace
 *      FCD N° de RUM                         NRUM    10     32   41  F               A       A         Gauche/Espace       Non utilise pour DMI
 *      FCD Date de début (JJMMAAAA)          DDEB     8     42   49  O               N       D         Droite/Zéro         Date de pose
 *      FCD Date de fin (JJMMAAAA)            DFIN     8     50   57  F               N       D         NA/NA               Non utilise pour DMI
 *      FCD Code                              CDE     15     58   72  O               A       A         Gauche/Espace       Code LPP
 *      FCD Nombre                            NB      10     73   82  O               N       N         Droite/Zéro         Nombre posé (10+0)               10+0
 *      FCD Montant payé                      MNTP    10     83   92  O               N       N         Droite/Zéro         Prix d’achat par le nombre posé  7+3
 *      S:  MONO
 */
mono {
    name 'Parcourir les DMI'
    input args['input_dmi']
    output args['output']
    metaName args.meta
    metasDir args.metasDir

    onInit {
        //ouvrir la destination
        outf = new FileWriter(outputFilePath)
    }
    onItem {item->
        def dmi = item.mono
        def nadl = dmi.txtNADL
        if (!nadls_vh.contains(nadl)) {
            //le NADL du DMI ne fait pas partie des séjours listés, envoyer l'enregistrement
            outf << item.line << nl
        }
    }
    onEnd {
        //fermer la destination
        outf.close()
    }

}
