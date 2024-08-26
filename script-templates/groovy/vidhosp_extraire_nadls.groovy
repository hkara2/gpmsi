/**☺:encoding=UTF-8:
 *
 * Dans un VIDHOSP extraire les NADL (appelés aussi IEP ou NDA).
 * Arguments :
 * input : le chemin du fichier qui contient les VIDHOSP
 * output : un fichier texte avec NADL en première ligne puis un NADL par ligne.
 * Exemple d'exécution :
  c:\app\gpmsi\v1.3\gpmsi.bat ^
  -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\vidhosp_extraire_nadls.groovy ^
  -a:input vh.txt
  -a:output vh_nadls.csv
 *
 * Harry Karadimas 2024, CHSE
 */
import fr.karadimas.gpmsi.CsvDestination

vidhosp {
  name 'extraire les NADLs'
  input args['input']
  output args['output']

  headerEmitted = false

  onInit {
      outf = new CsvDestination(new File(outputFilePath), "cp1252")
      outf.f 'NADL' //envoyer l'en-tête
      outf.endRow()
  }

  onItem {item->
    def vh = item.vidhosp
    outf.f vh.txtNADL
    outf.endRow()
  }

  onEnd {
    outf.close()
    println "Fin de l'execution."
  }
}
