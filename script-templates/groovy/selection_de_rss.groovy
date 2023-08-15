//:encoding=UTF-8:
//Emettre les RSS dont le numéro administratif local (nadl) n'est
//pas dans la liste des exclusions, ou bien est dans la liste des
//inclusions, selon l'argument qui est passé.
//La liste des exclusions a priorité sur celle des inclusions.
//C'est à dire que si un nadl est dans les deux listes, il sera exclu.
//Les nadl sont convertis en numerique en interne.
//Arguments :
//  input : fichier des RSS (groupes ou non) en entree
//  output : fichier qui contiendra uniquement les lignes RSS selectionnees
//  nadlincl : fichier csv qui contient la colonne des nadl a inclure (le nom de colonne doit être "nadl")
//  nadlexcl : fichier csv qui contient la colonne des nadl a exclure (le nom de colonne doit être "nadl")
//Exemple :
//Ne garder que les lignes de RUM qui concerne les dossiers dont les numéros
//sont dans le fichier err23.txt :
//C:\Local\GROUPAGE\2019\M04\190513-d\CDP190513>gpmsi -script C:\hkchse\dev\chse-gpmsi\scripts\selection-de-rss.groovy -a:input 042019_GRP019fg1819.txt -a:output grp_err23.txt -a:nadlincl err23.txt

import fr.karadimas.gpmsi.StringTable
import fr.karadimas.gpmsi.CsvDestination
import fr.karadimas.gpmsi.StringTransformable

nadlinclCol = null
nadlexclCol = null

rss {
    name 'Selection des RUMs'

    input args['input']

    output args['output']

    onInit {
        nadlexclFilePath = args['nadlexcl']
        def tonum = {s -> "" + (s as int)} as StringTransformable //pour transformer en numerique
        if (nadlexclFilePath == null) {
            nadlexcl = null
        }
        else {
            nadlexcl = new StringTable(new File(nadlexclFilePath), "utf-8", 0)
            nadlexclCol = nadlexcl.getColumnNumber("nadl") //par defaut la recherche n'est pas sensible a la casse
            if (nadlexclCol < 0) throw new Exception("Colonne non trouvee : nadl")
            nadlexcl.transform(nadlexclCol, tonum)
        }
        nadlinclFilePath = args['nadlincl']
        if (nadlinclFilePath == null) {
            nadlincl = null
        }
        else {
            nadlincl = new StringTable(new File(nadlinclFilePath), "utf-8", 0)
            nadlinclCol = nadlincl.getColumnNumber("nadl") //par defaut la recherche n'est pas sensible a la casse
            if (nadlinclCol < 0) throw new Exception("Colonne non trouvee : nadl")
            nadlincl.transform(nadlinclCol, tonum)
        }
        println "input : ${inputFilePath}"
        println "output : $outputFilePath"
        if (nadlexcl != null) println "fichier des nadl a exclure : ${nadlexclFilePath}"
        if (nadlincl != null) println "fichier des nadl a inclure : ${nadlinclFilePath}"
        if (nadlincl != null) {
            def sb = new StringBuffer()
            nadlincl.prettyPrintTo sb
            //println "Table des nadl a inclure : \r\n$sb"
        }
        fout = new FileWriter(new File(outputFilePath))
    }

    onItem {item->
        def rum = item.rum
        def nadl = rum.txtNADL //prendre l'nadl (numero adm local) du rum
        nadl = "" + (nadl as int) //le convertir en nombre pour eviter les confusions
        def emit = true //par defaut on envoie le RSS
        if (nadlincl != null) {

            //Si il existe un fichier des nadl a inclure et que le nadl courant n'y est pas, ne pas émettre le RUM
            if (nadlincl.findRow(nadlinclCol as int, nadl) < 0) emit = false;
        }
        if (nadlexcl != null) {
            //Si il existe un fichier des nadl a exclure et que le nadl courant y est, ne pas émettre le RUM
            if (nadlexcl.findRow(nadlexclCol as int, nadl) >= 0) emit = false;
        }
        //emettre la ligne du RUM si emit est true
        if (emit) { fout.write(item.line); fout.write("\r\n"); }
    }//onItem

    onEnd {
        fout.close()
    }
}