/**☺:encoding=UTF-8:
 * Selection de RSS sur un FINESS geographique.
 *
 * Garder les RSS qui ont au moins un RUM dans le FINESS précisé.
 * On utilise pour cela le fichier d'autorisation des UM qui doit être fourni.
 *
 * Arguments :
 * -a:input chemin_du_fichier_entree
 * -a:finess finess_pour_lequel_on_veut_les_sejours
 * -a:infoum chemin_du_fichier_des_infos_um
 * -a:output chemin_du_fichier_sortie
 * Exemples :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\gabarit_script.groovy -a:input test.txt -a:output testout.txt
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2023\M12\Medlink-optimisation\RSS
 c:\app\gpmsi\v1.3\gpmsi.bat -script c:\app\gpmsi\v1.3\scripts\groovy\rss_selection_sur_finess_geo.groovy ^
 -a:input RSSG-non-codes-medlink.txt ^
 -a:infoum "C:\Local\e-pmsi\fichiers-rss-mco\2023\IUM\autorisations_um_infoum1.txt" ^
 -a:finess 910001973 ^
 -a:output RSS-optimisation-selection2.txt
 */

finessParNum = [:]
nadls = [] as Set
finess = args.finess
crlf = '\r\n'
nbRumsSelectionnes = 0

mono {
    name 'Construire la table des finess par UM'
    input args.infoum
    metaName 'infoum1'
    onItem {item->
        def infoum = item.mono
        def num = infoum.txtNUM
        def fgeo = infoum.txtFGEO
        //println "num '$num' - fgeo '$fgeo'"
        finessParNum[num] = fgeo
    }
}

//println "Table finessParNum : $finessParNum"

rss {
    name 'Collecter les nadl qui ont le bon FINESS'
    input args.input
    onItem {item ->
        def rum = item.rum
        if (finessParNum[rum.txtNUM] == finess) nadls.add(rum.txtNADL)
    }
}

//println "Table des NADLs : $nadls"

rss {
    name 'Envoyer les RUMs qui ont un NDA dans la selection'
    input args.input
    output args.output
    onInit { outf = new FileWriter(outputFilePath) }
    onItem {item ->
        def rum = item.rum
        if (rum.txtNADL in nadls) {
            outf << item.line << crlf
            nbRumsSelectionnes++
        }
    }
    onEnd {
        outf.close() 
    }
}

println "$nbRumsSelectionnes RUMs selectionnes."
println "${nadls.size()} sejours selectionnes."

