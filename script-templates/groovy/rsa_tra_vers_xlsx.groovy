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
 * c:\app\gpmsi\exec.bat -script c:\app\gpmsi\v1.3\scripts\groovy\rsa_tra_vers_xlsx.groovy -a:input_rsa 910019447.2021.12.rsa -a:input_tra 910019447.2021.12.tra.txt -a:output 910019447.2021.12_rsa.xlsx
 *
 * #240618 hk Création du fichier
 */

import fr.gpmsi.CsvDestination
import fr.gpmsi.poi.XlsxHelper

headerSent = false

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
            classeur.newRow()
            headerSent = true
        }
        childNodes.each {childNode->
            classeur.addCell(childNode?.value)
        }
        classeur.newRow()

    }//onItem

    onEnd {
        File destFile = new File(outputFilePath);
        classeur.setOutput(destFile);
        classeur.writeFileAndClose();
    }
}
