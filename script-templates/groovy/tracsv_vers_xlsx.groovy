/**☺:encoding=UTF-8:
 * Lire un fichier TRA au format CSV (Format de DRUIDES à partir de avril 2023)
 * et le convertir au format .xlsx, avec les noms de colonne suivants :
 * nligne;nrss;nadl;ddsej;dfsej;ghm;hash_tra
 *
 * Arguments :
 * -a:input chemin_du_fichier_entree
 * -a:output chemin_du_fichier_sortie
 * Exemples :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\tracsv_vers_xlsx.groovy -a:input montra.txt -a:output montra.xlsx
 */
import fr.gpmsi.StringTable
import fr.gpmsi.poi.XlsxHelper
import static fr.gpmsi.StringUtils.isTrimEmpty

tras = new StringTable("TRA")
//lire le TRA dans la StringTable
tras.readFrom(new File(args.input), ["nligne", "nrss", "nadl", "ddsej", "dfsej", "ghm", "hash_tra"] as String[], "ISO-8859-1", ';' as char)

//transformer les dates en utilisant les deux formats qui sont présents par défaut
//tras.transformDates("ddsej", pmsiDateFormat, frenchDateFormat)
//tras.transformDates("dfsej", pmsiDateFormat, frenchDateFormat)


//for (int i = 0; i < tras.rowCount; i++) {
//    cur_row = tras[i]
//    for (int j = 0; j < cur_row.size(); j++) {
//        val = cur_row[j]
//        println "$i,$j:$val"
//    }
//}

xlsx = new XlsxHelper("tra")

tras.columnNames.each {name-> xlsx.addCell(name)}
xlsx.newRow()
cal = Calendar.getInstance()
tras.each {row->
    row.eachWithIndex {colval, ix->
        if (!isTrimEmpty(colval) && (ix == 3 || ix == 4)) {
            cal.setTime(pmsiDateFormat.parse(colval))
            xlsx.addCell(cal)
        }
        else {
          xlsx.addCell(colval)
        }
    }
    xlsx.newRow()
}

xlsx.setOutput(new File(args.output))
xlsx.writeFileAndClose()