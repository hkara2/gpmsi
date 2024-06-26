// :encoding=utf-8:
/**
 * Transformer un fichier ANOHOSP en fichier Excel xlsx
 * arguments :
 * -a:input chemin_du_fichier_anohosp_en_entree
 * -a:output chemin_du_fichier_xlsx_en_sortie
 *
 * Exemple d'exécution :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2021\M12\RSA
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\anohosp_vers_xlsx.groovy -a:input 910019447.2024.2.001.ano.txt -a:output 910019447.2024.2.001.ano.xlsx
 *
 * #240618 hk Création du fichier
 */

import fr.karadimas.gpmsi.CsvDestination
import fr.karadimas.gpmsi.poi.XlsxHelper

headerSent = false

//déterminer la métadonnée à partir de la première ligne, pos 17 à 19 (soit 16 à 18 parce que java commence à 0. Toutes les lignes doivent être de la même version, ce qui est normalement le cas pour un ANOHOSP)

ver = new File(args.input).text[16..18]
metaNameCalc = "anohospV$ver" as String

mono {
    name 'Transformation ANOHOSP en Excel'

    input args['input']
    output args['output']

    metaName metaNameCalc

    onInit {
        classeur = new XlsxHelper("ANO$ver");
    }

    onItem {item->
        def mono = item.mono
        def m = mono.meta
        def cm = m.childMetas
        def names = cm*.stdName
        def vals = names.collect {childName->
            def child = mono.getChild(childName)
            if (child == null) throw new Exception("element non trouve $childName")
            child.value ? child.value : ""
        }
        def childNodes = names.collect {childName->
            mono.getChild(childName)
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
