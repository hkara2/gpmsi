/**☺:encoding=UTF-8:
 * Selection des séjours du RSS qui ont au moins un RUM dans une des UMs passée en
 * argument, et dont le mois de sortie dans le VIDHOSP est entre moismin et moismax (inclus).
 *
 * Arguments :
 * -a:input_rss chemin_du_fichier_rss
 * -a:input_vh  chemin_du_fichier_vidhosp
 * -a:ums liste_ums_separees_par_virgule
 * -a:moismin mois minimum de sortie du sejour (defaut : 1)
 * -a:moismax mois maximum de sortie du sejour (defaut : 12)
 * -a:output chemin_du_fichier_sortie
 * Exemples :
 *
 * C:\Local\e-pmsi\fichiers-rss-mco\2023\M12\RSS+VH\240209\RSA>c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi.bat -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\rss_pour_mois_et_ums.groovy -a:moismin 9 -a:moismax 12 -a:ums 4001 -a:input_vh ..\VIDHOSP_MCO.txt -a:input_rss ..\MCO_RSSG_20240209160321_122.txt -a:output nadls_sep_dec_4001.csv
 *
 * En décomposé :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2023\M12\RSS+VH\240209\RSA
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi.bat ^
 * -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\rss_pour_mois_et_ums.groovy ^
 * -a:moismin 9 ^
 * -a:moismax 12 ^
 * -a:ums 4001 ^
 * -a:input_vh ..\VIDHOSP_MCO.txt ^
 * -a:input_rss ..\MCO_RSSG_20240209160321_122.txt ^
 * -a:output nadls_sep_dec_4001.csv
 */

ums = [] as Set
nadls = [] as Set
nadls_dans_mois = [] as Set

crlf = '\r\n'

ums.addAll(args.ums.split(','))
moisMin = (args.moismin ?:  1) as int
moisMax = (args.moismax ?: 12) as int

//println "Table finessParNum : $finessParNum"

//Collecter les numéros de dossier qui sont dans la fourchette de mois demandés
vidhosp {
    name 'Collecter les nadls qui sont dans la periode de temps consideree'
    input args.input_vh

    onItem {item->
        def vh = item.vidhosp
        def dsor = vh.DSOR.toLocalDate()
        if (dsor != null) {
            def mois = dsor.monthValue
            if (moisMin <= mois && mois <= moisMax) nadls_dans_mois << vh.txtNADL
        }
    }
}

rss {
    name 'Collecter les nadl qui ont un RUM dans une des UMs et sortis dans la fourchette de mois precisee'
    input args.input_rss

    onItem {item ->
        def rum = item.rum
        def nadl = rum.txtNADL
        if (nadl in nadls_dans_mois && rum.txtNUM in ums) nadls << nadl
    }
}

//Enfin, émettre les NADLs qui ont été collectés
outf = new FileWriter(args.output)
nadls.each {nadl->
    outf << nadl << "\r\n"
}
outf.close()

//println "Table des NADLs : $nadls"

println "Fin du script, ${nadls.size()} sejours selectionnes."

