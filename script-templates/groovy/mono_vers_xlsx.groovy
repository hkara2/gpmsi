/**:encoding=UTF-8:
 * Lister un fichier "mono-niveau" (une simple table) vers son équivalent en xlsx
 * Arguments :
 * -a:input le fichier d'entree
 * -a:meta le nom du fichier de metadonnees a utiliser
 * -a:metasDir le chemin du repertoire qui contient les fichiers de metadonnees
 * -a:output le fichier de sortie
 * -f:txt forcer la sortie au format texte (par defaut le type prefere est utilise)
 *
 * Exemples d'execution :
 * gpmsi -script C:\hkchse\dev\chse-gpmsi\scripts\mono-vers-csv.groovy -a:input 910019447.2017.12.ium -a:meta ium2017 -a:metasDir C:\hkchse\pmsi\formats\pmsixml -a:output 910019447.2017.12.ium.csv
 * gpmsi -script C:\hkchse\dev\chse-gpmsi\scripts\mono-vers-csv.groovy -a:input FICHCOMPATU -a:meta fichcompatu2017 -a:output FICHCOMPATU.csv
 * gpmsi -script C:\hkchse\dev\chse-gpmsi\scripts\mono-vers-csv.groovy -a:input FICHCOMPMED -a:meta fichcompmed2017 -a:output FICHCOMPMED.csv
 * gpmsi -script C:\hkchse\dev\chse-gpmsi\scripts\mono-vers-csv.groovy -a:input 910019447.2017.12.tra.txt -a:meta tra2016 -a:output 910019447.2017.12.tra.csv
 * gpmsi -script C:\hkchse\dev\chse-gpmsi\scripts\mono-vers-csv.groovy -a:input 910019447.2019.6.ano.in.txt -a:meta anohospV013 -a:output 910019447.2019.6.ano.in.txt.csv
 * gpmsi -script C:\hkchse\dev\chse-gpmsi\scripts\mono-vers-csv.groovy -a:input VIDHOSP_MCO.txt -a:meta vidhospV013 -a:output VIDHOSP_MCO.txt.csv
 * Harry Karadimas 2020
 * v.1 200326 hk creation, base sur mono-vers-csv.groovy
 */
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

def d(val) {
    Cell c = row.createCell(cur_col);
    c.setCellValue(val);
    c.setCellStyle(cs);
    cur_col++;
    return c
}

def endRow() {
    cur_row++
    cur_col = 0
    row = sh.createRow(cur_row)
}

def addComment(cell, commentText) {
    //
    CreationHelper factory = wb.getCreationHelper();

    ClientAnchor anchor = factory.createClientAnchor();
    //i found it useful to show the comment box at the bottom right corner
    anchor.setCol1(cell.getColumnIndex() + 1); //the box of the comment starts at this given column...
    anchor.setCol2(cell.getColumnIndex() + 5); //...and ends at that given column
    anchor.setRow1(cur_row + 1); //one row below the cell...
    anchor.setRow2(cur_row + 8); //...and 4 rows high

    Drawing drawing = sh.createDrawingPatriarch();
    Comment comment = drawing.createCellComment(anchor);
    //set the comment text and author
    comment.setString(factory.createRichTextString(commentText));
    comment.setAuthor("Gpmsi");

    cell.setCellComment(comment);
}

sh = null //the Sheet
cur_row = 0
cur_col = 0
row = null //objet "row" courant
cs = null //cell style
headerSent = false

mono {
    input args.input
    output args.output
    metaName args.meta
    metasDir args.metasDir

    headerSent = false

    onInit {
        wb = new XSSFWorkbook();
        fout = new FileOutputStream(outputFilePath);
        sh = wb.createSheet();
        cs = wb.createCellStyle();
    }

    onItem {item->
        def mono = item.mono
        def m = mono.meta
        def cm = m.childMetas
        def names = cm*.stdName
        def vals = names.collect {childName->
            //println("$childName")
            // a faire : implementer le flag 'txt' pour avoir une sortie texte exclusive
            mono.getChild(childName)?.formattedValue //200129 hk utilisation de formattedValue
        }
        def childNodes = names.collect {childName->
            mono.getChild(childName)
        }
        if (!headerSent) {
            row = sh.createRow(cur_row)
            childNodes.each {childNode->
                def c = d(childNode.meta.stdName)
                def longName = childNode.meta.longName
                def remarks = childNode.meta.remarks ?: ''
                addComment(c, "$longName.\r\n$remarks")
            }
            endRow()
            headerSent = true
        }
        childNodes.each {childNode->
            d(childNode?.formattedValue)
        }
        endRow()
    }

    onEnd {
        wb.write(fout)
        wb.close()
        fout.close()
    }

}
