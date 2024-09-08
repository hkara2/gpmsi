//:encoding=UTF-8:
//TODO 
//* enregistrer les resultats de groupage  par nadl pour chaque groupe
//  pour avoir le vrai mode "comparercmd". Recuperer pour le nadl qui n'est pas vide
//* dans les enregistrement a_ recuperer le GHM de B pour le nadl

//Comparer des RUMs groupes ou non et sortir les lignes differentes 
//(attention ne compare pas les actes)
//Il y a deux entrees : a et b
//Arguments : input_a, input_b, output
//Flags : comparercmd : compare les categories majeures de diagnostic et les 
//ghms (ne marche que pour les RUMs groupés bien sûr)
import fr.gpmsi.StringTable
import fr.gpmsi.CsvDestination
import fr.gpmsi.NumsRums //est avec les scripts distribués


def lastChar(str) {
	if (str == null || str.length() == 0) return ""
	return str.substring(str.length() - 1, str.length())
}

/**
 * calculer la cle de reperage. 
 * Ici c'est le numero administratif + le numero du RUM
 * A noter que ici le numero du RUM est soit vide, soit le RUM original, soit le RUM
 * renumerote selon l'utilisation qu'on veut faire de la cle.
 */
def rumKey(rum, num) {
  	  def nadl = rum.txtNADL
  	  "$nadl:$num"
}

def klix(str) {
	str = str ?: ''
	def ix = str.indexOf(':')
	if (ix < 0) return ""
	return str.substring(ix+1)
}

numsRumsA = new NumsRums()
numsRumsB = new NumsRums()

// rumNumbersAByNadl = [:]
// newKeysAByNrumKey = [:]
// rumsAByKey = [:]
extraANrumKeys = new HashSet()
groupsAByNadl = [:]
// 
// rumNumbersBByNadl = [:]
// newKeysBByNrumKey = [:]
// rumsBByKey = [:]
extraBNrumKeys = new HashSet()
groupsBByNadl = [:]

csvln = 1 //numero de la ligne csv courante ; utile pour l'envoi des formules

comparercmd = flags.contains('comparercmd')

rss {
  name 'Lecture des numeros de RUM de A et renumerotation'
  
  input args['input_a']
  
  onInit {
    println "Debut analyse de A : ${inputFilePath}"
  }
  onItem {item->
	def rum = item.rum
	def nadl = rum.txtNADL
	numsRumsA.indexRum(rum)

	def grp = ''
	if (comparercmd) grp = rum.txtNCMD + rum.txtNGHM
	if (grp.length() > 0) groupsAByNadl[nadl] = grp
  }
  onEnd {
  	  //renumeroter maintenant les numeros de RUM, en commencant a 1, pour chaque dossier
  	  numsRumsA.indexNumbers()
  	  extraANrumKeys = numsRumsA.getNrumKeys()
  	  //println "extraANrumKeys : '$extraANrumKeys'"
      println "Fin analyse de A :  ${inputFilePath}."
  }
}

rss {
  name 'Lecture des numeros de RUM de B et renumerotation'
  
  input args['input_b']
  
  onInit {
    println "Debut analyse de B : ${inputFilePath}"
  }
  onItem {item->
	def rum = item.rum
	def nadl = rum.txtNADL
	numsRumsB.indexRum(rum)

	def grp = ''
	if (comparercmd) grp = rum.txtNCMD + rum.txtNGHM
	if (grp.length() > 0) groupsBByNadl[nadl] = grp
  }
  onEnd {
  	  //renumeroter maintenant les numeros de RUM, en commencant a 1, pour chaque dossier
  	  numsRumsB.indexNumbers()
  	  extraBNrumKeys = numsRumsB.getNrumKeys()
      println "Fin analyse de B :  ${inputFilePath}."
  }
}

