// :encoding=utf-8:
/**
 * Transformer un fichier RSA accompagne de son fichier TRA en fichier Excel
 * xlsx.
 * pour créer de nouveaux scripts
 * arguments :
 * -a:input_rsa chemin_du_fichier_rsa_en_entree
 * -a:input_tra chemin_du_fichier_tra_en_entree
 * -a:output chemin_du_fichier_csv_en_sortie
 *
 * Exemple d'exécution :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2021\M12\RSA
 * %GPMSI_BASE%\@PROJECT_VERSION@\gpmsi -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rsa_tra_vers_xlsx.groovy -a:input_rsa 910019447.2021.12.rsa -a:input_tra 910019447.2021.12.tra.txt -a:output 910019447.2021.12_rsa.xlsx
 *
 * #260121 hk Création du fichier
 */

import fr.gpmsi.CsvDestination
import fr.gpmsi.StringTable
import fr.gpmsi.poi.XlsxHelper

headerSent = false

//charger les TRA (optionnel)
tra = null

//si il y a un argument "input_tra" lire les TRA au format csv
if (args.containsKey("input_tra")) {
    tra = new StringTable("TRA")
    //lire le TRA dans la StringTable
    tra.readFrom(new File(args.input_tra), ["nrsa", "nrss", "nadl", "ddsej", "dfsej", "ghm", "hash_tra"] as String[], "ISO-8859-1", ';' as char)
    //enlever les espaces de début et fin des nadl(s)
    tra.transform('nadl') {s-> s?.trim()}
    //idem pour les nrss
    tra.transform('nrss') {s-> s?.trim()}
    //ajouter un index sur le nrsa pour retrouver plus vite les nadl
    tra.addIndex('nrsa')
}

rsa {
    name 'Transformation RSA+TRA en Excel'

    input args['input_rsa']
    output args['output']

    onInit {
        classeur = new XlsxHelper("RSA_TRA");
    }

    onItem {item->
        def rsa = item.rsa
        def m = rsa.meta
        def cm = m.childMetas
        def names = cm*.stdName
        def vals = names.collect {childName->
            //println("$childName")
            // a faire : implementer le flag 'txt' pour avoir une sortie texte exclusive
            def child = rsa.getChild(childName)
            if (child == null) throw new Exception("element non trouve $childName")
            child.value ? child.value : ""
        }
        def childNodes = names.collect {childName->
            rsa.getChild(childName)
        }
        if (!headerSent) {
            //row = sh.createRow(cur_row)
            childNodes.each {childNode->
                def c = classeur.addCell(childNode.meta.stdName)
                def longName = childNode.meta.longName
                def remarks = childNode.meta.remarks ?: ''
                XlsxHelper.setComment(c, "$longName.\r\n$remarks" as String, '')
            }
            def cell = classeur.addCell('NADL')
            XlsxHelper.setComment(cell, "Numero Administratif Dossier Local" as String, '')
            cell = classeur.addCell('RUM_DAS')
            XlsxHelper.setComment(cell, "DAS de chaque RUM" as String, '')
            cell = classeur.addCell('NB_DAS')
            XlsxHelper.setComment(cell, "Nb de DAS" as String, '')
            cell = classeur.addCell('RUM_DP')
            XlsxHelper.setComment(cell, "DP de chaque RUM" as String, '')
            cell = classeur.addCell('RUM_DR')
            XlsxHelper.setComment(cell, "DR de chaque RUM" as String, '')
            cell = classeur.addCell('RUM_IGS2')
            XlsxHelper.setComment(cell, "IGS2 de chaque RUM" as String, '')
            classeur.newRow()
            headerSent = true
        }
        childNodes.each {childNode->
            if (childNode == null) return;
            if (childNode.meta.stdName == "DUTSP") classeur.addCell(childNode.toInt()) //cas particulier de la durée de séjour que l'on veut en numérique
            else classeur.addCell(childNode.value)
        }
        //ajouter le NADL si disponible
        def nadl = ''
        if (tra != null) nadl = tra.find('nrsa', rsa.txtNRSA, 'nadl') //recuperer le numero de dossier grace a la table des tra
        classeur.addCell(nadl)
        //ajouter tous les das
        String tousDas = "{" + (rsa.RU.collect { ru -> ru.DA.txtTDA.join(" ") }).join("}{") + "}"
        classeur.addCell(tousDas)
        //ajouter le nombre de DAS total de tous les RUMs
        int nbDas = rsa.RU.DA.flatten().size()
        classeur.addCell(nbDas)
        //ajouter tous les DPs
        String tousDp = "{" + rsa.RU.txtDP.join("}{") + "}"
        classeur.addCell(tousDp)
        //ajouter tous les DRs
        String tousDr = "{" + rsa.RU.txtDR.join("}{") + "}"
        classeur.addCell(tousDr)
        //ajouter tous les IGS2
        String tousIgs = "{" + (rsa.RU.IGS2*.toInt()).join("}{") + "}"
        classeur.addCell(tousIgs)
        classeur.newRow()

    }//onItem

    onEnd {
        File destFile = new File(outputFilePath);
        classeur.setOutput(destFile);
        classeur.writeFileAndClose();
    }
}
