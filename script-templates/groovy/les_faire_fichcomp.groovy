//☺:encoding=UTF-8:

/**
 * Faire un fichcomp
 * Arguments :
 * -a:input chemin_du_fichier_csv_en_entree
 * -a:output chemin_du_fichier_fichcomp_en_sortie
 * -a:md nom_des_metadonnees_a_utiliser
 * Exemples :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\les_faire_fichcomp.groovy -a:input test.txt -a:output testout.txt -a:md fichcompmed2020
 */
import fr.gpmsi.pmsixml.MetaFileLoader
import fr.gpmsi.pmsixml.FszGroupMeta

//Etape csv qui lit les rangees a transformer en fichcomp molecules onereuses
/* champs :
FINESS
TYPPR  Hors ATU : 06 et sous ATU : 09
NADL   Numéro administratif local de séjour
DTADM Date d'administration JJ/MM/AAAA
CUCD  Code UCD
NBADM  Nombre administre (nombre exact à virgule)
PXACHU  Prix d'achat unitaire (pour 1, nombre exact à virgule)
VCR     Validation initiale de la prescription par un centre de référence ou de compétence 1: oui, 2:Non
INDIC   Indication
*/

csv {
    input args.input
    output args.output
    onInit {
        md = args.md
        //ouvrir la destination
        outf = new FileWriter(outputFilePath)
        meta = FszGroupMeta.getOrLoadMeta(md)
        fichcompmed = meta.getFirstChildGroupMeta()
        sb = new StringBuffer()
        meta.dump(sb)
        println "meta:${sb.toString()}"
    }
    onItem {item->
        if (item.linenr == 1) return //ignorer ligne des entetes
        row = item.row
        g = fichcompmed.makeBlankInstance()
        println "g:${g.toString()} , ${g.dumpStructureString()}"
        children = g.getChildren()
        println "children:$children"
        g.NADL.value = row.NADL
        sb = new StringBuffer()
        g.toText(sb)
        outf << sb.toString() << nl
    }
    onEnd {
        //fermer la destination
        outf.close()
    }
}