rss {
  name 'Comparaison avec les RSS de B'
  
  input args['input_b']
  
  output args['output']
  
  onInit {
    println "Debut comparaison A et B"
  	d = new CsvDestination(new File(outputFilePath), "Cp1252")
    //println "Starting with input ${inputFilePath}"
	//println "Anciens rums : ${rumsByKey}"
	comparercmd = flags.contains('comparercmd')
	entete = false
  }
  onItem {item->
  	def rumsDifferents = false
  	def rumb = item.rum
	def num_b = rumb.txtNUM
	def igs2_b = rumb.IGS2.toInt()
	def nadl_b = rumb.txtNADL
	def dp_b = rumb.txtDP.trim()
	//println "dp_b : '${dp_b}'"
	def dr_b = rumb.txtDR.trim()
	def das_b = rumb.DA.txtTDA*.trim()
	def rssId_b = rumb.txtNRSS
	def nrum_b = rumb.txtNRUM
	def ncmd_b = ''
	if (comparercmd) ncmd_b = rumb.NCMD ? rumb.txtNCMD : ""
	def nghm_b = ''
	if (comparercmd) nghm_b = rumb.NGHM ? rumb.txtNGHM : "" 
	def sev_b = lastChar(nghm_b)
	def deum_b = formatAsFrenchDate(rumb.DEUM.toDate())
	def dsum_b = formatAsFrenchDate(rumb.DSUM.toDate())
	def nrumKey_b = numsRumsB.makeNrumKey(rumb)
	def rorderKey_b = numsRumsB.getRorderKeyForNrumKey(nrumKey_b) //prendre la cle renumerotee
	def rum_a = numsRumsA.getRumForRorderKey(rorderKey_b) //cherche s'il y a un RUM dans A qui correspond à la clé avec numéro d'ordre de B
	//println "nrumKey_b $nrumKey_b, rorderKey_b $rorderKey_b, rum_a $rum_a"
	//println "  rum_a : ${rum_a}"
	def dp_a = ""
	def dr_a = ""
	def num_a = ""
	def cmd_a = ""
	def nghm_a = ""
	def igs2_a = 0
	def sev_a = ""
	def deum_a = ""
	def dsum_a = ""
	def das_a = []
	def nrum_a = ""
	def rorderKey_a = ""
	def nf = '_b' //par defaut, on a b mais pas a
	if (rum_a != null) {
	  num_a = rum_a.txtNUM
	  dp_a = rum_a.txtDP
	  dr_a = rum_a.txtDR
	  cmd_a = ''
	  if (comparercmd) cmd_a = rum_a.NCMD ? rum_a.txtNCMD : ""
	  nghm_a = ''
	  if (comparercmd) nghm_a = rum_a.NGHM ? rum_a.txtNGHM : ""
	  igs2_a = rum_a.IGS2.toInt()
	  sev_a = lastChar(nghm_a)
	  deum_a = formatAsFrenchDate(rum_a.DEUM.toDate())
	  dsum_a = formatAsFrenchDate(rum_a.DSUM.toDate())
	  das_a = rum_a.DA.txtTDA*.trim()
	  nrum_a = rum_a.txtNRUM
	  nf = 'ab' //on a trouve a et b
	  def nrumKey_a = numsRumsA.makeNrumKey(rum_a)
	  rorderKey_a = numsRumsA.getRorderKeyForNrumKey(nrumKey_a)
	  //println "nrumKey_a : '$nrumKey_a'"
	  extraANrumKeys.remove(nrumKey_a)
	  def newSz = extraANrumKeys.size()
	  //println "New size : $newSz"
	}
	def inchanges = das_a.intersect(das_b)
	def enleves = das_a - inchanges
	def ajoutes = das_b - inchanges
	if (!(dp_b == dp_a)) rumsDifferents = true
	if (!(igs2_b == igs2_a)) rumsDifferents = true
	//println "DP change : ancien ${dp_a}, nouveau ${dp_b}"
	if (!(dr_b == dr_a)) rumsDifferents = true
	if (!enleves.empty) rumsDifferents = true
	if (!ajoutes.empty) rumsDifferents = true
	if (comparercmd) {
		//on ne compare /que/ sur les resultats de groupage cmd + ghm
		rumsDifferents = false
		def grp_a = groupsAByNadl[nadl_b]
		def grp_b = groupsBByNadl[nadl_b]
		if (!(grp_a == grp_b)) rumsDifferents = true
	}
	if (rumsDifferents) {
		if (!entete) {
			d.f('ou')
			d.f('rssId_b')
			d.f('nadl_b')
			d.f('nrum_a')
			d.f('nrumR_a')
			d.f('nrum_b')
			d.f('nrumR_b')
			d.f('num_a')
			d.f('num_b')
			d.f('deum_a')
			d.f('deum_b')
			d.f('dsum_a')
			d.f('dsum_b')
			d.f('ds_a')
			d.f('ds_b')
			d.f('cmd_a')
			d.f('ghm_a')
			d.f('cmd_b')
			d.f('ghm_b')
			d.f('igs2_a')
			d.f('igs2_b')
			d.f('dp_a')
			d.f('dp_b')
			d.f('dr_a')
			d.f('dr_b')
			d.f('das_enleves')
			d.f('das_inchanges')
			d.f('das_ajoutes')
			d.f('sev_a')
			d.f('sev_b')
			d.endRow()
			csvln++
			entete = true
		}
	  d.f(nf)
	  d.f(rssId_b)
	  d.f(nadl_b.trim())
	  d.f(nrum_a)
	  d.f(klix(rorderKey_a))
	  d.f(nrum_b)
	  d.f(klix(rorderKey_b))
	  d.f(num_a)
	  d.f(num_b)
	  d.f(deum_a)
	  d.f(deum_b)
	  d.f(dsum_a)
	  d.f(dsum_b)
	  d.f("=L$csvln-J$csvln")
	  d.f("=M$csvln-K$csvln")
	  d.f(cmd_a)
	  d.f(nghm_a)
	  d.f(ncmd_b)
	  d.f(nghm_b)
	  d.f(igs2_a)
	  d.f(igs2_b)
	  d.f(dp_a)
	  d.f(dp_b)
	  d.f(dr_a)
	  d.f(dr_b)
	  d.f(enleves.join(','))
	  d.f(inchanges.join(','))
	  d.f(ajoutes.join(','))
	  d.f(sev_a)
	  d.f(sev_b)
	  d.endRow()	
	  csvln++
	}
	//println "DAS enleves : ${enleves}, idem : ${inchanges}, ajoutes : ${ajoutes}"
	extraBNrumKeys.remove(nrumKey_b)
  }
  onEnd {
  	//envoyer maintenant les manquants
  	extraANrumKeys.each {nrumKey->
  		def ruma = numsRumsA.getRumForNrumKey(nrumKey)
  		def rorderKey_a = numsRumsA.getRorderKeyForNrumKey(nrumKey)
  		def grpa = ''
  		if (comparercmd) grpa = ruma.txtNCMD + ruma.txtNGHM
  		def nadl_a = ruma.txtNADL
  		def grpb = groupsBByNadl[nadl_a] ?: '' //recuperer groupe de b ou '' si non trouvé
  		if (comparercmd && grpa == grpb) return //si en fait on a le meme groupe CMD et que l'on est en mode comparaison de cmd, sortir
		d.f("a_") //on a a mais pas b
		d.f(ruma.txtNRSS)
		d.f(ruma.txtNADL?.trim())
		d.f(ruma.txtNRUM)
		d.f(klix(rorderKey_a))
		d.f("")
		d.f("")
		d.f(ruma.txtNUM)
		d.f("")
		d.f(formatAsFrenchDate(ruma.DEUM.toDate()))
		d.f("")
		d.f(formatAsFrenchDate(ruma.DSUM.toDate()))
		d.f("")
        d.f("=L$csvln-J$csvln")
        d.f("=M$csvln-K$csvln")
		d.f( comparercmd && ruma.NCMD ? ruma.txtNCMD : "")
		def nghm_a = comparercmd && ruma.NGHM ? ruma.txtNGHM : ""
		d.f(nghm_a)
		d.f("")
		d.f("")
		d.f(ruma.IGS2.toInt())
		d.f(0)
		d.f(ruma.txtDP.trim())
		d.f("")
		d.f(ruma.txtDR.trim())
		d.f("")
		d.f(ruma.DA.txtTDA*.trim().join(','))
		d.f("")
		d.f("")
		d.f(lastChar(nghm_a))
		d.f("")
		d.endRow()
		csvln++
  	}
  	d.close()
    println "Fin comparaison A et B."
  }
}
