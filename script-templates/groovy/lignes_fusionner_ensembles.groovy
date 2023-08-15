/**:encoding=UTF-8:
 * Pour deux fichiers csv A et B (traités comme ensembles de lignes uniques),
 * produire un fichier qui est la fusion de A et de B.
 *
 * Arguments :
 * input_a : le fichier d'entrée A
 * input_b : le fichier d'entrée B
 * output : le fichier fusionne
 *
 * La premiere ligne de chaque fichier doit etre l'en-tête
 * Cette premiere ligne doit etre la meme dans A et dans B
 * La premiere ligne de A est utilisee comme en-tête du fichier fusionné.
 *
 * Drapeaux :
 * notrim : ne pas enlever les espaces de début et fin de ligne
 *
 * Exemple :
 * cd C:\hkchse\dev\chse-gpmsi\scripts\tests\ensembles_a_b
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\lignes_fusionner_ensembles.groovy -a:input_a a_lignes.txt -a:input_b b_lignes.txt -a:output ab_fusion.txt
 */

ab_lines = [] as Set
trim = true
a_col = ''
b_col = ''

line {
  name "Lecture des lignes de A"
  input args.input_a
  
  onInit {
      if (flags.contains('notrim')) trim = false
  }
  onItem { item ->
      def line = item.line
      if (trim) line = line.trim()
      if (item.linenr == 1) a_col = line
      else ab_lines << line
  }
}

line {
  name "Lecture des lignes de B"
  input args.input_b
  output args.output
  
  onInit {
      if (flags.contains('trim')) trim = true
  }
  onItem { item ->
      def line = item.line
      if (trim) line = line.trim()
      if (item.linenr == 1) b_col = line
      else ab_lines << line
  }
  onEnd {
      if (a_col != b_col) {
          System.err.println("Attention pour le fichier A la colonne est '$a_col', pour le fichier B la colonne est '$b_col'")
      }
      ab_out = new File(outputFilePath)
      ab_out.write "$a_col\r\n"
      ab_lines.each { ab_out.append "$it\r\n" }
  }
}


