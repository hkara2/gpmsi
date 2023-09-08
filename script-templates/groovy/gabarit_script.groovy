//☺:encoding=UTF-8:

/**
 * Gabarit pour un nouveau script. A modifier selon les besoins
 * Arguments :
 * -a:input chemin_du_fichier_entree
 * -a:output chemin_du_fichier_sortie
 * Exemples :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\gabarit_script.groovy -a:input test.txt -a:output testout.txt
 */

//Etape line ici car simple, autres étapes possibles : rss, rsa, rhs, rha, csv, single, vidhosp, rsface, mono, xlpoi, dbf
line {
    input args.input
    output args.output
    onInit {
        //ouvrir la destination
        outf = new FileWriter(outputFilePath)
    }
    onItem {item->
        //ecrire numéro de ligne, suivi du contenu de la ligne
        outf << item.linenr << ' ' << item.line << nl
    }
    onEnd {
        //fermer la destination
        outf.close()
    }
}