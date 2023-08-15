/**:encoding=UTF-8:
 * Comparaison de deux fichiers textes dont la première ligne est identique
 * dans chaque fichier, et pour lesquels chaque ligne est unique (typiquement
 * deux fichiers .csv qui ont les mêmes colonnes).
 *
 * Pour deux fichiers de texte A et B (traités comme ensembles de lignes uniques),
 * produire :
 * un fichier de sortie A pour les lignes qui ne sont que dans A
 * un fichier de sortie B pour les lignes qui ne sont que dans B
 * un fichier de sortie AB pour les lignes qui sont à la fois dans A et B
 *
 * Arguments :
 * input_a : le fichier d'entrée A
 * input_b : le fichier d'entrée B
 * output_a : le fichier de sortie A
 * output_b : le fichier de sortie B
 * output_ab : le fichier de sortie AB
 *
 * Pour chaque fichier, la premiere ligne est l'en-tete.
 * Les deux en-tetes doivent etre identiques.
 * C'est l'en-tete de A qui est utilise pour le fichier de sortie AB
 *
 * Drapeaux :
 * notrim : ne pas enlever les espaces de début et fin de ligne
 *
 * Exemple :
 * cd C:\hkchse\dev\chse-gpmsi\scripts\tests\ensembles_a_b
 * gpmsi -script C:\hkchse\dev\chse-gpmsi\scripts\csv_comparer_ensembles.groovy -a:input_a a_lignes.txt -a:input_b b_lignes.txt -a:output_a a.txt -a:output_b b.txt -a:output_ab ab.txt
 * gpmsi -script C:\hkchse\dev\chse-gpmsi\scripts\csv_comparer_ensembles.groovy -a:input_a a_lignes.txt -a:input_b b_lignes.txt -a:output_a a.txt -a:output_b b.txt -a:output_ab ab.txt -f:notrim
 */

a_lines = [] as Set
b_lines = [] as Set
ab_lines = [] as Set
trim = true
a_header = ''
b_header = ''

line {
  name "Lecture des lignes de A"
  input args.input_a

  onInit {
      if (flags.contains('notrim')) trim = false
  }
  onItem { item ->
      def line = item.line
      if (trim) line = line.trim()
      if (item.linenr == 1) a_header = line
      else a_lines << line
  }
}

line {
  name "Lecture des lignes de B"
  input args.input_b

  onInit {
      if (flags.contains('notrim')) trim = false
  }
  onItem { item ->
      def line = item.line
      if (trim) line = line.trim()
      if (item.linenr == 1) b_header = line
      else b_lines << line
  }
  onEnd {
      if (a_header != b_header) {
          System.err.println("Attention pour le fichier A la colonne est '$a_header', pour le fichier B la colonne est '$b_header'")
      }
      ab_lines = a_lines.intersect(b_lines)
      a_only = a_lines - ab_lines
      b_only = b_lines - ab_lines
      a_out = new File(args.output_a)
      a_out.write "$a_header\r\n"
      a_only.each { a_out.append "$it\r\n" }
      b_out = new File(args.output_b)
      b_out.write "$b_header\r\n"
      b_only.each { b_out.append "$it\r\n" }
      ab_out = new File(args.output_ab)
      ab_out.write "$a_header\r\n"
      ab_lines.each { ab_out.append "$it\r\n" }
  }
}
