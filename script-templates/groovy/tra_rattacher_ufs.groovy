/**:encoding=UTF-8:
 * Pour chaque TRA rattacher la séquence des UMs du séjour.
 * La séquence est extraite du parcours des lignes du RSS.
 * arguments en entree :
 * -a:input_tra  : fichier des TRA en entree, au format csv (DRUIDES depuis 2023)
 * -a:input_rss : fichier des RSS
 * -a:output : fichier des lignes de TRA sélectionnés, au format csv
 *
 * Exemple :
 * cd C:\Local\Audit codage RSS\2024\2025-03-M12
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi.bat -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\tra_rattacher_ufs.groovy -a:input_tra INOUT\910019447.2025.0.tra.txt -a:input_rss MCO_RSSG_20250225150906_E_F-R01R02.txt -a:output RSAS_UMS.csv
 */
import fr.gpmsi.StringTable
import fr.gpmsi.CsvDestination

titres = ["nligne", "nrss", "nadl", "ddsej", "dfsej", "ghm", "hash_tra"] as String[]

tras = new StringTable("TRA")
tras.readFrom(new File(args.input_tra), titres, "ISO-8859-1", ';' as char)
tras.transform("nadl", {s-> s?.trim()})

seqUmsParNadl = [:]

//Remplir la table des séquences d'UFs par NADL
rss {
    input args.input_rss
    onItem {item->
        def rum = item.rum
        def nadl = rum.txtNADL
        def seqUms = seqUmsParNadl[nadl]
        if (seqUms == null) {
            seqUms = rum.txtNUM
        }
        else {
            seqUms = seqUms + '-' + rum.txtNUM
        }
        seqUmsParNadl[nadl] = seqUms
    }
}//rss

d = new CsvDestination(new File(args.output))
d.f "nligne"
d.f "nrss"
d.f "nadl"
d.f "ddsej"
d.f "dfsej"
d.f "ghm"
d.f "seq_ums"
d.endRow()

tras.each {row->
    println("'${row.nadl}'")
    //emettre la rangee
    d.f row.nligne
    d.f row.nrss
    d.f row.nadl
    d.f row.ddsej
    d.f row.dfsej
    d.f row.ghm
    def seqUms = seqUmsParNadl[row.nadl.trim()]
    if (seqUms == null) d.f " " else d.f seqUms
    d.endRow()
}

d.close()

