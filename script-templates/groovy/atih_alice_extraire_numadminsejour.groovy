/**☺:encoding=UTF-8:
 * A partir d'un fichier .csv produit par l'outil "Alice", extrait un fichier
 * qui ne contient que les "NumAdminSejour", sans les espaces.
 * La première ligne est NumAdminSejour
 * On peut changer le nom en autre chose au lieu de "NumAdminSejour", notamment
 * pour les fichiers Alice SMR où le nom est "NumAdmin". On fait cela avec
 * l'argument "col".
 * Le nom du fichier de sortie est celui d'entree, mais sans l'extension, et
 * avec "_NumAdminSejour.txt" (ou le nom donné dans l'argument "col" s'il y a
 * lieu) à la fin.
 *
 * Arguments :
 * -a:input chemin_du_fichier_entree
 * -a:col nom_de_colonne       : nom de colonne a extraire (par défaut NumAdminSejour)
 *
 * Exemples :
 * cd C:\hkchse\qualite-risque\2026\alice
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\atih_alice_extraire_numadminsejour.groovy -a:input 910000280.2025.12.ca.MCO.20260414115130.csv
 */
import fr.gpmsi.PreambleRemovedReader
import static fr.gpmsi.FileUtils.splitNameAndExtension

rd = new PreambleRemovedReader(new FileReader(args.input), /^-+$/)

col = "NumAdminSejour"
if (args.col) col = args.col

csv {
    inputReader rd
    onInit {
        outf = new PrintWriter(
                 new FileWriter(
                   splitNameAndExtension(args.input)[0]+"_${col}.txt"
                 )
               )
    }
    onItem {item->
        row = item.row
        nas = row."$col".trim()
        outf.println "$nas"
    }
    onEnd {
        outf.close()
    }
}

