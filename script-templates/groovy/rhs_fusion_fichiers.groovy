/**☺:encoding=UTF-8:
 * fusionner ensemble deux fichiers de RHS A et B
 * On importe d'abord tous les RHS de A et on les ajoute à la sortie
 * Puis on parcourt les RHS de B, et on les ajoute à la sortie s'ils ne sont pas dans A
 *
 * Exemple :
 * cd C:\Local\e-pmsi\fichiers-rhs-ssr\2020\M12\210201\DXC210201_1616
 * gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rhs-fusion-fichiers.groovy -a:input_a SSR_RHSG_20210201161211.txt -a:output fusion.txt -a:input_b SSR_RHSG_DATES_RHS_20210201181525.txt
 */
 
rhsaKeys = [] as Set
outs = null

rhs {
  name 'Lecture des RHS de A'
  
  input args['input_a']
  output args['output']
  
  onInit {
    println "Debut analyse A : ${inputFilePath}"
    outs = new FileWriter(outputFilePath)
  }
  onItem {item->
	def rhs = item.rhs
	def nadl = rhs.txtNADL
	def nsem = rhs.txtNSEM
	//println "nadl $nadl, nsem $nsem"
	def key = nadl + '_' + nsem
	outs << item.line << "\r\n"
	rhsaKeys << key
  }
  onEnd {
    println "Fin analyse A : ${inputFilePath}."
  }

}

rhs {
  name 'Lecture des RHS de B'
  
  input args['input_b']
  
  onInit {
    println "Debut analyse B : ${inputFilePath}"
  }
  onItem {item->
	def rhs = item.rhs
	def nadl = rhs.txtNADL
	def nsem = rhs.txtNSEM
	//println "nadl $nadl, nsem $nsem"
	def key = nadl + '_' + nsem
	if (!rhsaKeys.contains(key)) outs << item.line << "\r\n"
  }
  onEnd {
    println "Fin analyse B : ${inputFilePath}."
    outs.close()
  }

}

