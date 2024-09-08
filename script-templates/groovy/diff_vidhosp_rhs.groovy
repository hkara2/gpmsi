/**☺:encoding=UTF-8:
 * Difference entre dossiers VIDHOSP et RHS
 * Arguments :
 * input_rhs : le chemin du fichier qui contient les RHS
 * input_vh : le chemin du fichier qui contient les VID HOSP
 * output : le chemin du fichier qui contient les différences
 *
 * Exemple d'exécution :
  c:\app\gpmsi\v@PROJECT_VERSION@\scripts\gpmsi.bat ^
  -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\diff_vidhosp_rhs.groovy ^
  -a:input_vh vh.txt ^
  -a:input_rhs rhs.txt ^
  -a:output diffs_rhs_vh.txt
 *
 * Harry Karadimas 2020, 2022 CHSE
 */

import fr.gpmsi.CsvDestination

nadls_vh = [] as Set
nadls_rhs = [] as Set

vidhosp {
  name 'parcourir les vidhosp'
  input args['input_vh']

  onInit {
  }

  onItem {item->
    def vh = item.vidhosp
    def nadl = vh.txtNADL //numero de dossier
    nadls_vh.add(nadl)
    //println "$nadl\t$nia\t$dent\t$dsor"
  }

  onEnd {
    println "Fin lecture vidhosp."
  }
}

rhs {
    name 'Parcourir les RHS'
    input args['input_rhs']

    onInit {
    }

    onItem {item->
        def rhs = item.rhs
        def nadl = rhs.txtNADL
        nadls_rhs.add(nadl)
    }

    onEnd {
    println "Fin lecture rhs."
    }
}

single {
    name 'Faire la difference des deux ensembles de nadl'
    output args['output']

    onEnd {
        d = new CsvDestination(new File(outputFilePath), "Cp1252")
        d.f('ou')
        d.f('nadl')
        d.endRow()
        def nadls_pas_dans_vh = nadls_rhs - nadls_vh
        def nadls_pas_dans_rhs = nadls_vh - nadls_rhs
        def nadls_dans_vh_et_rhs = nadls_rhs.intersect(nadls_vh)
        nadls_pas_dans_vh.each {nadl->
            d.f('r_')
            d.f(nadl)
            d.endRow()
        }
        nadls_pas_dans_rhs.each {nadl->
            d.f('_v')
            d.f(nadl)
            d.endRow()
        }
        nadls_dans_vh_et_rhs.each {nadl->
            d.f('rv')
            d.f(nadl)
            d.endRow()
        }
        d.close()
        println "Fin du script. Le fichier des differences est '$outputFilePath'."
    }
}
