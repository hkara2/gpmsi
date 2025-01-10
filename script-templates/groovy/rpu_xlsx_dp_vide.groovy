/**:encoding=UTF-8:
 * Sur un fichier xlsx contenant des RPUs, ne garder que les lignes où la
 * colonne "dp" est vide.
 * Arguments :
 * -a:input fichier_xlsx_a_filtrer
 * -a:output fichier_xlsx_resultat
 * cd H:\partage_intersite\ADMINISTRATION\DIM\URGENCES\RPU\2024\M12\au-250110
 * c:\app\gpmsi\v2.0\gpmsi.bat -script c:\app\gpmsi\v2.0\scripts\groovy\rpu_xlsx_dp_vide.groovy -a:input RPU_3702_20250110122543_test.xlsx -a:output RPU_3702_20250110122543_test_dp_vide.xlsx
 */
import static fr.gpmsi.StringUtils.isEmpty
import fr.gpmsi.poi.PoiHelper
import fr.gpmsi.poi.XlsxHelper
import fr.gpmsi.poi.ValueWrapper
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.CellCopyPolicy

//Copier les valeurs d'une rangée dans le XlsxHelper, et passer à la rangée suivante
def copyRowCells(srcRow, xh, firstCellNum, lastCellNum) {
    for (int i = firstCellNum; i <= lastCellNum; i++) {
        def srcCell = srcRow.getCell(i)
        //def destCell = destRow.getCell(i)
        //if (destCell == null) destCell = destRow.createCell(i)
        //"copyCellFrom" est séduisant, mais pour l'instant déclenche une exception :
        //Exception in thread "main" groovy.lang.MissingMethodException: No signature of method: org.apache.poi.xssf.streaming.SXSSFCell.copyCellFrom() is applicable for argument types: (org.apache.poi.xssf.usermodel.XSSFCell, org.apache.poi.ss.usermodel.CellCopyPolicy) values: [naissance, org.apache.poi.ss.usermodel.CellCopyPolicy@14ef2482]
        //destCell.copyCellFrom(srcCell, ccPolicy) //cf. https://poi.apache.org/apidocs/5.0/org/apache/poi/xssf/usermodel/XSSFCell.html#copyCellFrom-org.apache.poi.ss.usermodel.Cell-org.apache.poi.ss.usermodel.CellCopyPolicy-

        //Ne fonctionne pas :
        //def cellValue = ph.getCellValueAsObject(srcCell)
        //ph.setCellValueFromObject(destCell, cellValue)

        def vw = new ValueWrapper(srcCell)
        xh.addCell(vw)
    }//for
    xh.newRow() //finir la rangée
}

ph = new PoiHelper() //nécessaire car sinon erreurs avec getCellValueAsObject !!

inputFile = new File(args.input)
wb = WorkbookFactory.create(inputFile)
outputFile = new File(args.output)

//lire les noms des colonnes (rangée 0)
colNames = []
sheet0 = wb.getSheetAt(0)
row0 = sheet0.getRow(0)
lastRowNum = sheet0.getLastRowNum()
firstCellNum = row0.getFirstCellNum()
lastCellNum = row0.getLastCellNum()
dpColNum = -1

for (int i = firstCellNum; i <= lastCellNum; i++) {
    def cell = row0.getCell(i)
    def colName = cell?.stringCellValue
    if (colName == null) colName = ''
    colNames << colName
    if (colName.equalsIgnoreCase('dp')) dpColNum = i
}

//println "columns: $colNames"
//println "firstCellNum : $firstCellNum, lastCellNum : $lastCellNum"

if (dpColNum <= 0) throw new Exception("Pas de colonne 'dp' trouvée")

//Méthode 1 :
//parcourir les rangées (à l'envers) et enlever les rangées qui n'ont pas un
//DP vide
/* méthode abandonnée car extrêmement lente pour des fichiers qui dépassent les 10000 lignes
for (int i = lastRowNum; i > 0; i--) {
    def row = sheet0.getRow(i)
    def cell = row.getCell(dpColNum)
    def val = cell.stringCellValue
    if (!isEmpty(val)) {
        sheet0.removeRow(row) //effacement de la rangée
        if (i < lastRowNum) {
            println "i: $i,lastRowNum: $lastRowNum"
            sheet0.shiftRows(i + 1, lastRowNum, -1); //et décalage pour enlever la rangée vide
        }
    }
}
*/


boolean t = true
boolean f = false
ccPolicy = new CellCopyPolicy().createBuilder().
    condenseRows(t).
    cellFormula(f).
    cellStyle(f).
    cellValue(t).
    copyHyperlink(f).
    mergedRegions(f).
    rowHeight(f).
    mergeHyperlink(f).
    build()

//ecrire le fichier résultat
xh = new XlsxHelper("dp_vide")

titlesRow = sheet0.getRow(0)
copyRowCells(titlesRow, xh, firstCellNum, lastCellNum)

//copier les rangées (en commencant à 1 puisqu'on vient de copier la rangée 0)
for (int i = 1; i <= lastRowNum; i++) {
    def srcRow = sheet0.getRow(i)
    //def destRow = xh.getSheet().createRow(i)
    def cell = srcRow.getCell(dpColNum)
    def val = cell.stringCellValue
    if (isEmpty(val)) {
        //pas de dp, on copie
        copyRowCells(srcRow, xh, firstCellNum, lastCellNum)
        //sheet0.removeRow(row) //effacement de la rangée
        //if (i < lastRowNum) {
            //println "i: $i,lastRowNum: $lastRowNum"
            //sheet0.shiftRows(i + 1, lastRowNum, -1); //et décalage pour enlever la rangée vide
        //}
    }
}


xh.setOutput(outputFile);
xh.writeFileAndClose();


