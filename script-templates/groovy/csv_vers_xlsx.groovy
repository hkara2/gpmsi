/**:encoding=utf-8:
 * Transformer de manière générique un fichier CSV fichier Excel, à l'aide 
 * d'une table de typage des colonnes.
 * La table de typage doit comporter les colonnes suivantes :
 * - NOMCOL : le nom de la colonne
 * - TYPECOL : le type de la colonne
 * - JAVAFORMAT : optionnel, la chaîne de formatage pour java
 * - EXCELFORMAT : optionnel, la chaine de formatage pour excel
 * - LARG : la largeur de la colonne en 256èmes de taille de caractères. Pour 
 *       Calibri 11, prendre le nombre de caractères désirés et multiplier par
 *       284. Si la colonne est vide ou à 0, la largeur n'est pas touchée et
 *       à la valeur par défaut.
 *
 * Les types disponibles sont :
 * B : booleen
 * N : numerique
 * T : texte
 * D : date
 * DT : horodatage (date + time)
 * DU : dUrée (inférieur à 24 heures)
 *
 * Si vous avez des durées Supérieures à 24 heures, il vaut mieux utiliser le type texte
 *
 * Optionnellement on peut mettre le format java désiré pour le type
 * - pour B : les valeurs pour VRAI, un espace, les valeurs pour FAUX. Par défaut : 0,F,FALSE,FAUX,,N 1,T,TRUE,VRAI,Y
 * - pour N : le motif à utiliser (cf. DecimalFormat). Ex : N 0.0
 * - pour D : le motif à utiliser (cf. SimpleDateFormat). Par défaut dd/MM/yyyy
 * - pour DT : le motif à utiliser (cf. SimpleDateFormat). Par défaut dd/MM/yyyy HH:mm:ss
 * - pour DU : le motif à utiliser (cf. SimpleDateFormat). Par défaut HH:mm:ss
 *
 * Optionnellement on peut mettre le format excel désiré pour le type
 * - pour D : par défaut "d/m/yy"
 * - pour DT : par défaut "d/m/yy h:mm:ss"
 * - pour DU : par défaut "h:mm:ss"
 *
 * Faire des tests +++ et en cas de doute utiliser le type texte (T) qui ne
 * modifiera pas le contenu qui est lu dans le csv.
 *
 * arguments :
 * -a:input chemin_du_fichier_csv_a_convertir
 * -a:table_types chemin_du_fichier_csv_avec_correspondance_colonne_type
 * -a:output chemin_du_fichier_xlsx_en_sortie
 * -a:titre titre_de_la_feuille (si absent, le titre est "Feuil1")
 *
 * Exemple d'exécution :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2021\M12\RSA
 * c:\app\gpmsi\v1.3\gpmsi -script c:\app\gpmsi\v1.3\scripts\groovy\csv_vers_xlsx.groovy -a:input in\csv_vers_xlsx_donnees1.csv -a:table_types in\csv_vers_xlsx_types1.csv -a:output tmp-out\csv_vers_xlsx_donnees1.xlsx
 *
 * #240624 hk Création du fichier
 */

import fr.gpmsi.CsvDestination
import fr.gpmsi.poi.XlsxHelper
import fr.gpmsi.StringTable
import org.apache.poi.xssf.streaming.SXSSFCell
import java.text.SimpleDateFormat
import java.text.NumberFormat
import java.text.DecimalFormat
import groovy.transform.AutoClone

@AutoClone
class TypeCol {
    String nomType
    String formatJava
    String formatExcel
    SimpleDateFormat formatteur
    DecimalFormat formatteurDec
    Set<String> textesVrai
    Set<String> textesFaux

