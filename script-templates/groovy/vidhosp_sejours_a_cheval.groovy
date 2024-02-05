/**☺:encoding=UTF-8:
 *
 * Dans un VIDHOSP sortir les NADL (appelés aussi IEP ou NDA) pour lesquels
 * l'année d'entrée est différente de l'année de sortie.
 * Arguments :
 * input : le chemin du fichier qui contient les VIDHOSP
 * output : un fichier texte avec NADL en première ligne puis un NADL par ligne.
 * Exemple d'exécution :
  c:\app\gpmsi\v1.3\gpmsi.bat ^
  -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\vidhosp_sejours_a_cheval.groovy ^
  -a:input vh.txt ^
  -a:output sej-a-cheval.csv
 *
 * (C) Harry Karadimas 2024, CHSE
 */
import fr.karadimas.gpmsi.CsvDestination

/** Extraire l'année d'un fszField. Si null, ramène 0. */
def extraireAnnee(f) {
    d = f.toLocalDate()
    d ? d.year : 0
}

vidhosp {
  name 'Trouver les sejours a cheval'
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
    def anneeEntree = extraireAnnee(vh.DENT)
    def anneeSortie = extraireAnnee(vh.DSOR)
    if (anneeEntree == anneeSortie) return //sortir si entrée et sortie sont la même année

    outf.f vh.txtNADL
    //outf.f vh.DENT
    //outf.f vh.DSOR
    outf.endRow()
  }

  onEnd {
    outf.close()
    println "Fin de l'execution."
  }
}
