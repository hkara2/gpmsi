/**:encoding=UTF-8:
 * Lister un fichier "mono-niveau" (une simple table) vers son équivalent en csv
 * Arguments :
 * -a:input le fichier d'entree
 * -a:meta le nom du fichier de metadonnees a utiliser
 * -a:metasDir le chemin du repertoire qui contient les fichiers de metadonnees
 * -a:output le fichier de sortie
 * -f:txt forcer la sortie au format texte (par defaut le type prefere est utilise)
 *
 * Exemples d'execution :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\mono_vers_csv.groovy -a:input 910019447.2017.12.ium -a:meta ium2017 -a:metasDir C:\hkchse\pmsi\formats\pmsixml -a:output 910019447.2017.12.ium.csv
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\mono_vers_csv.groovy -a:input FICHCOMPATU -a:meta fichcompatu2017 -a:output FICHCOMPATU.csv
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\mono_vers_csv.groovy -a:input FICHCOMPMED -a:meta fichcompmed2017 -a:output FICHCOMPMED.csv
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\mono_vers_csv.groovy -a:input 910019447.2017.12.tra.txt -a:meta tra2016 -a:output 910019447.2017.12.tra.csv
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\mono_vers_csv.groovy -a:input 910019447.2019.6.ano.in.txt -a:meta anohospV013 -a:output 910019447.2019.6.ano.in.txt.csv
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\mono_vers_csv.groovy -a:input VIDHOSP_MCO.txt -a:meta vidhospV013 -a:output VIDHOSP_MCO.txt.csv
 * Harry Karadimas 2017 - 2020
 * v.2 200129 hk utilisation de la valeur formattée pour avoir des vraies dates
 */

mono {
    input args.input
    output args.output
    metaName args.meta
    metasDir args.metasDir

    headerSent = false

    onInit {
        ow = new FileWriter(outputFilePath)
    }

    onItem {item->
        def mono = item.mono
        def m = mono.meta
        def cm = m.childMetas
        def names = cm*.stdName
        def vals = names.collect {childName-> 
            // a faire : implementer le flag 'txt' pour avoir une sortie texte exclusive
            mono.getChild(childName)?.formattedValue //200129 hk utilisation de formattedValue
        }
        if (!headerSent) {
            ow << names.join(';') << "\r\n"
            headerSent = true
        }
        ow << vals.join(';') << "\r\n"
    }

    onEnd {
        ow.close()
    }

}
