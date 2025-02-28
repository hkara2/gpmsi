/**
 * Selection de TRA (format csv DRUIDES 2023 et ultérieur) pour lesquels le numero de RSA est
 * dans la liste des NADL donnés (fichier .csv dont une colonne doit être nommée NADL).
 * arguments en entree :
 * -a:input  : fichier des TRA en entree, au format csv (DRUIDES depuis 2023)
 * -a:nadls : fichier des numeros de dossier à selectionner (doit comporter une colonne NADL)
 * -a:output : fichier des lignes de TRA sélectionnés, au format csv
 * -f:pasnadl : si ce flag est présent, laisser le NADL vide en sortie
 *
 * Exemple :
 * cd C:\Local\Audit codage RSS\2024\2025-03-M12
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi.bat -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\tra_selection_par_nadl.groovy -a:input INOUT\910019447.2025.0.tra.txt -a:nadls "C:\Local\Audit codage RSS\2024\cumul-NADLs-vus.csv" -a:output RSA_VUS.csv
 */
import fr.gpmsi.StringTable
import fr.gpmsi.CsvDestination

titres = ["nligne", "nrss", "nadl", "ddsej", "dfsej", "ghm", "hash_tra"] as String[]

tras = new StringTable("TRA")
tras.readFrom(new File(args.input), titres, "ISO-8859-1", ';' as char)

dossiers = new StringTable("DOSSIERS", new File(args.nadls))
dossiers.addIndex("NADL")

d = new CsvDestination(new File(args.output))
d.f "nligne"
d.f "nrss"
d.f "nadl"
d.f "ddsej"
d.f "dfsej"
d.f "ghm"
d.f "hash_tra"
d.endRow()

tras.each {row->
    println("'${row.nadl}'")
    if (dossiers.contains("NADL", row.nadl.trim())) {
        //emettre la rangee
        d.f row.nligne
        d.f row.nrss
        if (flags.contains('pasnadl')) d.f "" else d.f row.nadl
        d.f row.ddsej
        d.f row.dfsej
        d.f row.ghm
        d.f row.hash_tra
        d.endRow()
    }
}

d.close()

