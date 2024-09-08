package fr.gpmsi

/**
 * Gestion des numéros de RUMs (nrum) pour avoir un numéro de l'ordre de ce
 * numéro du RUM (rorder) dans le RSS.
 * On crée des clés nrumKey (NADL:NRUM) et rorderKey (NADL:rorder) qui permettent de faire des 
 * tableaux globaux.
 */
public class NumsRums {
    def nrumsByNadl = [:] //numéros de rum par nadl
    def rumsByNadl = [:] //rums par nadl
    def rorderKeysByNrumKey = [:]
    def rumsByNrumKey = [:]
    def rumsByRorderKey = [:]
    def nrumKeys = []
    
    // comparer les RUMs pour le chaînage. On compare d'abord par date d'entrée
    // puis par date de sortie, puis par numéro de RUM
    def compareRums = {r1, r2 ->
    	def deum1 = r1.DEUM.toDate()
    	def deum2 = r2.DEUM.toDate()
    	def cr = deum1 <=> deum2
    	if (cr != 0) return cr
    	def dsum1 = r1.DSUM.toDate()
    	def dsum2 = r2.DSUM.toDate()
    	cr = dsum1 <=> dsum2
    	if (cr != 0) return cr
    	return r1.NRUM.toInt() <=> r2.NRUM.toInt()
    }
    
    /**
     * A appeler une fois pour chaque RUM, enregistre les numeros originaux des 
     * RUMs et les rums eux-même
     */
    void indexRum(rum) {
        def nadl = rum.txtNADL //prendre valeur textuelle de NADL
        def nrums = nrumsByNadl[nadl] //et recuperer le tableau des numeros de RUM
        if (nrums == null) { //si ce tableau n'existe pas
            nrums = [] //le creer
            nrumsByNadl[nadl] = nrums //et l'enregistrer pour le nadl
        }
        nrums << rum.txtNRUM //prendre valeur textuelle de NRUM et ajouter a la liste globale des numeros de rum
        
        def rums = rumsByNadl[nadl] //recuperer le tableau des RUMs
        if (rums == null) { //si cette liste n'existe pas
            rums = [] //la creer
            rumsByNadl[nadl] = rums //et l'enregistrer pour le nadl
        }
        rums << rum //prendre valeur textuelle de NRUM et ajouter a la liste globale des numeros de rum
        
        //println "original key A : $originalKey, rum : $rum"
        rumsByNrumKey[makeNrumKey(rum)] = rum //enregistrer le rum pour plus tard par sa cle
	}


    /**
     * A appeler une fois tous les rums traites par indexRum(rum).
     * Pour chaque nadl, trie par ordre croissant chaque nrum, et enregistre
     * ensuite la position relative de chaque nrum.
     */
    void indexNumbers() {
  	  //renumeroter maintenant les numeros de RUM, en commencant a 1, pour chaque dossier
  	  rumsByNadl.each {nadl, rums->
  	  	  rums.sort(compareRums) //trier le tableau avec la fonction de comparaison adéquate
  	  	  rums.eachWithIndex {rum, ix->
  	  	  	  //def rorder = (ix + 1) as String //formatter sur 3 chiffres le numero d'ordre du rum
  	  	  	  //rorder = rorder.padLeft(nrum.length()) //rajouter des espaces pour que la taille soit la meme que pour le NRUM
  	  	  	  def nrum = rum.txtNRUM
  	  	  	  def rorder = ix + 1
  	  	  	  def nrumKey = "$nadl:$nrum" as String
  	  	  	  def rorderKey = "$nadl:$rorder" as String
  	  	  	  //println "nrumKey A $nrumKey, rorderKey A $rorderKey"
  	  	  	  rorderKeysByNrumKey[nrumKey] = rorderKey //garder la correspondance ancien -> nouveau
  	  	  	  rumsByRorderKey[rorderKey] = rumsByNrumKey[nrumKey] //ranger aussi le rum par sa nouvelle cle
  	  	  	  nrumKeys << nrumKey
  	  	  }
  	  }
    }
    
    def getRumForNrumKey(key) { return rumsByNrumKey[key] }
    
    def getRumForRorderKey(key) { return rumsByRorderKey[key] }
    
    def getNrumForNadl(nadl) { return nrumsByNadl[nadl] }
    
    def getRorderKeyForNrumKey(key) { return rorderKeysByNrumKey[key] }
    
    def makeNrumKey(rum) { rum.txtNADL + ':' + rum.txtNRUM }
    
    def makeRorderKey(rum, key) { rum.txtNADL + ':' + key }
    
    def getNrumKeys() { nrumKeys }
}
