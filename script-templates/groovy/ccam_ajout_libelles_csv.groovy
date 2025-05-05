/**☺:encoding=utf-8:
 * A partir d'un fichier csv qui contient une colonne CODEP avec des codes CCAM
 * simples (7 caracteres), ou avec extension PMSI (7 caracteres + '-' + 2 chiffres)
 * produire un fichier de sortie avec les libelles
 * ajoutes (a partir du fichier "%USERPROFILE%\.gpmsi\ccam\ccam_descr_pmsi_utf8.csv"
 * au format windows-1252 (pour pouvoir l'ouvrir directement avec Excel)
 *
 * arguments :
 * -a:input chemin_du_fichier_csv_avec_codes_ccam
 * -a:output chemin_du_fichier_csv_en_sortie
 *
 * Exemple d'exécution :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2021\M12\RSA
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\csv_vers_xlsx_strings.groovy -a:input in\csv_vers_xlsx_donnees1.csv -a:output tmp-out\csv_vers_xlsx_donnees1.xlsx
 *
 * #250424 hk Création du fichier
 */

import fr.gpmsi.CsvDestination
import fr.gpmsi.poi.XlsxHelper
import fr.gpmsi.StringTable

def uh = System.getProperty('user.home')
ccamFile = new File(uh + "\\.gpmsi\\ccam\\ccam_descr_pmsi_utf8.csv")
if (!ccamFile.exists()) throw new Exception("L'exécution de ce script nécessite la CCAM descriptive PMSI dans le fichier $ccamFile")
//charger ccam en encodage utf8, avec indexation de la colonne 1 (CODE7, le code qui a juste 7 car., cad PMSI sans extension)
ccam = new StringTable(ccamFile, "utf-8", 1)

/**
 * retrouver le nom CCAM qui correspond au code passé en paramètre.
 * Utilise la CCAM descriptive à usage PMSI, la mettre à jour dès qu'il y en a
 * une nouvelle.
 */
def nomCcam(cde) {
  if (cde == null) return ""
  cde = cde.trim()
  if (cde.equals("")) return ""
  //attention lorsqu'il n'y a pas d'extension PMSI parfois on retrouve "-00", l'enlever si c'est le cas
  if (cde.endsWith('-00')) cde = cde[0..-4]
  def libc = null
  //si le code ne fait que 7 caracteres on prend le premier de CODE7. Sinon on cherche dans CODEP.
  if (cde.length() == 7) libc = ccam.find('CODE7', cde, 'LIBELLE')
  else libc = ccam.find('CODEP', cde, 'LIBELLE')
  if (libc == null) return ""
  return libc
}


csv {
    input args.input
    onInit {
        d = new CsvDestination(new File(args.output))
        d.f "CODEP"
        d.f "LIBELLE"
        d.endRow()
    }
    onItem {item->
        if (item.linenr == 1) return //ne pas traiter la ligne de titre
        row = item.row
        def code = row.CODEP
        def libelle = nomCcam(code)
        d.f code
        d.f libelle
        d.endRow()
    }
    onEnd {
        d.close()
    }
}
