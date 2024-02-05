/**:encoding=UTF-8:
 * Pour deux fichiers csv A et B (qui ont les mêmes colonnes dans le même ordre),
 * produire un fichier qui est la concatenation de A et de B.
 *
 * Arguments :
 * input_a : le fichier d'entrée A
 * input_b : le fichier d'entrée B
 * output : le fichier de la réunion de A et B
 *
 * La premiere ligne de chaque fichier doit etre l'en-tête
 * Cette premiere ligne doit etre la meme dans A et dans B (si ce n'est pas le
 * cas le script envoie un avertissement)
 * La premiere ligne de A est utilisee comme en-tête du fichier réuni.
 *
 * Drapeaux :
 * notrim : ne pas enlever les espaces de début et fin de ligne
 * noempty : ne pas envoyer les lignes vides (ou qui ne contiennent que des espaces ou tabulations)
 *
 * Exemple :
 * cd C:\hkchse\dev\chse-gpmsi\scripts\tests\ensembles_a_b
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\lignes_fusionner_ensembles.groovy -a:input_a a_lignes.txt -a:input_b b_lignes.txt -a:output ab_fusion.txt
 */

ab_lines = [] as Set
trim = true
noempty = false
a_col = ''
b_col = ''

ab_out = null

line {
  name "Lecture des lignes de A"
  input args.input_a
  output args.output

  onInit {
      ab_out = new FileWriter(outputFilePath)
      if (flags.contains('notrim')) trim = false
  }
  onItem { item ->
      def line = item.line
      if (trim) line = line.trim()
      if (item.linenr == 1) a_cols = line
      def canEmit = noempty == false || line.trim().length() > 0
      if (canEmit) ab_out.write "$line\r\n"
  }
}

line {
  name "Lecture des lignes de B"
  input args.input_b

  onInit {
      if (flags.contains('notrim')) trim = false else trim = true
  }
  onItem { item ->
      def line = item.line
      if (trim) line = line.trim()
      if (item.linenr == 1) {
          b_cols = line
          if (a_cols != b_cols) {
              System.err.println("Attention pour le fichier A les colonnes sont \n'$a_cols'\n, pour le fichier B les colonnes sont \n'$b_cols'")
          }
      }
      else {
          def canEmit = noempty == false || line.trim().length() > 0
          if (canEmit) ab_out.write "$line\r\n"
      }
  }
  onEnd {
      ab_out.close()
  }
}