    TypeCol(String nomType, String formatJava, String formatExcel) {
        this.nomType = nomType
        this.formatJava = formatJava
        this.formatExcel = formatExcel
    }
    Object parse(String txt) {
        switch (nomType.toUpperCase()) {
            case 'B':
            if (textesVrai == null) {
                if (formatJava == null) formatJava = '1,T,TRUE,V,VRAI,Y,YES,O,OUI 0,F,FALSE,FAUX,,N,NO,NON'
                def (strVrais, strFaux) = formatJava.tokenize(' ')
                textesVrai = strVrais.tokenize(',') as Set
                textesFaux = strFaux.tokenize(',') as Set
            }
            if (txt == "") return null
            if (textesVrai.contains(txt.toUpperCase())) return Boolean.TRUE
            if (textesFaux.contains(txt.toUpperCase())) return Boolean.FALSE
            println "Valeur '"+txt+"' ni vraie ni fausse, retour de FALSE"
            return Boolean.FALSE
            case 'N':
            if (txt == "") return null
            if (formatteurDec == null) formatteurDec = new DecimalFormat('############.############E000')
            //java ne connait pas "E+", il faut donc le remplacer par "E"
            txt = txt.toUpperCase().replaceAll('E\\+', 'E')
            //pour l'instant on fait une conversion standard
            //println "Conversion nombre '"+txt+"' : '"+formatteurDec.parse(txt)+"'"
            return formatteurDec.parse(txt)
            case 'D':
            if (formatteur == null) {
                if (formatJava == null) formatJava = 'dd/MM/yyyy'
                formatteur = new SimpleDateFormat(formatJava)
            }
            if (txt == "") return null
            return formatteur.parse(txt)
            case 'DT':
            if (formatteur == null) {
                if (formatJava == null) formatJava = 'dd/MM/yyyy HH:mm:ss'
                formatteur = new SimpleDateFormat(formatJava)
            }
            if (txt == "") return null
            return formatteur.parse(txt)
            case 'DU':
            if (formatteur == null) {
                if (formatJava == null) formatJava = 'HH:mm:ss'
                formatteur = new SimpleDateFormat(formatJava)
            }
            if (txt == "") return null
            return formatteur.parse(txt)
            case 'T':
            //fall through
            default:
            return txt
        }
    }//parse

    SXSSFCell addCell(XlsxHelper xh, Object val) {
        SXSSFCell c
        if (val == null) c = xh.addCell((String)null)
        else if (val instanceof Boolean) c = xh.addCell(((Boolean)val).booleanValue())
        else if (val instanceof Number) c = xh.addCell(((Number)val).doubleValue())
        else if (val instanceof Date) c = xh.addCell((Date)val)
        else c = xh.addCell(val.toString())
        if (formatExcel != null && formatExcel != "") xh.adjustCellStyle(c, formatExcel)
        return c
    }
}

title = args.titre
if (title == null || title == "") title = "Feuil1"

typeB = new TypeCol('B', '1,V,VRAI,T,TRUE,Y,YES,O,OUI 0,F,FAUX,FALSE,N,NO,NON', '')
typeN = new TypeCol('N', '', '')
typeT = new TypeCol('T', '', '')
typeD = new TypeCol('D', 'dd/MM/yyyy', 'd/m/yyyy')
typeDT = new TypeCol('DT', 'dd/MM/yyyy HH:mm:ss', 'd/m/yyyy h:mm:ss')
typeDU = new TypeCol('DU', 'HH:mm:ss', 'h:mm:ss')

classeur = new XlsxHelper(title);

typesParNomCol = [:]

types = []

//lire types
tableTypes = new StringTable('TYPES', new File(args.table_types))

tableTypes.eachWithIndex {row,ix->
    def nom = row.NOMCOL
    def typ = row.TYPECOL
    def jfmt = row.JAVAFORMAT
    def xfmt = row.EXCELFORMAT
    def colwStr = row.LARG
    if (colwStr == null) colwStr = ""
    if (colwStr == "") colwStr = "0"
    def colw = Integer.valueOf(colwStr)
    TypeCol tc = null
    switch (typ) {
        case 'B' : tc = typeB.clone(); break
        case 'N' : tc = typeN.clone(); break
        case 'D' : tc = typeD.clone(); break
        case 'DT' : tc = typeDT.clone(); break
        case 'DU' : tc = typeDU.clone(); break
        case 'T' :
        //fall through
        default :
        tc = typeT.clone()
    }
    if (jfmt != "") tc.formatJava = jfmt
    if (xfmt != "") tc.formatExcel = xfmt
    types << tc
    typesParNomCol[nom] = tc
    if (colw > 0) classeur.sheet.setColumnWidth(ix, colw)
}

csv {
    name 'Transformation générique csv en Excel'

    input args['input']
    output args['output']

    onInit {
    }

    onItem {item->
        def linenr = item.linenr
        def row = item.row
        if (linenr == 1) {
            row.values.each { classeur.addCell(it) }
            classeur.newRow()
            return
        }
        types.eachWithIndex {typ,i->
            typ.addCell(classeur, typ.parse(row[i]))
        }
        classeur.newRow()
    }//onItem

    onEnd {
        File destFile = new File(outputFilePath);
        classeur.setOutput(destFile);
        classeur.writeFileAndClose();
    }
}
