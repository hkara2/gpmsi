/**☺:encoding=UTF-8:
 * Pour un RSS donner le nombre de RUMs par UF.
 *
 * Selection des séjours du RSS qui ont au moins un RUM dans une des UMs passée en
 * argument, et dont le mois de sortie dans le VIDHOSP est entre moismin et moismax (inclus).
 *
 * Arguments :
 * -a:input chemin_du_fichier_rss
 * -a:output chemin_du_fichier_sortie  : fichier csv avec colonnes UF;NB
 * Exemples :
 *
 * %GPMSI_BASE%\v@PROJECT_VERSION@\gpmsi.bat -script %GPMSI_BASE%\v@PROJECT_VERSION@\scripts\rss_stats_ums.groovy -a:input MCO_RSSG_20240209160321_122.txt -a:output ums_nb_rums.csv
 *
 */
import fr.gpmsi.CsvDestination

nbs_par_um = [:]

rss {
    name 'Compter les UMs'
    input args.input

    onItem {item ->
        def rum = item.rum
        def num = rum.txtNUM
        def nb = 0
        if (nbs_par_um.containsKey(num)) nb = nbs_par_um[num]
        nb++
        nbs_par_um[num] = nb
    }

    onEnd {
        //envoyer maintenant le csv avec les stats par UM
        def cd = new CsvDestination(new File(args.output))
        cd.f "UM"
        cd.f "NB"
        cd.endRow()
        //commencer par reprendre la liste des UMs mais par ordre alpha croissant
        def ums = []
        ums.addAll(nbs_par_um.keySet())
        ums.sort()
        ums.each {um->
            cd.f um
            cd.f nbs_par_um[um]
            cd.endRow()
        }
        cd.close()
    }
}

println "Fin du script, ${nbs_par_um.size()} UMs comptees."

