/**:encoding=UTF-8:
 * Joindre plusieurs fichiers de données (textuelles en un seul.
 * Utilisé lorsqu'une extraction de table doit se faire en plusieurs fois
 * Utilise un fichier qui doit contenir le chemin de chaque fichier à joindre,
 * dans l'ordre.
 * Les lignes du premier fichier sont copiées telles quelles.
 * Pour les autres fichiers, la première ligne (qui contient les noms de 
 * colonne) est ignorée.
 * Les lignes blanches sont conservées, car parfois avec l'envoi de csv, il
 * peut y avoir du multilignes.
 * Les paramètres sont :
 * -a:encoding   L'encodage à utiliser (par défaut celui de Windows (1252))
 * -a:listfile   Le fichier qui contient la liste des fichiers a joindre
 * -a:output     Le nom du fichier de sortie
 */
 
outf = null

line {
	input args.listfile
	output args.output
	onInit {
	    enc = args.encoding
	    if (enc == null) enc = "cp1252"
	    outf = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath), enc)))
	}
	onItem {item-> 
	    def filename = item.line
	    if (filename == null) return //ignorer lignes vides de la liste des fichiers
	    def infisr = new InputStreamReader(new FileInputStream(filename), enc)
	    def bufin = new BufferedReader(infisr)
	    def linenr = 1
	    def line = bufin.readLine()
	    while (line != null) {
	        if (linenr == 1 && item.linenr > 1) {
	            //ignorer premiere ligne des autres fichiers
	        }
	        else outf.println(line)
	        line = bufin.readLine()
	        linenr++
	    }
	    bufin.close()
	}
	onEnd {
	    outf.close()
	}
}


/*
 * Verification :
 * 3 fichiers, avec en nombre de lignes : 334553, 968544, 1232494 soit 2535591 au total
 * Après jointure des fichiers, la longueur est 2535587 soit 4 de moins :
 * 2 lignes de titre ignorés, et 2 lignes vides de fin ignorées.