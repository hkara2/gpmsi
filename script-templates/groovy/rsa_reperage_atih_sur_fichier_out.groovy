//:encoding=UTF-8:
//A partir d'un fichier d'atypies produit par l'ATIH, et du fichier .out.zip
//produit par GENRSA, essayer de retrouver
//les numéros de dossiers concernés, en recherchant directement le fragment de
//rsa qui suit le numéro de RSA (à partir du caractère 22).
//Paramètres :
//-a:input_atih   fichier des rsa rejetés, que l'on trouve sur le site de l'ATIH/Ovalide
//-a:input_outzip fichier .out.zip produit par GENRSA, (typiquement dans le repertoire de sauvegarde)
//-a:output       fichier de sortie, première colonne donne le numéro de référence, la deuxième le numéro de dossier
//Exemple (via fichier batch) :
//cd C:\Local\e-pmsi\fichiers-rss-mco\2020\M11\210108\ePMSI\OVALIDE
//C:\hkchse\dev\pmsixml\doc\exemples-de-script\atih-atypies-trouver-nda\atih-atypies-trouver-nda.bat C:\Users\hkaradimas\AppData\Roaming\ATIH\GENRSA\sauvegarde\910019447.2020.11.08012021180328.out.zip
//#210112 hk v.1.0
//
import java.util.regex.* 
import fr.karadimas.pmsixml.MonoLevelReader
import fr.gpmsi.StringTable
import fr.gpmsi.ZipUtils
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.FileVisitor

pRsa = null
pTra = null
infilename = null

line {
	input args['input_atih']
	output args['output']
	
	onInit {
	    infilename = new File(inputFilePath).name
		outs = new FileOutputStream(outputFilePath)
		patternReference = Pattern.compile(/R.f.rence =(\d+)/)
		patternNsej = Pattern.compile(/Num.roS.jour=(\d+)/)
		refByPrsa = [:]
		nsejByRef = [:]
	}
	
	onItem {item->
	    //on ne traite que les lignes qui contiennent le mot qui correspond a "R.f.rence =" avec un nombre derriere
	    //(il faut faire comme cela car les "é" sont dans un encodage inconnu)
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
	    	refByPrsa[prsa] = rn //enregistrer la reference ATIH par le contenu du RSA
	    	nsejByRef[rn] = ns //enregistrer le 'numero de sejour' ATIH par sa reference
	    }
		//outs << "$rn $ns $prsa" << "\r\n"
	}
	
	onEnd {
		def zf = ZipUtils.openZip(args.input_outzip)
		pRsa = ZipUtils.getAsTempFile(zf, 'glob:**.rsa')
		//println "pRsa:$pRsa"
		pTra = ZipUtils.getAsTempFile(zf, 'glob:**.tra.txt')
		//println "pTra:$pTra"
	}

}

rsa {
	name 'Reperage des RSA du rejet'
	input pRsa.toString()
	onInit {
		//Lecture table des tra
		//def traPath = args['input_tra']
		def traFile = pTra.toFile()
		//créer une StringTable pour lire le contenu du TRA
		traTbl = new StringTable('TRA')
		MonoLevelReader trar = new MonoLevelReader()
		trar.setMetaName("tra2016");
		//La StringTable sait s'auto-alimenter à partir d'un fichier à champs fixes avec sa définition !
		traTbl.readFrom(traFile, trar.getOrLoadMeta().getChildMetas().get(0)) //pas tres elegant, à améliorer
		traTbl.addIndex('NRSA') //ajout d'un index pour accélérer la recherche par numéro de RSA
		outs << 'fichier;ReferenceATIH;NumeroSejourATIH;NRSA;NDA' << "\r\n"
	}
	onItem {item->
		def line = item.line
		def rsa = item.rsa
		def prsa = line.substring(22)
		def rn = refByPrsa[prsa]
		if (rn != null) {
			def ndoss = traTbl.find('NRSA', rsa.txtNRSA, 'NDOSS') //recuperer le numero de dossier grace a la table des tra
			def ns = nsejByRef[rn]
			outs << infilename << ';' << rn.trim() << ';' << ns << ';' << rsa.txtNRSA.trim() << ';' << ndoss.trim() << "\r\n"
		}
	}
	onEnd {
		outs.close()
	}
}


/*
 * Exemple de fichier OVALIDE téléchargé depuis ePMSI :
-------------------------------------------------------------------------------
OVALIDE T2A MCO PUBLIC : Test t1q5dpr
CHI SUD ESSONNE-DOURDAN-ETAMPES (910019447)
2020 M11 : de janvier Ó novembre

R²f²rence =1	 Num²roS²jour=220	 > 91001944722600000002201200041120Z06T   1120Z06T00001059   1850420208  00019165091650         00728000000000 0 10 00000000000000000000000 072800       00000000000000000000000000000                 02000        01F163        000100002               010034910000280F163        000  01002000107AC000000          Z290  000YYYY6000001      4011000ZZQH0330001      4011
R²f²rence =2	 Num²roS²jour=3491	 > 91001944722600000034911200041108M312   1108M31200001002   2850420208 A00052831028310         00313500000000 0 00 00000000000000000000000 031350       00000000000000000000000000000                 0200         01M8602       000700003               010010910000280M8602       000  07003000528 C000000          B950  D571  D649  J030  Z290  Z7580 R650  000MZQK0030001 EFZ  1011001QZQM0010001      1011002PAQL0020001 G     011
R²f²rence =3	 Num²roS²jour=4376	 > 91001944722600000043761200041108M312   1108M31200001002   28 0520208 A00062831028310         00313500000000 0 00 00000000000000000000000 031350       00000000000000000000000000000                 0200         01M8602       000600002               010010910000280M8602       000  06002000628 C000000          D570  R194  R509  Z290  Z7580 R650  000MBQK0010001 EZ   1011002MZQJ0010001       011
R²f²rence =4	 Num²roS²jour=7182	 > 91001944722600000071821200041120Z062   1120Z06200001031   2850420208  00039141091410         00728000000000 0 10 00000000000000000000000 072800       00000000000000000000000000000                 02000        01F1231       000200002               010034910000280F1231       000  02002000307AC000000          Z290  U0713 001YYYY6000001      4011001ZZQH0330001 Z    4011
R²f²rence =5	 Num²roS²jour=10400	 > 91001944722600000104001200041120Z031   1120Z03100001053   18504202074 00019115091150         00726300000000 0 00 00000000000000000000000 072630       00000000000000000000000000000                 0200         01F1904       000000000               010003910001973F1904       000  00000000107AC000000          


Attention : les fichiers ont ²t² r²-anonymis²s et m²lang²s apr²s leur r²c²ption par e-PMSI. Les num²ros des s²jours ne correspondent donc pas ² ceux des fichiers au niveau des ²tablissements.
-------------------------------------------------------------------------------
 * N.B. l'encodage est inconnu ; ici j'ai ouvert le fichier en encodeage DOS IBM850
 * mais en fait aucun encodage connu (Windows 1252, UTF-8, IBM850) ne fonctionne
 */
 
// Astuce pour réunir les fichiers txt à la fin : copy *.txt tout.txt
