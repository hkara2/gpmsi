/**☺:encoding=UTF-8:
 * Production d'un FICHCOMP DMI (2020, non changé en 2026) à partir d'un fichier
 * Excel xlsx qui comporte au minimum les colonnes
 * FINESS;TYPPR;NADL;NRUM;DDEB;DFIN;CDE;NB;MNTP
 *
 * Arguments :
 * -a:input chemin_du_fichier_entree
 * -a:output chemin_du_fichier_sortie
 * Exemples :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script %GPMSI_BASE%\v@PROJECT_VERSION@\scripts\groovy\xlsx_vers_fichcompdmi.groovy -a:input FichCompDmi_PIE.xlsx
 */
import fr.gpmsi.pmsixml.FszGroupMeta
import static fr.gpmsi.FileUtils.splitNameAndExtension

def convertDateForPmsi(d) {
    //Utilise ce qui est déjà dans le 'binding' script :
    //frenchDateFormat : un SimpleDateFormat au format francais dd/MM/yyyy
    //pmsiDateFormat : un SimpleDateFormat au format francais mais sans séparateurs, pour les fichiers PMSI ddMMyyyy
    if (d == null || d == '') return ''
    if (d instanceof Date) return pmsiDateFormat.format(d)
    //println "d:${d.class.name} : $d"
    //a priori c'est de type String
    def dte = frenchDateFormat.parse(d)
    return pmsiDateFormat.format(dte)
}

//charger le fichier de définition du infoum1
def fichcompdmi_root = FszGroupMeta.getOrLoadMeta("fichcompdmi2020")
fichcompdmi = fichcompdmi_root.getFirstChildGroupMeta()

xlpoi {
    input args.input
    output args.output

    onInit {
        outw = new FileWriter(outputFilePath)
    }

    onItem {item->
        if (item.isEmpty()) return
        def row = item.row
        if (item.linenr == 1) {
            //la ligne 1 est celle des titres ; on l'ignore et on en profite pour mettre notre propre format de date pour la rangée
            row.setDateFormat(frenchDateFormat)
            return
        }
        def nd = fichcompdmi.makeBlankInstance()
        def finess = row.FINESS
        nd.FINESS.setValue(finess)
        def typpr = row.TYPPR //; println "typpr:'$typpr'"
        nd.TYPPR.setValue(typpr)
        def nadl = row.NADL
        nd.NADL.setValue(nadl)
        def nrum = row.NRUM
        nd.NRUM.setValue(nrum)
        def ddeb = row.DDEB //; println "ddeb:'$ddeb'"
        nd.DDEB.setValue(convertDateForPmsi(ddeb))
        def dfin = row.DFIN //; println "dfin:'$dfin'"
        nd.DFIN.setValue(convertDateForPmsi(dfin))
        def cde = row.CDE
        nd.CDE.setValue(cde)
        def nb = row.NB
        nd.NB.setValue(nb)
        def mntp = row.MNTP
        nd.MNTP.setValue(mntp)
        def sb = new StringBuffer()
        nd.toText(sb)
        outw << sb << '\r\n'
    }

    onEnd {
        outw.close()
    }
}
