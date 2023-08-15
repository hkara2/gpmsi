/**:encoding=UTF-8:
 * Pour deux fichiers csv A et B qui ont une colonne qui donne l'identification,
 * différences entre les enregistrements.
 * Les colonnes doivent être les mêmes et leur ordre doit être identique.
 * Dans chaque fichier csv, il ne doit pas y avoir deux lignes avec le même ID.
 * Le fichier resultat comporte une premiere colonne supplementaire ab, qui
 * contient les codes suivants :
 * - +b : ligne qui n'existait pas dans A est apparue dans B
 * - a- : ligne qui existait dans A n'existe plus dans B
 * - == : ligne identique dans A et dans B (n'est envoyé que si le flag outeq est mis)
 * - !a : lignes avec même ID mais contenu différent, valeurs de A
 * - !b : lignes avec même ID mais contenu différent, valeurs de B
 * Arguments :
 * - a:input_a : fichier csv pour A
 * - a:input_b : fichier csv pour B
 * - a:id_col : nom de la colonne ID
 * - a:output : nom du fichier des différences
 * - f:outeq : output equals (sortir aussi les lignes ou les deux sont egaux)
 *
 * C:\t>c:\app\gpmsi\gpex -script c:\app\gpmsi\v1.2\scripts\groovy\csv_diffs.groovy -a:input_a fa.csv -a:input_b fb.csv -a:id_col y -a:output f_diff_ab.csv
 *
 */
import fr.karadimas.gpmsi.CsvDestination

def makeIndexByName(String[] names) {
    indexByName = [:]
    names.eachWithIndex { name, index -> indexByName[name] = index }
    return indexByName
}

a_values_by_id = [:]
a_keys = [] as Set
b_values_by_id = [:]
a_header_row = null
b_header_row = null
outEq = flags.contains('outeq')

id_col = args['id_col']

csv {
    name "Lecture fichier A"
    input args['input_a']

    onItem {item->
      if (item.linenr == 1) return //ne pas traiter la ligne 1
      row = item.row
      if (a_header_row == null) {
          a_header_row = csvHeaderRow
          a_indexesByName = makeIndexByName(a_header_row)
      }
      id = row."$id_col"
      values = row.values
      if (a_values_by_id.containsKey(id)) {
          throw new Exception("Valeur en double '$id' dans le fichier $inputFilePath")
      }
      a_values_by_id[id] = values
      a_keys << id
    }
}

csv {
    name "Lecture fichier B"
    input args['input_b']

    onInit {
        //construire le flux CSV de sortie
        outCsvDest = new CsvDestination(new File(args['output']), 'windows-1252', ';' as char)
        abcol = ['ab'] as String[]
        outHeaderRow = abcol + a_header_row
        outCsvDest.underlyingCSVWriter.writeNext(outHeaderRow, false) //envoyer la ligne d'en-tête en se servant des noms de A, avec guillemets si besoin seulement
    }

    onItem {item->
      if (item.linenr == 1) return //ne pas traiter la ligne 1
      row = item.row
      if (b_header_row == null) {
          b_header_row = csvHeaderRow
          b_indexesByName = makeIndexByName(b_header_row)
          abcol = ['ab'] as String[]
          out_header_row = abcol + b_header_row
          out_indexesByName = makeIndexByName(out_header_row)
      }
      id = row."$id_col"
      b_values = row.values
      if (b_values_by_id.containsKey(id)) {
          throw new Exception("Valeur en double '$id' dans le fichier $inputFilePath")
      }
      b_values_by_id[id] = b_values
      //maintenant, comparer.
      a_values = a_values_by_id[id]
      if (a_values == null) {
          //vient d'apparaitre
          abvals = ['+b'] as String[]
          outValues = abvals + b_values
          outCsvDest.underlyingCSVWriter.writeNext(outValues, false) //ecrire avec guillemets si besoin seulement
      }
      else {
          a_keys.remove(id)
          differs = b_header_row.any {
              a_val = a_values[a_indexesByName[it]]
              b_val = b_values[b_indexesByName[it]]
              a_val != b_val
          }
          if (differs) {
              //lignes differentes
              //ecrire valeurs de A
              abvals = ['!a'] as String[]
              outValues = abvals + a_values
              outCsvDest.underlyingCSVWriter.writeNext(outValues, false) //ecrire avec guillemets si besoin seulement
              //ecrire valeurs de B
              abvals = ['!b'] as String[]
              outValues = abvals + b_values
              outCsvDest.underlyingCSVWriter.writeNext(outValues, false) //ecrire avec guillemets si besoin seulement
          }
          else {
              //lignes identiques
              if (outEq) {
                  //ecrire juste valeurs de A
                  abvals = ['=='] as String[]
                  outValues = abvals + a_values
                  outCsvDest.underlyingCSVWriter.writeNext(outValues, false) //ecrire avec guillemets si besoin seulement
              }
          }
      }
    }

    onEnd {
        //ecrire les valeurs de A qui ont disparu
        a_keys.each { key ->
          a_values = a_values_by_id[key]
          //ecrire valeurs de A
          abvals = ['a-'] as String[]
          outValues = abvals + a_values
          outCsvDest.underlyingCSVWriter.writeNext(outValues, false) //ecrire avec guillemets si besoin seulement
        }
        outCsvDest.close()
    }
}



/*
 * Exemple avec résultat.
 * La colonne qui sert d'ID est la colonne y.
 * Dans A il y a jean (ID 1), jacques (ID 2), solange (ID 3)
 * Dans B il y a jeanne (ID 1), solange (ID 3), catherine (ID 4)
 * Entre A et B on voit que :
 * - pour l'ID 1 on a jean dans A, jeanne dans B
 * - jacques n'est plus dans B
 * - catherine n'était pas dans A, et est apparue dans B
 * - solange est à la fois dans A et B, avec y = 4 et z = 159 dans les deux fichiers
 *
 * Fichier A (fa.csv) :
 * x;y;z
 * jean;1;192
 * jacques;2;172
 * solange;3;159
 *
 * Fichier B (fb.csv) :
 * x;y;z
 * jeanne;1;192
 * solange;3;159
 * catherine;4;162
 *
 * Fichier f_diff_ab.csv, résultat de l'exécution de :
 * c:\app\gpmsi\gpex -script c:\app\gpmsi\v1.2\scripts\groovy\csv_diffs.groovy -a:input_a fa.csv -a:input_b fb.csv -a:id_col y -a:output f_diff_ab.csv
 *
 * ab;x;y;z
 * !a;jean;1;192
 * !b;jeanne;1;192
 * +b;catherine;4;162
 * a-;jacques;2;172
 *
 * Fichier f_diff_ab.csv, résultat de l'exécution de :
 * c:\app\gpmsi\gpex -script c:\app\gpmsi\v1.2\scripts\groovy\csv_diffs.groovy -a:input_a fa.csv -a:input_b fb.csv -a:id_col y -a:output f_diff_ab.csv -f:outeq
 *
 * ab;x;y;z
 * !a;jean;1;192
 * !b;jeanne;1;192
 * ==;solange;3;159
 * +b;catherine;4;162
 * a-;jacques;2;172
 */
