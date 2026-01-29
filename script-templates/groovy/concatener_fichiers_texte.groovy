/**☺:encoding=UTF-8:
 * Concatener fichiers textes
 * Arguments :
 * -a:input chemin_du_fichier_entree (on peut le répéter plus que deux fois)
 * -a:output chemin_du_fichier_sortie
 * -f:sansLignesVides : si présent, les lignes vides seront ignorées
 * L'encodage est l'encodage système (pour la France, windows-1252).
 * Exemple :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\concatener_fichiers_texte.groovy -a:input fa.txt -a:input fb.txt -a:input fc.txt -a:output fabc.txt
 */

outf = new FileWriter(args.output)
inputs = args.input as String[]
removeBlankLines = flags.contains('sansLignesVides')

inputs.each {inputPath->
    line {
        input inputPath
        onItem {item->
            def str = item.line
            if (removeBlankLines && str.trim().length() == 0) return //ignorer lignes vides si demandé
            //envoyer la ligne
            outf << str << nl
        }
    }
}

outf.close()
