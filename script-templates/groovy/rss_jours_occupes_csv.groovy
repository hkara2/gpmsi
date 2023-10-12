/**☺:encoding=UTF-8:
 * Extraire jours d'occupation (sortie - entrée + 1) par UF et par jour de l'année.
 * Le format de sortie CSV est de 4 colonnes :
 * UM : le numéro d'UM concerné
 * JA : le numéro du jour de l'année. 1 est le 1er janvier de l'année.
 * MO : le numéro du mois.
 * NB : nombre de jours occupés au total
 * Un exemple d'utilisation de ces données est dans : xlsx\jours_um_pour_rss_jours_occupes_csv.xlsx
 * Arguments :
 * -a:year annee a extraire
 * -a:input chemin(s)_du_ou_des_fichier(s)_entree (séparés par ";")
 * -a:output chemin_du_fichier_sortie
 * Exemple 1 : analyse sur 2023, avec RSS(s) de 2023 de janvier à août :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2023\M01\RSS+VH
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi.bat -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rss_jours_occupes_csv.groovy -a:input MCO_RSSG_20230307194405.txt -a:output jours_um.csv
 * Exemple 2 : analyse sur 2023, avec RSS(s) de tout 2022 + RSS(s) de 2023 de janvier à août :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2023\M08\RSS+VH\231007
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi.bat -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rss_jours_occupes_csv.groovy -a:year 2023 -a:input "C:\Local\e-pmsi\fichiers-rss-mco\2022\M12\RSS+VH\MCO_RSSG_20230209190338.txt;MCO_RSSG_20231007113755_122.txt" -a:output jours_um.csv
 */
import java.time.LocalDate
import fr.karadimas.gpmsi.CsvDestination

def getOrMakeYearTable(String um) {
    def year = anneesUm[um]
    if (year == null) {
        year = new ArrayList<int>(366)
        for (int i = 0; i < 366; i++) year[i] = 0
        anneesUm[um] = year
    }
    return year
}

//annees frequentees, par UF
anneesUm = [:]

if (!args.containsKey('year')) throw new Exception("Argument manquant : year")

//annee a extraire
anneeEnCours = args.year as int

println "Annee en cours:$anneeEnCours"

j1 = LocalDate.of(anneeEnCours, 1, 1) //premier jour
dj = LocalDate.of(anneeEnCours, 12, 31) //dernier jour 

//prendre chacun des chemins des RSS à analyser, séparés par un point-virgule
inputs = args.input.tokenize(';')

inputs.each { inpath ->
    println "inpath:$inpath"
    //Procéder au comptage des jours pour l'entrée donnée
    rss {
        input inpath
        output args.output
        onInit {
            //ouvrir la destination csv, au format préféré de Excel
            d = new CsvDestination(new File(outputFilePath), "windows-1252")
            d.f 'UM' //um
            d.f 'JA' //jour de l'année
            d.f 'MO' //numéro du mois
            d.f 'NB' //nombre de jours
            d.endRow()
        }
        onItem {item->
            def rum = item.rum
            def dateDebut = rum.DEUM.toLocalDate()
            def dateFin = rum.DSUM.toLocalDate()
            if (dateDebut != null && dateFin != null) {
                def num = rum.txtNUM
                def jours = getOrMakeYearTable(num)
                def jourDebut = dateDebut.dayOfYear
                def anneeDebut = dateDebut.year
                def jourFin = dateFin.dayOfYear
                def anneeFin = dateFin.year //c'est l'année en cours
                //couper au 1er janvier si on est sur un séjour à cheval
                if (anneeDebut < anneeEnCours) jourDebut = 1
                //couper au 31 decembre si on est sur un séjour à cheval
                if (anneeEnCours < anneeFin) jourFin = dj.dayOfYear
                //println "num:$num,jourDebut:$jourDebut,anneeDebut:$anneeDebut,jourFin:$jourFin,anneeFin:$anneeFin,"
                //compter les jours mais seulement si le RUM a un morceau dans
                //l'annee en cours (sinon ignorer RUMs des annees precedentes et suivante)
                if (anneeDebut == anneeEnCours || anneeFin == anneeEnCours) {
                    //maintenant ajouter 1 jour pour chaque jour de présence dans l'année en cours
                    for (int i = jourDebut; i <= jourFin; i++) jours[i]++
                }
            }
        }
        onEnd {
            //envoyer toutes les années - UM dans la destination csv
            anneesUm.each { key, jours ->
                for (int i = 0; i < jours.size(); i++) {
                    d.f key
                    d.f i + 1
                    d.f j1.plusDays(i).getMonthValue()
                    d.f jours[i]
                    d.endRow()
                }//for
            }//anneesUm.each
            //fermer la destination
            d.close()
        }
    }//rss

}//inputs.each

