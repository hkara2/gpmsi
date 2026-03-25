/**:encoding=UTF-8:
 * Correction de RSS de version 122 en version 123
 * Recopie les champs en utilisant les métadonnées.
 * Il n'y a pas de différence de contenu entre 122 et 123, ce sont juste des
 * espaces supplémentaires réservés qui ont été ajoutés.
 * Parametres :
 * input : fichier en entree
 * output : fichier en sortie
 * Exemple de lancement :
 *
 * C:\Local\e-pmsi\fichiers-rss-mco\2022\M03\220507\DXC>c:\app\gpmsi\v@PROJECT_VERSION@\scripts\gpmsi.bat -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rss-120-vers-121.groovy -a:input RSSG_120.txt -a:output RSSG_transforme_121.txt
 */
import java.text.SimpleDateFormat

import fr.gpmsi.pmsixml.RssWriter
import fr.gpmsi.pmsixml.MetaFileLoader

df = new SimpleDateFormat('ddMMyyyy')

dateSeuilVersion = df.parse('01012026') //date de passage au RSS 123/023

dateSortieHopParNadl = [:]

rss {
  name 'Parcours des RSS pour date sortie hop'

  input args['input']

  onItem {item->
    //récupérer max de date sortie UM ce sera la date sortie hopital
    def rum = item.rum
    def dsum = rum.DSUM.toDate()
    def nadl = rum.txtNADL
    def dshop = dateSortieHopParNadl[nadl]
    if (dshop == null) dshop = dsum
    else if (dsum.after(dshop)) dshop = dsum
    dateSortieHopParNadl[nadl] = dshop
  }

}

rss {
  name 'Conversion des RSS de v.122 en v.123'

  input args['input']
  output args['output']

  onInit {
    println "Debut analyse ${inputFilePath}"
    fout = new FileWriter(outputFilePath)
    rsswr = new RssWriter()
    mfl = new MetaFileLoader()
    metaRSS123 = mfl.loadMeta('/fr/gpmsi/pmsixml/rss123.csv') //récupérer les métadonnées pour un rss de type 123
  }

  onItem {item->
    def rum = item.rum
    def vrss = rum.txtVRSS
    def vrum = rum.txtVRUM
    def nadl = rum.txtNADL
    def dshop = dateSortieHopParNadl[nadl]
    if (vrss == '122' && vrum == '022' && (dshop == dateSeuilVersion || dshop.after(dateSeuilVersion))) {
      //créer un nouveau RSS format 123
      def sb = new StringBuffer()
      //rhswr.writeRhs(rhs, sb)
      //sb << "\r\n"
      def rum_tr = metaRSS123.getFirstChildGroupMeta().makeNewNode() //créer un RSS vide de type RSS123
      rum.copyFieldsTo(rum_tr) //faire la recopie de tous les champs qui existent dans l'ancienne version
      rum_tr.VRSS.value = '123' //changer valeur pour la version "RSS"
      rum_tr.VRUM.value = '023' //changer valeur pour la version de "RUM"
      rsswr.writeRss(rum_tr, sb) //ecrire le RUM résultant
      fout.write(sb.toString())
      fout.write('\r\n')
    }
    else {
      //envoyer la ligne telle quelle, ne rien changer
      fout.write(item.line)
      fout.write('\r\n')
    }
  }

  onEnd {
    println "Fin analyse   ${inputFilePath}."
    fout.close()
  }
}

