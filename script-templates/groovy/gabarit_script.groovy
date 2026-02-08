/**☺:encoding=UTF-8:
 * Gabarit pour un nouveau script. A modifier selon les besoins.
 * Contient des exemples pour l'utilisation de input, output, un argument, un flag.
 * Arguments :
 * -a:input chemin_du_fichier_entree
 * -a:output chemin_du_fichier_sortie
 * -a:monarg un_argument_exemple
 * Drapeaux :
 * -f:myflag lever le drapeau myflag
 *
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
        //donner la valeur de l'argument monarg
        monarg = args['monarg']
        println "monarg : $monarg"
        //indiquer que le drapeau a été levé
        if (flags.contains('myflag')) println "option myflag detectee"
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