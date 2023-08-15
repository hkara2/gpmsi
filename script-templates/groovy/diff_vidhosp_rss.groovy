/**:encoding=UTF-8:
 * Difference entre dossiers VIDHOSP et RSS
 * Arguments :
 * input_rss : le chemin du fichier qui contient les RSS
 * input_vh : le chemin du fichier qui contient les VID HOSP
 * output : le chemin du fichier qui contient les différences
 *
 * Exemple d'exécution :
  c:\app\gpmsi\v@PROJECT_VERSION@\scripts\gpmsi.bat ^
  -script c:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\diff-vidhosp-rss.groovy ^
  -a:input_vh vh.txt ^
  -a:input_rss rss.txt ^
  -a:output diffs_rss_vh.txt
 * 
 * (C) Harry Karadimas 2020, CHSE
 */

import fr.karadimas.gpmsi.CsvDestination

nadls_vh = [] as Set
nadls_rss = [] as Set

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

rss {
    name 'Parcourir les RSS'
    input args['input_rss']
    
    onInit {
    }
    
    onItem {item->
        def rum = item.rum
        def nadl = rum.txtNADL
        nadls_rss.add(nadl)
    }
    
    onEnd {
    println "Fin lecture rss."
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
		def nadls_pas_dans_vh = nadls_rss - nadls_vh
		def nadls_pas_dans_rss = nadls_vh - nadls_rss
		def nadls_dans_vh_et_rss = nadls_rss.intersect(nadls_vh)
		nadls_pas_dans_vh.each {nadl->
		    d.f('r_')
		    d.f(nadl)
		    d.endRow()
		}
		nadls_pas_dans_rss.each {nadl->
		    d.f('_v')
		    d.f(nadl)
		    d.endRow()
		}
		nadls_dans_vh_et_rss.each {nadl->
		    d.f('rv')
		    d.f(nadl)
		    d.endRow()
		}
        d.close()
        println "Fin du script. Le fichier des differences est '$outputFilePath'."
    }
}
