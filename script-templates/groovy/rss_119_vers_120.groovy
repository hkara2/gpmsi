/**:encoding=UTF-8:
 * Correction de RSS de version 119 en version 120
 * Méthode "brutale" en écrivant directement dans les positions de caractères.
 * Parametres :
 * input : fichier en entree
 * output : fichier en sortie
 * Exemple :
 * C:\Local\e-pmsi\fichiers-rss-mco\2019\M03\190507\DXC>c:\app\gpmsi\v@PROJECT_VERSION@\scripts\gpmsi.bat -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rss-119-vers-120.groovy -a:input RSSG_119.txt -a:output RSSG_transforme_120.txt
 */
import java.text.SimpleDateFormat

df = new SimpleDateFormat('ddMMyyyy')

premierMars = df.parse('01032020')

dateSortieHopParNadl = [:]

rss {
  name 'Parcours des RSS pour date sortie hop'
  
  input args['input']
  
  onItem {item->
	def rum = item.rum
	def dsum = rum.DSUM.toDate()
	def nadl = rum.txtNADL
	def dshop = dateSortieHopParNadl[nadl]
	if (dshop == null) dshop = dsum
	else if (dsum.after(dshop)) dshop = dsum
	dateSortieHopParNadl[nadl] = dshop
  }
  
}

rss {
  name 'Conversion des RSS de v.119 en v.120'
  
  input args['input']
  output args['output']
  
  onInit {
    println "Debut analyse ${inputFilePath}"
    fout = new FileWriter(outputFilePath)
  }
  
  onItem {item->
	def rum = item.rum
	def vrss = rum.txtVRSS
	def vrum = rum.txtVRUM
	def nadl = rum.txtNADL
	def dshop = dateSortieHopParNadl[nadl]
	if (vrss == '119' && vrum == '019' && (dshop == premierMars || dshop.after(premierMars))) {
	    //changer les numéros de version
	    def t = item.line
	    def newLine = t[0..8] + '120' + t[12..23] + '020' + t[27..-1]
	    fout.write(newLine)
	    fout.write('\r\n')	    
	}
	else {
	    //envoyer la ligne telle quelle
	    fout.write(item.line)
	    fout.write('\r\n')
	}
  }
  
  onEnd {
    println "Fin analyse   ${inputFilePath}."
    fout.close()
  }
}

