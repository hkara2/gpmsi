// :encoding=utf-8:
/**
 * Dans un fichier de RSS trouver les doublons RSS/RUM et les doublons parfaits
 * de ligne. On les envoie juste dans la sortie normale.
 * arguments :
 * -a:input chemin_du_fichier_rss_a_analyser
 * Exemple :
 * cd C:\Local\e-pmsi\fichiers-rss-mco\2023\M10\RSS+VH\231214
 * c:\app\gpmsi\v1.3\gpmsi.bat -script c:\app\gpmsi\v1.3\scripts\groovy\rss_doublons_lignes.groovy -a:input MCO_RSSG_20231213134638_122.txt
 */

numlignesParLigne = [:] //pour une ligne, liste des numeros ou on trouve cette ligne

numlignesParNumrssrum = [:] //pour un couple nrss_nrum, liste des numeros ou on trouve ce couple

rss {
    name 'Recherche doublons'

    input args['input']

    onItem {item->
        def rum = item.rum
        def linenr = item.linenr
        def line = item.line
        def nrss = rum.txtNRSS
        def nrum = rum.txtNRUM
        def nrssnrum = nrss + '_' + nrum
        
        //enregistrement des numéros de ligne par contenu de ligne
        def listeNumeros = []
        if (numlignesParLigne.containsKey(line)) {
            listeNumeros = numlignesParLigne[line]
        }
        else {
            numlignesParLigne[line] = listeNumeros
        }
        listeNumeros << linenr
        listeNumeros = null //on ne reutilise pas les variables
        
        //même démarche pour détecter les couples RSS/RUM
        def listeNumeros2 = []
        if (numlignesParNumrssrum.containsKey(nrssnrum)) {
            listeNumeros2 = numlignesParNumrssrum[nrssnrum]
        }
        else {
            numlignesParNumrssrum[nrssnrum] = listeNumeros2
        }
        listeNumeros2 << linenr
    }//onItem

    onEnd {
        int nbDoublons = 0
        //ecrire maintenant les lignes en double :
        numlignesParLigne.each {k,v ->
            if (v.size > 1) {
                println "Lignes en double : $v"
                nbDoublons++
            }
        }
        //idem pour les lignes qui ont le même couple RSS+RUM :
        numlignesParNumrssrum.each {k,v ->
            if (v.size > 1) {
                println "Numeros RSS+RUM double : $v"
                nbDoublons++
            }
        }
        if (nbDoublons == 0) {
            println "Aucun doublon retrouve."
        }
    }
}//rss

