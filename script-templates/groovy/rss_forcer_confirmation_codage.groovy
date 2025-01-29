/**☺:encoding=UTF-8:
 * Forcer la confirmation de codage pour les RSS dont le NDA est dans la liste
 * fournie. Cette liste est un fichier .csv avec comme premiere ligne les noms
 * de colonne, il doit y avoir une colonne NDA qui contient les numéros de
 * dossier administratif concernés.
 * Ce script est nécessaire pour DxCare qui ne permet pas cette confirmation
 * (bug) avant la 8.2021.5
 *
 * Arguments :
 *
 * -a:input  chemin_du_fichier_entree
 * -a:ndas   chemin_liste_ndas
 * -a:output chemin_du_fichier_sortie
 *
 * Exemples :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rss_forcer_confirmation_codage.groovy.groovy -a:input test.txt -a:ndas ndas.csv -a:output testout.txt
 */
import fr.gpmsi.StringTable
import fr.gpmsi.pmsixml.RssWriter

ndas = args.ndas
if (ndas == null) throw new Exception("Argument -a:ndas manquant")

inp = args.input
if (inp == null) throw new Exception("Argument -a:input manquant")

outp = args.output
if (outp == null) throw new Exception("Argument -a:output manquant")

//lire fichier des NDAs dans une StringTable

stNdas = new StringTable("NDAS", new File(ndas))

rss {
    name 'Forcer la confirmation de codage pour les RSS qui ont un NDA dans la liste'
    input inp
    onInit {
        rssw = new RssWriter()
        ow = new FileWriter(outp)
        crlf = "\r\n"
    }
    onItem {item ->
        def rum = item.rum
        def nadl = rum.txtNADL
        if (stNdas.contains('NDA', nadl)) {
            println "$nadl;mise confirmation codage a 1"
            rum.CCRS.value = '1'
        }
        ow.write(rssw.rumToString(rum))
        ow.write(crlf)
    }
    onEnd {
        ow.close()
    }
}

