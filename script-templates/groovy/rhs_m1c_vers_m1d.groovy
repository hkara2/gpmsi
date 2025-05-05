/**:encoding=UTF-8:
 * Correction de RHS de version M1C en version M1D
 * Recopie les champs en utilisant les métadonnées.
 *
 * https://www.atih.sante.fr/fonction-groupage-smr-v2025
 * Pour la FG-SMR v2025, voici les formats de RHS à utiliser:
 *
 * Pour les RHS à partir de la semaine 10 de 2025 : format M0D obligatoire 
 * Pour les RHS jusqu’à la semaine 9 de 2025 incluse : format M0C
 *
 *
 * Parametres :
 * input : fichier en entree
 * output : fichier en sortie
 * Exemple de lancement :
 *
 * cd C:\Local\e-pmsi\fichiers-rhs-ssr\2025\M03\RHS+VH
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi.bat -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rhs_m1c_vers_m1d.groovy -a:input SSR_RHSG_test1.txt -a:output SSR_RHSG_test1_m1d.txt
 *
 */
import java.text.SimpleDateFormat

import fr.gpmsi.pmsixml.RhsWriter
import fr.gpmsi.pmsixml.MetaFileLoader

df = new SimpleDateFormat('ddMMyyyy')

s10 = '202510' //la semaine 10 au format AAAASS

rhs {
  name 'Conversion des RHS de v.M1C en v.M1D'

  input args['input']
  output args['output']

  onInit {
    println "Debut analyse ${inputFilePath}"
    fout = new FileWriter(outputFilePath)
    rhswr = new RhsWriter()
    mfl = new MetaFileLoader()
    metaRHSM1D = mfl.loadMeta('/fr/gpmsi/pmsixml/rhsm1d.csv') //récupérer les métadonnées pour un rhs de type m1d
  }

  onItem {item->
    def rhs = item.rhs
    def vrhs_g = rhs.txtVRHSG
    def vrhs_ng = rhs.txtVRHS
    def nadl = rhs.txtNADL
    def sem_annee = rhs.txtNSEM
    def annee_sem = sem_annee[2..5] + sem_annee[0..1]
    if (vrhs_g == 'M1C' && vrhs_ng == 'M0C' && (annee_sem >= s10)) {
        //créer un nouveau RHS format M1D
        def sb = new StringBuffer()
        //rhswr.writeRhs(rhs, sb)
        //sb << "\r\n"
        def rhs_tr = metaRHSM1D.getFirstChildGroupMeta().makeNewNode() //créer un RHS vide de type RHSM1D
        rhs.copyFieldsTo(rhs_tr) //faire la recopie de tous les champs qui existent dans l'ancienne version
        rhs_tr.VRHSG.value = 'M1D' //changer valeur pour la version "RHS groupé"
        rhs_tr.VRHS.value = 'M0D' //changer valeur pour la version de "RHS non groupé"
        rhs_tr.NCSR.value = '000' //mettre le nombre d'actes CSAR à 0
        rhswr.writeRhs(rhs_tr, sb) //ecrire le RHS résultant
        fout.write(sb.toString())
        fout.write('\r\n')
    }
    else {
        //le RHS n'a pas besoin de conversion, ou n'est pas reconnu. Envoyer la ligne telle quelle, ne rien changer
        fout.write(item.line)
        fout.write('\r\n')
    }
  }

  onEnd {
    println "Fin analyse   ${inputFilePath}."
    fout.close()
  }
}

