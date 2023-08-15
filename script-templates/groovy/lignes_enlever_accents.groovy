/**:encoding=UTF-8:
 * Pour chaque ligne du fichier d'entrée, enlever les accents
 * et écrire la ligne sur le fichier de sortie.
 * Arguments :
 * -a:input : obligatoire. Chemin du fichier texte à lire
 * -a:output : obligatoire. Chemin du fichier texte à écrire
 * -a:encoding : optionnel. Nom de l'encodage à utiliser. Si absent, c'est
 *     l'encodage 'windows-1252' qui sera utilisé.
 *
 * Exemple avec un textes accentué encodé en windows-1252 sur windows, sans
 * utilisation de l'argument 'encoding' :
 * cd C:\t\test_accents
 * c:\app\gpmsi\exec -script c:\app\gpmsi\v1.1\scripts\groovy\lignes_enlever_accents.groovy -a:input bonjour-cp1252.groovy -a:output bonjour-cp1252_sans_accents.groovy
 *
 * Exemple avec un textes accentué encodé en utf-8 sur windows, avec
 * utilisation de l'argument 'encoding' avec 'utf-8' :
 * cd C:\t\test_accents
 * c:\app\gpmsi\exec -script c:\app\gpmsi\v1.1\scripts\groovy\lignes_enlever_accents.groovy -a:input bonjour-utf-8.groovy -a:output bonjour-utf-8_sans_accents.groovy -a:encoding utf-8
 */
import org.apache.commons.lang3.StringUtils

bw = null //le buffered writer que nous utiliserons pour ecrire

line {
    input args.input
    output args.output
    inputEncoding args.encoding ?: "windows-1252" //utiliser windows-1252 si pas d'encodage proposé

    onInit {
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath), inputEncoding))
    }

    onItem {item->
        //utilise StringUtils de apache commons qui transforme tous les caractères accentués en leur équivalent non accentué !
        bw << StringUtils.stripAccents(item.line)
        bw.newLine()
    }

    onEnd {
        bw.close()
    }
}
