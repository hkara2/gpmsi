//☺:encoding=utf-8:
/**
 * injecter dans une feuille excel des données de RSF ACE (un onglet par type de facture)
 * arguments :
 * -a:input : fichier d'entree
 * -a:output : ficher de destination
 * -a:meta_hint : année de la version des métadonnées RSF-ACE
 *
 * Exemple :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2023\M01\RSF
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\hkchse\dev\chse-gpmsi\scripts\rsface-vers-xlsx.groovy -a:input RSF.txt -a:output rsf.xlsx -a:meta_hint 2023
 *
 * Le script utilise le SXSSFWorkbook qui ne garde qu'un nombre limité de
 * rangées en mémoire, ce qui n'est pas gênant pour nous puisque c'est juste
 * une recopie qui se fait, sans nécessité de retour en arrière.
 *
 * Attention le fichier .xlsx généré est assez énormes, quasiment aussi
 * gros que le RSF lui-même.
 */
import fr.karadimas.gpmsi.StringTable
import fr.karadimas.gpmsi.CsvDestination
import fr.karadimas.gpmsi.StringTransformable
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import java.text.SimpleDateFormat
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

def d(letter, val) {
    Cell c = row[letter].createCell(cur_col[letter]);
    c.setCellValue(val);
    c.setCellStyle(cs);
    cur_col[letter]++;
    return c
}

def endRow(letter) {
    cur_row[letter]++
    cur_col[letter] = 0
    row[letter] = sh[letter].createRow(cur_row[letter])
}

def addComment(letter, cell, commentText) {
    //
    CreationHelper factory = wb.getCreationHelper();

    ClientAnchor anchor = factory.createClientAnchor();
    //i found it useful to show the comment box at the bottom right corner
    anchor.setCol1(cell.getColumnIndex() + 0); //the box of the comment starts at this given column...
    anchor.setCol2(cell.getColumnIndex() + 5); //...and ends at that given column
    anchor.setRow1(cur_row[letter] + 0); //one row below the cell...
    anchor.setRow2(cur_row[letter] + 8); //...and 4 rows high

    Drawing drawing = sh[letter].createDrawingPatriarch();
    Comment comment = drawing.createCellComment(anchor);
    //set the comment text and author
    comment.setString(factory.createRichTextString(commentText));
    comment.setAuthor("PmsiXml");

    cell.setCellComment(comment);
}

sh = [:] //Map de Sheet, cles abchlmp (une clé par type de facture)

wb = null //the Workbook
cur_row = [:] //Map de cur_row, par type de facture
cur_col = [:] //Map de cur_col, par type de facture
row = [:] //objet "row" courant
cs = null //cell style

lettresRsf = ['a', 'b', 'c', 'h', 'l', 'm', 'p', ]
fdfSlashed = new SimpleDateFormat('dd/MM/yyyy')
dsordeb = null
dsorfin = null
headerSent = [a: false, b: false, c: false, h: false, l: false, m: false, p: false]

nLinesRead = 0

rsface {
    name 'Envoi des rsf-ace vers un fichier excel'
    input args['input']
    output args['output']
    metaHint args['meta_hint']

    onInit {
        def dsordebStr = args['dsordeb']
        def dsorfinStr = args['dsorfin']
        //dsordeb = fdfSlashed.parse(dsordebStr)
        //dsorfin = fdfSlashed.parse(dsorfinStr)
        wb = new SXSSFWorkbook(1000); // keep 1000 rows in memory, exceeding rows will be flushed to disk
        //wb = new XSSFWorkbook();
        fout = new FileOutputStream(outputFilePath);
        //creer autant d'onglets qu'il y a de lettres
        //et initialiser les compteurs par lettre
        lettresRsf.each {ltr->
            sh[ltr] = wb.createSheet(ltr)
            cur_row[ltr] = 0
            cur_col[ltr] = 0
            row[ltr] = null
        }
        cs = wb.createCellStyle();
    }

    onItem {item->
        def rsf = item.rsf
        def lettre = rsf.txtTENR.toLowerCase()
        def m = rsf.meta
        def cm = m.childMetas
        def names = cm*.stdName
        def vals = names.collect {childName->
            //println("$childName")
            // a faire : implementer le flag 'txt' pour avoir une sortie texte exclusive
            rsf.getChild(childName)?.formattedValue
        }
        def childNodes = names.collect {childName->
            rsf.getChild(childName)
        }
        if (!headerSent[lettre]) {
            //println('cur_row[' + lettre + ']' + cur_row[lettre])
            row[lettre] = sh[lettre].createRow(cur_row[lettre])
            childNodes.each {childNode->
                //println('cur_col[' + lettre + '] ' +cur_col[lettre])
                def c = d(lettre, childNode.meta.stdName)
                def longName = childNode.meta.longName
                def remarks = childNode.meta.remarks ?: ''
                addComment(lettre, c, "$longName.\r\n$remarks")
            }
            endRow(lettre)
            headerSent[lettre] = true
        }
        childNodes.each {childNode->
            d(lettre, childNode?.formattedValue)
        }
        endRow(lettre)

        //ps.addBatch()
        if (item.linenr % 100 == 0) {
            //ps.executeBatch()
            //afficher toutes les 100 lignes le numero de ligne courant
            print "\r${item.linenr}                                "
        }
        //println "name : $tableName"
        nLinesRead++
    }//onItem

    onEnd {
        //fini, effacer la ligne et mettre le nombre total de lignes lues
        println "\r  $nLinesRead lignes lues                       "
        wb.write(fout)
        wb.close()
        wb.dispose() //nettoyage des fichiers temporaires
        fout.close()
    }

}
