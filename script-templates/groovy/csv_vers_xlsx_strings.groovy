/**:encoding=utf-8:
 * Transformer de manière générique un fichier CSV fichier Excel, avec toutes
 * les colonnes Excel de type "String".
 *
 * arguments :
 * -a:input chemin_du_fichier_csv_a_convertir
 * -a:output chemin_du_fichier_xlsx_en_sortie
 * -a:titre titre_de_la_feuille (si absent, le titre est "Feuil1")
 * -a:sep code_hexa_separateur       : optionnel, code hexadécimal du séparateur. Par défaut 28 (point-virgule), sinon 2C (virgule)
 * -a:enc encodage                   : optionnel, encodage du fichier. Par défaut windows-1252, sinon UTF-8 est courant aussi.
 *
 * Exemple d'exécution :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2021\M12\RSA
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\csv_vers_xlsx_strings.groovy -a:input in\csv_vers_xlsx_donnees1.csv -a:output tmp-out\csv_vers_xlsx_donnees1.xlsx
 *
 * #240805 hk Création du fichier
 * #241115 hk Ajout options sep et enc
 */

import fr.gpmsi.CsvDestination
import fr.gpmsi.poi.XlsxHelper
import fr.gpmsi.StringTable

sep = args.sep
if (sep == null) '28' //par défaut séparateur ';'
sepc = (Integer.parseInt(sep), 16) as char
lg.debug "utilisation separateur '$sepc'"

enc = args.enc
if (enc == null) 'windows-1252' //par défaut encodage windows-1252

title = args.titre
if (title == null || title == "") title = "Feuil1"

classeur = new XlsxHelper(title);

csv {
    name 'Transformation générique csv en Excel .xlsx avec colonnes String'

    input args['input']
    output args['output']
    csvSeparator sepc
    inputEncoding enc

    onInit { //rien de plus a initialiser
    }

    onItem {item->
        def row = item.row
        row.values.each { classeur.addCell(it) }
        classeur.newRow()
    }

    onEnd {
        File destFile = new File(outputFilePath);
        classeur.setOutput(destFile);
        classeur.writeFileAndClose();
    }
}
