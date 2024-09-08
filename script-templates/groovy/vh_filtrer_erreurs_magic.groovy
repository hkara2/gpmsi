//☺:encoding=UTF-8:

/**
 * A partir d'un fichier détaillé d'erreurs MAGIC (magic.logdetail.txt) sortir les
 * lignes VIDHOSP en face de chaque erreur.
 * Arguments :
 * -a:input_vh chemin_du_fichier_vidhosp
 * -a:input_log chemin_du_fichier_magic_logdetail_txt
 * -a:output chemin_du_fichier_xlsx_en_sortie
 * Exemples :
 *c:\app\gpmsi\gpex.bat -script c:\app\gpmsi\v1.3\scripts\groovy\vh_filtrer_erreurs_magic.groovy -a:input_vh VIDHOSP_MCO.txt -a:input_log MAGIC\magic.logdetail.txt -a:output erreurs_vidhosp.xlsx
 *
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\gabarit_script.groovy -a:input test.txt -a:output testout.txt
 */
import fr.gpmsi.StringTable;
import fr.gpmsi.poi.XlsxHelper;

stringTransformTrim = {s -> s?.trim(); }

magicLog = new StringTable('MAGIC_LOG', new File(args.input_log));
//transformer la colonne des numeros administratifs pour qu'elle n'aie plus
//d'espaces de fin
magicLog.transform('NOADMINSEJ', stringTransformTrim);

headerSent = false;

vidhosp {
    input args.input_vh
    output args.output
    onInit {
        //ouvrir la destination
        destFile = new File(outputFilePath);
        xl = new XlsxHelper('lignes VIDHOSP en erreur');
    }
    onItem {item ->
        vh = item.vidhosp;
        nadl = vh.txtNADL;
        numsLignesErr = magicLog.findRows('NOADMINSEJ', nadl);
        numsLignesErr.each {numLigne ->
            //recuperer toutes les colonnes du vidhosp
            def m = vh.meta;
            def cm = m.childMetas;
            def names = cm*.stdName;
            def childNodes = names.collect {childName ->
                vh.getChild(childName);
            };
            //tout d'abord envoyer la ligne d'en-têtes si cela n'a pas encore été fait
            if (!headerSent) {
                def cell = xl.addCell('CDE_ERR');
                XlsxHelper.setComment(cell, 'Code erreur', 'script vh_filtrer_erreurs_magic');
                //row = sh.createRow(cur_row)
                childNodes.each {childNode ->
                    def stdName = childNode.meta.stdName;
                    cell = xl.addCell(stdName);
                    def longName = childNode.meta.longName;
                    def remarks = childNode.meta.getRemarksFormatted('\r\n');
                    XlsxHelper.setComment(cell, "$longName.\r\n$remarks", 'script vh_filtrer_erreurs_magic');
                }
                xl.newRow()
                headerSent = true;
            };
            //envoyer maintenant le code erreur, suivi de toutes les colonnes du vidhosp
            codeErr = magicLog.getValue(numLigne, 'CODERR');
            xl.addCell(codeErr);
            childNodes.each {childNode ->
                xl.addCell(childNode); //hk 231108 XlsxHelper se débrouille maintenant avec le FszNode
            };
            xl.newRow();
        };
    }
    onEnd {
        xl.setOutput(destFile);
        xl.writeFileAndClose();
    }
}
