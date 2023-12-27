//:encoding=UTF-8:
//produire un infoum1 a partir d'un fichier csv saisi a la main sous excel
//et sauvegarde au format (mono) infoum1
//hk 211001 creation

import java.text.SimpleDateFormat
import fr.karadimas.pmsixml.FszGroupMeta

def convertDateForPmsi(d) {
    def dte = dfFr.parse(d)
    return dfPmsi.format(dte)
}


//Production d'un infoum1 à partir d'un fichier .csv
//Seules les colonnes suivantes sont utilisées :
//NUM   FGEO    TYAUT   DEAUT   NBLITS  MODHOSP
csv {
    input args.input
    onInit {
        //charger le fichier de définition du infoum1
        def infoum_root = FszGroupMeta.getOrLoadMeta("infoum1")
        infoum = infoum_root.getFirstChildGroupMeta()
        //println "fcivgmd $fcivgmd"
        dfFr = new SimpleDateFormat('dd/MM/yyyy')
        dfPmsi = new SimpleDateFormat('ddMMyyyy')
        outf = new FileOutputStream(args.output)
    }
    onItem {item->
        if (item.linenr == 1) return //ignorer premiere ligne
        //println "Ligne $item.linenr"
        def row = item.row
        println "Ligne $item.linenr, rangée : $row"
        def num = row.NUM
        def fgeo = row.FGEO
        def tyaut = row.TYAUT
        def deaut = row.DEAUT
        def nblits = row.NBLITS
        def modhosp = row.MODHOSP
        def nd = infoum.makeBlankInstance()

        nd.getChild('NUM').value = num
        nd.getChild('FGEO').value = fgeo
        nd.getChild('TYAUT').value = tyaut
        nd.getChild('DEAUT').value = convertDateForPmsi(deaut)
        nd.getChild('NBLITS').value = nblits
        nd.getChild('MODHOSP').value = modhosp
        def sb = new StringBuffer()
        nd.toText(sb)
        println sb
        outf << sb << "\r\n"
    }
    onEnd {
        outf.close()
    }
}
