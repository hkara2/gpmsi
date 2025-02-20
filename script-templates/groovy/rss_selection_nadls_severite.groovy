/*:encoding=UTF-8:
 * Sélection de NADLs à partir des lignes des RSS d'une liste de NADLs et de sévérité.
 * Pour chaque ligne de RUMs, on ne garde que les NADLs qui sont dans la liste 
 * et pour laquelle la sévérité est au minimum égale à smin, et/ou au maximum égale à smax.
 * Cette sélection peut être utile lorsque l'on a ciblé sur quelque chose qui
 * peut être revalorisé mais qu'on ne veut garder que les séjours qui sont
 * encore revalorisables.
 * Attention les sévérités comprennent les lettres aussi, qui suivent les chiffres.
 * Pour rappel on peut avoir 1234ABCDEJTZ en niveau de sévérité.
 * Par exemple si on ne veut que les Z on fait -a:smin Z -a:smax Z
 * Par défaut smin est '0' et smax est 'Z'
 *
 * Arguments :
 *   input : fichier des RSS (groupes) en entree
 *   output : fichier qui contiendra uniquement les lignes RUM pour les RSS selectionnes
 *   nadls : fichier csv qui contient la colonne des nadl a inclure (le nom de colonne doit être "NADL" en majuscules)
 *   smin : sévérité minimum à garder. Par exemple -a:smin 3 ne gardera que les niveaux 3 et 4 et toutes les lettres
 *   smax : sévérité maximum à garder. Par exemple -a:smax 3 ne gardera que les niveaux 1, 2 et 3
 *
 * Par défaut smin = '0' et smax = 'Z'.
 *
 * Les nadls sont recherchés tels quels (donc faire attention si le zéro initial est significatif).
 *
 * Exemple :
 * Ne garder que les lignes de RUM qui concerne les dossiers dont les numéros
 * sont dans le fichier err23.txt et dont la sévérité est au minimum de 2 :
 * cd C:\Local\GROUPAGE\2019\M04\190513-d\CDP190513
 * gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rss_selection_nadls_severite.groovy ^
 * -a:input 042019_GRP019fg1819.txt ^
 * -a:output grp_err23.txt ^
 * -a:nadls err23.txt
 * -a:smin 2 -a:smax 4
 C:\Local\e-pmsi\fichiers-rss-mco\2024\M12\RSS+VH>c:\app\gpmsi\v2.0\gpmsi.bat -script c:\app\gpmsi\v2.0\scripts\groovy\rss_selection_nadls_severite.groovy -a:input MCO_RSSG_20250205003852-R01R02.txt -a:output RSS_selection.txt -a:nadls "C:\Local\BO-MCO\Medicaments-prescrits\2024\ndas_antidepresseurs.csv" -a:smin 1 -a:smax 1
 C:\Local\e-pmsi\fichiers-rss-mco\2024\M12\RSS+VH>c:\app\gpmsi\v2.0\gpmsi.bat -script c:\app\gpmsi\v2.0\scripts\groovy\rss_selection_nadls_severite.groovy -a:input MCO_RSSG_20250205003852-R01R02.txt -a:output RSS_selection.txt -a:nadls "C:\Local\BO-MCO\Medicaments-prescrits\2024\ndas_antidepresseurs.csv" -a:smin 1 -a:smax 1
 */

import fr.gpmsi.StringTable
import fr.gpmsi.CsvDestination
import fr.gpmsi.StringTransformable

smin = '0'
smax = 'Z'

if (args.containsKey('smin')) smin = args.smin
if (args.containsKey('smax')) smax = args.smax
nadlsFilePath = args['nadls']
if (nadlsFilePath == null) throw new Exception("Argument manquant : -a:nadls")

nadlsCol = -1

nadlsSelectionnes = [] as Set //ensemble des NADLs sélectionnés (on utilise un ensemble car sinon le même nadl pourrait apparaître plusieurs fois)

rss {
    name 'Selection des NADLs a partir des RUMs'

    input args['input']

    output args['output']

    onInit {
        def trimmer = {s -> ("" + s).trim() } as StringTransformable //pour enlever les espaces initiaux et finaux
        nadls = new StringTable(new File(nadlsFilePath), "utf-8", 0)
        nadlsCol = nadls.getColumnNumber("NADL") //au mieux majuscules, mais par defaut la recherche n'est pas sensible a la casse
        if (nadlsCol < 0) throw new Exception("Colonne non trouvee : NADL")
        nadls.transform(nadlsCol, trimmer)
        //nadls.each { println "nadl:${it[0]}" }
        fout = new FileWriter(new File(outputFilePath))
    }

    onItem {item->
        def rum = item.rum
        def nadl = rum.txtNADL //prendre le nadl (numero adm local) du rum
        //println "nadl:$nadl"
        def keep = true //par defaut on garde ce nadl, sauf si un des critères n'y est pas
        //Si le nadl courant n'est pas dans la table, ne pas le garder
        if (nadls.findRow(nadlsCol, nadl) < 0) keep = false;
        //Si la sévérité n'est pas entre smin et smax, ne pas garder le nadl
        def sev = rum.txtNGHM[-1]
        //println "sev:$sev"
        //si le niveau de sévérité est compatible avec ce qui est demandé, garder le nadl
        if (sev < smin || smax < sev) keep = false
        //println "sev:$sev"
        //garder le NADL dans l'ensemble si keep est true
        if (keep) nadlsSelectionnes << nadl
    }//onItem

    onEnd {
        //écrire l'ensemble des NADLs sélectionnés et fermer le writer
        nadlsSelectionnes.each {nadl-> fout << "$nadl\r\n"}
        fout.close()
    }
}