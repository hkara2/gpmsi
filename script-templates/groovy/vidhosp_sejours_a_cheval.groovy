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
 * Drapeau supplementaire pour ajouter les colonnes DENT et DSOR pour dates entree et sortie de l'hôpital :
 * -f:avecdates
 *
 * (C) Harry Karadimas 2024, CHSE
 */
import fr.gpmsi.CsvDestination

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
      avecdates = flags.contains('avecdates')
      outf = new CsvDestination(new File(outputFilePath), "cp1252")
      outf.f 'NADL' //envoyer l'en-tête
      if (avecdates) {
        outf.f 'DENT'
        outf.f 'DSOR'
      }
      outf.endRow()
  }

  onItem {item->
    def vh = item.vidhosp
    def anneeEntree = extraireAnnee(vh.DENT)
    def anneeSortie = extraireAnnee(vh.DSOR)
    if (anneeEntree == anneeSortie) return //sortir si entrée et sortie sont la même année

    outf.f vh.txtNADL
    if (avecdates) {
      outf.f formatAsFrenchDate(vh.DENT.toDate())
      outf.f formatAsFrenchDate(vh.DSOR.toDate())
    }
    outf.endRow()
  }

  onEnd {
    outf.close()
    println "Fin de l'execution."
  }
}
