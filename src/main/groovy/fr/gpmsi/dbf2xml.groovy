/**
 * Script pour convertir une table DBF (encodage DOS, page de code 850) en un fichier XML,
 * qui comprend les métadonnées
 * arguments :
 * a:input fichier DBF en entrée
 * a:output fichier XML en sortie
 * "C:\t\pmsixml-nx\v83\CCAMTB08300\TB02.dbf"
 * "C:\t\pmsixml-nx\v83\CCAMTB08300\TB02.xml"
 */
package fr.gpmsi

import java.nio.charset.Charset
import java.time.format.DateTimeFormatter

import com.linuxense.javadbf.DBFBase
import com.linuxense.javadbf.DBFDataType
import com.linuxense.javadbf.DBFField
import com.linuxense.javadbf.DBFReader

import groovy.xml.MarkupBuilder

isoDf = DateTimeFormatter.ISO_DATE
isoDTf = DateTimeFormatter.ISO_DATE_TIME

/**
 * Conversion d'un objet lu par la librairie javadbf.
 * - Si l'objet est Date et le type est @ (timestamp) : formatage YYYY-MM-DD HH:MM:SS
 * - Si l'objet est Date et le type n'est pas @ (date normale) : formatage YYYY-MM-DD
 * - Si l'objet est null : ""
 * - Sinon, on prend sa représentation String "naturelle"
 * @param fieldObject L'objet
 * @param fieldLetter la lettre représentant le type de la colonne
 * @return La String qui correspond à l'objet
 */
def fieldToString(Object fieldObject, char fieldLetter) {
    if (fieldObject == null) return ''
    if (fieldObject instanceof Date) {
        Date d = (Date)fieldObject
        if (fieldLetter == '@') return d.toLocalDate().format(isoDTf) //dbx version 7, plus rare
        else return d.toLocalDate().format(isoDf)
    }
    return String.valueOf(fieldObject)
}

/**
 * Echappement (basique)
 * - si caractère | on envoie \|
 * - si caractère \ on envoie \\
 * - sinon on envoie le caractère tel quel
 * @param str La chaîne pour laquelle on veut faire l'échappement des caractères
 * @return La chaîne convertie
 */
def escape(String str) {
    StringBuffer sb = new StringBuffer()
    if (str == null) str = ""
    char[] ca = str.toCharArray()
    for (c in ca) {
        if (c == '\\') sb.append('\\\\')
        else if (c == '|') sb.append('\\|')
        else sb.append(c)
    }
    return sb.toString()
}

bvars = binding.variables
//println "Binding variables : $bvars"
inpStr = args.input
outpStr = args.output
println "Transformation de '$inpStr' en '$outpStr'"

dbfr = new DBFReader(new FileInputStream(inpStr), Charset.forName("Cp850"), false, false)

//lecture du nombre de colonnes (= champs en DBF)
numberOfFields = dbfr.getFieldCount();
fieldLetters = new char[numberOfFields]

xmlWriter = new OutputStreamWriter(new FileOutputStream(outpStr), 'UTF-8')
xmlWriter.println('<?xml version="1.0" encoding="utf-8" ?>')

class FieldMeta {
    String name
    String code
    String minSize
    String defSize
    String maxSize
}

metas = []

// use this count to fetch all field information
// if required
for (int i = 0; i < numberOfFields; i++) {

    DBFField field = dbfr.getField(i);

    // do something with it if you want
    // refer the JavaDoc API reference for more details
    //
    //print(field.getName());
    DBFDataType ftype = field.getType()
    char typeCharCode = ftype.getCharCode()
    fieldLetters[i] = typeCharCode
    int minSize = ftype.getMinSize()
    int defSize = ftype.getDefaultSize()
    int maxSize = ftype.getMaxSize()
    //println " $typeCharCode $minSize $defSize $maxSize"
    metas << new FieldMeta(name: field.getName(), code: '' + typeCharCode, minSize: ""+minSize, defSize: ""+defSize, maxSize: ""+maxSize)
}

//emettre maintenant les enregistrements, en un format relativement compact (pour du XML)
//le séparateur est "|", le caractère d'échappement est "\"
Object[] fields;
records = []

while ((fields = dbfr.nextRecord()) != null) {
    StringBuffer sb = new StringBuffer()
    for (int i = 0; i < fields.length; i++) {
        if (i > 0) sb.append('|')
        def r = escape(fieldToString(fields[i], fieldLetters[i]))
        sb.append(r)
    }
    records << sb.toString()
    //println "${sb.toString()}"
}

MarkupBuilder xmlMarkup = new MarkupBuilder(xmlWriter)
xmlMarkup.DBF {
    FIELDS() {
        metas.eachWithIndex {m, i->
            FIELD(ID: i+1, NAME: m.name, CODE: m.code, MIN_SIZE: m.minSize, DEF_SIZE: m.defSize, MAX_SIZE: m.maxSize)
        }
    }
    DATA() {
        records.eachWithIndex {rec, i->
            R(ID: i+1, rec)
        }
    }
}
