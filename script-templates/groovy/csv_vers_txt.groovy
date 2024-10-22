/**:encoding=UTF-8:
 * Convertir un fichier .csv en fichier texte, en prenant la taille la plus
 * grande de chaque colonne. Attention ne marche pas pour les fichiers .csv
 * avec des colonnes multilignes !
 * La sortie est toujours alignée à droite quel que soit le type de la colonne.
 *
 * Arguments :
 * input : le fichier d'entrée csv. Si non trouvé en fichier, sera recherché en resource dans fr.gpmsi.pmsixml ou à la racine.
 * output : le fichier de sortie txt
 *
 * Exemple :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\csv_vers_txt.groovy -a:input monfichier.csv -a:output monfichier.txt
 * Exemple de lecture d'une métadonnée en resource :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\csv_vers_txt.groovy -a:input fichcompdmi2020.csv -a:output fichcompdmi2020.txt
 *
 */
import fr.gpmsi.pmsixml.MetaFileLoader

cols_max_sizes = null
outf = null

//on lit à partir d'un "reader", soit à partir du système de fichier (cp1252 sur Windows)
//soit à partir d'une "resource" en UTF-8 (on suppose que c'est pour lire une
//métadonnées pmsixml)

def inputAsReader() {
    inputFile = new File(args.input)
    if (inputFile.exists()) {
        inRd = new FileReader(inputFile)
    }
    else {
        def resourcePath1 = "/fr/karadimas/pmsixml/" + args.input
        def inStream = MetaFileLoader.getResourceAsStream(resourcePath1)
        if (inStream == null) {
            //faire un nouvel essai avec la resource à la racine (anciennes versions de pmsixml)
            def resourcePath2 = "/" + args.input
            inStream = MetaFileLoader.getResourceAsStream(resourcePath2)
            if (inStream == null) {
                throw new Exception("Resource non trouvee, ni dans : '$resourcePath1', ni dans '$resourcePath2'")
            }
        }
        inRd = new InputStreamReader(inStream, 'UTF-8')
    }
}

inRd = inputAsReader()

csv {
  name "mesure des colonnes"
  inputReader inRd

  onItem { item ->
      def row = item.row
      if (cols_max_sizes == null) cols_max_sizes = new int[row.columnCount]
      //mesurer et adapter les tailles max des colonnes
      for (int i = 0; i < row.columnCount; i++) {
          cols_max_sizes[i] = Math.max(cols_max_sizes[i], row[i].length())
      }
  }
}

inRd = inputAsReader()

csv {
  name "Relecture du fichier et emission"
  inputReader inRd
  output args.output

  onInit {
      outf = new FileWriter(outputFilePath)
  }
  onItem { item ->
      def row = item.row
      sb = new StringBuilder()
      //émettre chaque colonne en mettant autant d'espaces que nécessaire à chaque fois
      for (int i = 0; i < row.columnCount; i++) sb << row[i].padLeft(cols_max_sizes[i]) << ' '
      sb << '\r\n'
      outf.write(sb.toString())
  }
  onEnd {
      outf.close()
  }

}


