//:encoding=UTF-8:
//A partir d'un fichier d'atypies produit par l'ATIH, essayer de retrouver
//les numéros de dossiers concernés, en recherchant directement le fragment de
//rsa qui suit le numéro de RSA (à partir du caractère 22).
//Paramètres :
//-a:input_atih   fichier des rsa rejetés, que l'on trouve sur le site de l'ATIH/Ovalide
//-a:input_rsa    fichier des rsa transmis à l'atih, extrait depuis le répertoire de sauvegarde de GENRSA
//-a:input_tra    fichier tra qui fait la correspondance avec le numéro de dossier, extrait depuis le répertoire de sauvegarde de GENRSA
//-a:output       fichier de sortie, première colonne donne le numéro de référence, la deuxième le numéro de dossier
//Exemple : C:\Local\e-pmsi\fichiers-rss-mco\2018\M05\180709-epmsi\ePMSI180710>gpmsi -script C:\hkchse\dev\pmsixml\groovy-samples\reperage-rsa-atih.groovy -a:input_atih rsa_confirm_codage.txt -a:input_rsa 910019447.2018.5.rsa -a:input_tra 910019447.2018.5.tra.txt -a:output dossiers_confirm_codage.txt
import java.util.regex.* 
import fr.karadimas.pmsixml.MonoLevelReader
import fr.gpmsi.StringTable

line {
	input args['input_atih']
	output args['output']
	
	onInit {
		outs = new FileOutputStream(outputFilePath)
		patternReference = Pattern.compile(/R.f.rence =(\d+)/)
		patternNsej = Pattern.compile(/Num.roS.jour=(\d+)/)
		refByPrsa = [:]
	}
	
	onItem {item->
		def line = item.line
		Matcher m = patternReference.matcher(line)
		def rn = '?'
		if (m.find()) rn = m.group(1)
		def ns = '?'
		m = patternNsej.matcher(line)
		if (m.find()) ns = m.group(1)
		def prsa = '?'
		def ix = line.indexOf('> ')
	    if (ix > 0) {
	    	prsa = line.substring(ix+24)
	    	refByPrsa[prsa] = rn
	    }
		//outs << "$rn $ns $prsa" << "\r\n"
	}
	
	onEnd {
		println 'Fin de l analyse des rejets atih'
	}
}

rsa {
	name 'Reperage des RSA du rejet'
	input args['input_rsa']
	onInit {
		//Lecture table des tra
		def traPath = args['input_tra']
		def traFile = new File(traPath)
		//créer une StringTable pour lire le contenu du TRA
		traTbl = new StringTable('TRA')
		MonoLevelReader trar = new MonoLevelReader()
		trar.setMetaName("tra2016");
		//La StringTable sait s'auto-alimenter à partir d'un fichier à champs fixes avec sa définition !
		traTbl.readFrom(traFile, trar.getOrLoadMeta().getChildMetas().get(0)) //pas tres elegant, à améliorer
		traTbl.addIndex('NRSA') //ajout d'un index pour accélérer la recherche par numéro de RSA
	}
	onItem {item->
		def line = item.line
		def rsa = item.rsa
		def prsa = line.substring(22)
		def rn = refByPrsa[prsa]
		if (rn != null) {
			def ndoss = traTbl.find('NRSA', rsa.txtNRSA, 'NDOSS') //recuperer le numero de dossier grace a la table des tra
			outs << rn << ';' << ndoss << "\r\n"
		}
	}
	onEnd {
		outs.close()
	}
}
