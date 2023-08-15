//:encoding=UTF-8:
//Emettre les RUMs pour lesquels il y a au moins un acte dans la liste
//des actes donnée (on ne prend que les 7 premiers caractères de l'acte, CAD
//qu'on ne tient pas compte des ext. PMSI)
//Arguments :
//  input : fichier des RSS (groupes ou non) en entree
//  output : fichier qui contiendra uniquement les lignes RSS selectionnees
//  actes : actes séparés par des virgules, ex : "JCGE003,JCGH002,JCGE004,JAKD001,JCKD001,JCKE001,JCKH001,JCKE002" pour les poses de sondes de dérivation (sondes JJ)
//Exemple :
//Ne garder que les lignes de RUM qui ont au moins un acte CCAM parmi JCGE003,JCGH002,JCGE004,JAKD001,JCKD001,JCKE001,JCKH001,JCKE002
// 
//C:\Local\e-pmsi\fichiers-rss-mco\2022\M12\RSS+VH\t>C:\app\gpmsi\gpex -script C:\app\gpmsi\v1.1\scripts\groovy\rss_pour_actes.groovy -a:actes JCGE003,JCGH002,JCGE004,JAKD001,JCKD001,JCKE001,JCKH001,JCKE002 -a:output sel_rss.txt -a:input MCO_RSSG_20230209190338.txt

import fr.karadimas.gpmsi.StringTable
import fr.karadimas.gpmsi.CsvDestination
import fr.karadimas.gpmsi.StringTransformable

/** retourne true si le rum contient au moins un acte de la liste */
def contientAuMoinsUnActe(rum, listeActes) {
    actesRum = rum.ZA*.txtCCCA //lister tous les codes
    actesRum = actesRum*.substring(0,7) //ne garder que les 7 premiers caractères de chaque code
    //println("actesRum:$actesRum")
    return !(listeActes.disjoint(actesRum)) //disjoint est vrai s'il n'y a rien en commun
}

emittedCount = 0

rss {
    name 'Selection des RUMs pour une liste d actes'

    input args['input']

    output args['output']

    onInit {
        actesStr = args['actes']
        actes = (actesStr.split ",") as Set
        println "input : $inputFilePath"
        println "output : $outputFilePath"
        println "actes : $actes"
        fout = new FileWriter(new File(outputFilePath))
    }

    onItem {item->
        def rum = item.rum
        def nadl = rum.txtNADL //prendre l'nadl (numero adm local) du rum
        nadl = "" + (nadl as int) //le convertir en nombre pour eviter les confusions
        def emit = false //par defaut on envoie le RSS
        if (contientAuMoinsUnActe(rum, actes)) emit = true
        //emettre la ligne du RUM si emit est true
        if (emit) { fout.write(item.line); fout.write("\r\n"); emittedCount++ }
    }//onItem

    onEnd {
        fout.close()
        println("$emittedCount RUMs emis.")
    }
}