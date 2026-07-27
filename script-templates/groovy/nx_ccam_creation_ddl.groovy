/**
 * Passer du fichier XML de métadonnées des tables CCAM (constitué à la main
 * à partir des fichiers PDF Ameli/CREDIRA) aux instructions DDL de type
 * CREATE TABLE.
 * Ce script est utilisé pour créer la resource correspondante dans la
 * librairie pmsixml
 * Exemple :
 * cd %GPMSI_BASE%\v@PROJECT_VERSION@\gpmsi\scripts\groovy
 * %GPMSI_BASE%\v@PROJECT_VERSION@\gpmsi -script nx_ccam_creation_ddl.groovy -a:input C:\hkgh\pmsixml\src\main\resources\fr\gpmsi\pmsixml\nx\tables-nx-DBF201301v9.xml -a:output C:\hkgh\pmsixml\src\main\resources\fr\gpmsi\pmsixml\nx\tables-nx-DBF201301v9_h2_create.ddl
 *
 */
import groovy.xml.XmlSlurper

infile = args.input
outfile = args.output

outp = new PrintWriter(outfile)
outp.println "-- genere a partir de $infile en utilisant le script creation_ddl_nx_ccam.groovy"
outp.println ""

xslu = new XmlSlurper()
rootNode = xslu.parse(new File(infile))

rootNode.TABLE.each {tableNode->
    outp.println "CREATE TABLE IF NOT EXISTS ${tableNode.@NAME} ("
    def cn = 0
    tableNode.COLUMN.each {colNode->
        def type = colNode.@TYPE
        def typeDecl
        switch (type) {
            case 'NUMBER':
              def prec = '0'
              def scale = '0'
              if (colNode.@PRECISION != "") prec = colNode.@PRECISION
              else println "Pas de PRECISION pour ${tableNode.@NAME} ${colNode.@NAME} !"
              if (colNode.@SCALE != "") scale = colNode.@SCALE
              if (scale == '0') typeDecl = "NUMERIC($prec)"
              else typeDecl = "NUMERIC($prec, $scale)"
            break;
            case 'DATE':
              typeDecl = 'DATE'
            break;
            case 'VARCHAR':
              def maxLen = '0'
              if (colNode.'@MAX-LENGTH' != "") maxLen = colNode.'@MAX-LENGTH'
                  else println "Pas de MAX-LENGTH pour ${tableNode.@NAME} ${colNode.@NAME} !"
              typeDecl = "VARCHAR($maxLen)"
            break;
        }
        if (cn > 0) outp.println ","
        outp.print "  ${colNode.@NAME} $typeDecl"
        cn++
    }
    outp.println ""
    outp.println ");"
    outp.println ""
    //emettre le commentaire de table
    def tableDesc = tableNode.@DESCRIPTION
    //doubler les apostrophes si besoin
    tableDesc = tableDesc.toString().replace("'", "''")
    outp.println "COMMENT ON TABLE ${tableNode.@NAME} IS '$tableDesc';"
    //emettre les commentaires de colonne
    tableNode.COLUMN.each {colNode->
        def colDesc = colNode.@DESCRIPTION
        //doubler les apostrophes si besoin
        colDesc = colDesc.toString().replace("'", "''")
        outp.println"  COMMENT ON COLUMN ${tableNode.@NAME}.${colNode.@NAME} IS '$colDesc';"
    }
    outp.println ""
}

if (outp.checkError() == true) System.err.println "Il y a eu des erreurs d'écriture du fichier de sortie !"
outp.close()
