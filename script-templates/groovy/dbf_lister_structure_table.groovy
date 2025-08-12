/**:encoding=UTF-8:
 * Lister la structure d'une table DBF passée en paramètre.
 * C'est une application du code exemple de https://github.com/albfernandez/javadbf/tree/main
 * Exemple :
 * gpmsi -script "C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\dbf_lister_structure_table.groovy" -a:fichier_dbf R_ACTE_FORFAIT.dbf
 * Pour lister tous les dbf d'un repertoire sous CMD / Windows dans un fichier metadonnees_dbf.txt (par exemple pour la CCAM ...) :
 * for %I in (*.dbf) do c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi.bat -script "C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\dbf_lister_structure_table.groovy" -a:fichier_dbf %I >>metadonnees_dbf.txt
 */
import com.linuxense.javadbf.*;

fichier_dbf = args['fichier_dbf']

DBFReader reader = null;
try {

    // create a DBFReader object
    reader = new DBFReader(new FileInputStream(fichier_dbf))

    println("$fichier_dbf :")
    // get the field count if you want for some reasons like the following

    int numberOfFields = reader.getFieldCount()

    println("  $numberOfFields colonnes")
    
    // use this count to fetch all field information
    // if required

    for (int i = 0; i < numberOfFields; i++) {

        DBFField field = reader.getField(i)

        // do something with it if you want
        // refer the JavaDoc API reference for more details
        //
        String fieldName = field.name
        int fieldLength = field.length
        int fieldDecimalCount = field.decimalCount
        def fieldType = field.type
        println("  $fieldName $fieldType($fieldLength, $fieldDecimalCount)");
    }

    // Now, lets us start reading the rows

    DBFRow row;

    int rowCount = 0;
    while ((row = reader.nextRow()) != null) {
        //System.out.println(row.getString("PHONE"));
        rowCount++
    }

    // By now, we have iterated through all of the rows
    int declaredRowCount = reader.recordCount
    println("  $rowCount enregistrements ($declaredRowCount declares)")

} catch (DBFException e) {
    e.printStackTrace();
} catch (IOException e) {
    e.printStackTrace();
}
finally {
    DBFUtils.close(reader);
}


