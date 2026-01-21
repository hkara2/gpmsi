/**:encoding=UTF-8:
 * Traiter le fichier ListeDiags.csv pour regrouper ensemble toutes les listes
 * et passer de :
CMD;Liste;Libellé liste;Code;Libellé code
01;D-0101;Migraines et céphalées;F07.2;Syndrome post commotionnel
01;D-0101;Migraines et céphalées;G43.0;Migraine sans aura [migraine commune]
01;D-0102;Convulsions hyperthermiques;R56.0;Convulsions fébriles
01;D-0103;Épilepsies;F80.3;Aphasie acquise avec épilepsie [Landau Kleffner]
01;D-0103;Épilepsies;G40.0;Épilepsie et syndromes épileptiques idiopathiques définis par leur localisation (focale  partielle) avec crises à début focal
01;D-0103;Épilepsies;G40.1;Épilepsie et syndromes épileptiques symptomatiques définis par leur localisation (focale  partielle) avec crises partielles simples
 * à :
cmd;liste;codes
01;D-0101;F07.2 G43.0
01;D-0102;R56.0
01;D-0103;F80.3 G40.0 G40.1
 *
 * Exemple execution :
 * cd C:\Local\GHM\MANUEL DES GHM\2025\volume2cmd
 * %GPMSI_BASE%\v2.1\gpmsi -extracp file:/%GPMSI_BASE%/v2.1/scripts/groovy -scripturi fr/gpmsi/ghm/volume2cmd_liste_diags_vers_gpmsi.groovy -a:input ListeDiags.csv -a:output listes-d.csv
 */
package fr.gpmsi.ghm

import fr.gpmsi.StringTable
import fr.gpmsi.CsvDestination

//nld signifie "Nom Liste D", ce qui entraîne moins de confusion avec "liste"
listeNld = [] as SortedSet
cmdsParNld = [:]
listesDiagsParNld = [:]

stListeDiags = new StringTable("ListeDiags", new File(args.input))

stListeDiags.each {row->
    def cmd = row.CMD
    def nld = row.Liste
    def code = row.code
    //println "$cmd : $nld : $code"
    listeNld << nld
    cmdsParNld[nld] = cmd
    def listeDiag = listesDiagsParNld[nld]
    if (listeDiag == null) listeDiag = []
    listeDiag << code
    listesDiagsParNld[nld] = listeDiag
}

dest = new CsvDestination(new File(args.output))
dest.f 'cmd'
dest.f 'liste'
dest.f 'code'
dest.endRow()
listeNld.each {nld->
    dest.f cmdsParNld[nld]
    dest.f nld
    dest.f listesDiagsParNld[nld].join(' ') //envoyer la liste des diags séparés par des espaces
    dest.endRow()
}
dest.close()

print "Fin traitement liste ghms"


