/**:encoding=UTF-8:
 * Recopier le fichier d'entrée csv vers le fichier de sortie, mais pour la colonne
 * "col" remplir avec des zéros jusqu'à "colsize", si la colonne n'est pas vide.
 * La première ligne doit contenir les noms de colonne.
 *
 * Arguments :
 * input : le fichier d'entrée csv.
 * output : le fichier de sortie csv.
 * col : le nom de la colonne à ajuster avec des zéros
 * colsize : largeur de la colonne
 *
 * Exemple :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\csv_col_remplir_zero.groovy -a:input monfichier.csv -a:output monfichier_zp.csv -a:col NDA -a:colsize 9
 * C:\app\gpmsi\v2.1\gpmsi -script C:\app\gpmsi\v2.1\scripts\groovy\csv_col_remplir_zero.groovy -a:input 910000280.2025.12.dpa.SMR.20260414114937_NumAdmin.txt -a:output 910000280.2025.12.dpa.SMR.20260414114937_NumAdmin_zp.txt -a:col NumAdmin -a:colsize 9
 *
 */
import fr.gpmsi.CsvDestination

/**
 * Zero pad de str jusqu'à ce qu'il ait la taille sz.
 * Si str est null ou à blanc, renvoie ""
 */
def zp(str, sz) {
    if (str == null || str.trim() == '') return ""
    while (str.length() < sz) str = "0" + str
    return str
}

col = args.col
verifyNotNull(col, "Argument 'col' manquant")

colsize_str = args.colsize
verifyNotNull(colsize_str, "Argument 'colsize' manquant")

colsize = colsize_str as int

colCount = -1

csv {
    input args.input

    onInit {
        outf = new CsvDestination(new File(args.output))
    }

    onItem {item->
        row = item.row
        if (item.linenr == 1) {
            for (int i = 0; i < csvColumnCount; i++) outf.f csvHeaderRow[i]
            outf.endRow()
        }
        else {
            for (int i = 0; i < csvColumnCount; i++) {
                if (csvHeaderRow[i] == col) outf.f zp(row[i], colsize)
                else outf.f row[i]
            }
            outf.endRow()
        }
    }

    onEnd {
        outf.close()
    }
}

