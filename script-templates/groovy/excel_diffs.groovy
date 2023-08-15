/**
 * Pour deux fichiers Excel A et B, ne garde dans B que les lignes qui ne sont pas dans A.
 * Ecrit le resultat dans le nom de B avec "_new" à la fin.
 * Ignore la première ligne de chaque fichier (s'assurer qu'elles sont identiques)
 * Utiliser pour extraire les nouvelles lignes depuis les envois cumulatifs.
 * Attention les anciennes lignes sont enlevées mais il reste une ligne vide,
 * cela permet de comparer le avant-après,
 * donc si besoin refaire un tri de tout le classeur avant de continuer le
 * traitement pour éliminer ces lignes vides.
 * Ex : 
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2023\M06\fichcomp-transports
 * c:\app\gpmsi\gpex -script C:\hkchse\dev\gpmsi\script-templates\groovy\excel_diffs.groovy -a:input_a C:\Local\e-pmsi\fichiers-rss-mco\2023\M05\fichcomp-transports\liste_transports_mod_hk_v1.xlsx -a:input_b liste_transports_mod_hk_v1.xlsx
 */
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.commons.io.FilenameUtils

def getValue(sheet, rowIndex, columnIndex, dataFormatter) {
    Row r = sheet.getRow(rowIndex)
    if (r == null) return ''
    Cell c = r.getCell(columnIndex)
    if (c == null) return ''
    else return dataFormatter.formatCellValue(c);
}

fA = args['input_a']

//charger le premier classeur A et garder en mémoire ses lignes
Workbook wbA = WorkbookFactory.create(new File(fA)); //same code for .xls and .xlsx

Sheet shA = wbA[0]

dataFormatter = new DataFormatter();

allARows = [] as Set

//Parcourir les rangées et colonnes existantes
for (Row row : shA) {
    StringBuilder sbRow = new StringBuilder()
    for (Cell cell : row) {
        int x = cell.getColumnIndex()
        int y = row.getRowNum()
        String val = getValue(shA, y, x, dataFormatter)
        //println "[$y,$x]:$val"
        sbRow << '|' << val
        //println sbRow.toString()
    }
    allARows << sbRow.toString()
}

//allARows.each { println it }


filenameB = args['input_b']
fileB = new File(filenameB)

//charger le deuxièmre classeur B et effacer chaque rangée que l'on trouve dans allARows
Workbook wbB = WorkbookFactory.create(fileB); //same code for .xls and .xlsx

Sheet shB = wbB[0]

dataFormatter = new DataFormatter();

int i = shB.lastRowNum

//Parcourir les rangées et colonnes existantes en sens inverse
while (i > 0) {
    Row row = shB.getRow(i)
    StringBuilder sbRow = new StringBuilder()
    for (Cell cell : row) {
        int x = cell.getColumnIndex()
        int y = row.getRowNum()
        String val = getValue(shB, y, x, dataFormatter)
        //println "[$y,$x]:$val"
        sbRow << '|' << val
        //println sbRow.toString()
    }
    if (allARows.contains(sbRow.toString())) shB.removeRow(row) //effacer la rangée si elle existe déjà
    i--
}

basenameB = FilenameUtils.getBaseName(filenameB)
extB = FilenameUtils.getExtension(filenameB)

filenameC = basenameB + '_new.' + extB

if (fileB.parent) fileC = new File(fileB.parent, filenameC)
else fileC = new File(filenameC)

try (FileOutputStream osC = new FileOutputStream(fileC)) {
  wbB.write(osC)
}
