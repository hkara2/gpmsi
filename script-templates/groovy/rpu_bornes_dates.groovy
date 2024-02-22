/**☺:encoding=UTF-8:
 * Analyser le fichier des RPU et donner date entreee min, date entree max,
 * date sortie min, date sortie max.
 *
 * Exemple :
 * cd C:\Local\RPU\exports\2020\01-03
 * gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rpu_bornes_dates.groovy ^
 * -a:input RPU_7002_20200318145530.xml
 *
 */
import groovy.xml.XmlSlurper
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

isoDtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME
frDtf = DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm')

/**
 * Transformer une liste d'éléments enfants en une chaîne de caractères.
 * Utilisé pour avoir la liste des actes et la liste des da.
 */
def toList(parentElement, childName) {
    def sb = "" << ""
    sb << parentElement[childName].join(', ')
    sb.toString()
}

def parseDate(str) {
    if (str == null || str.trim() == '') return null
    return LocalDateTime.parse(str, frDtf)
}

//def outfw = new BufferedWriter(new FileWriter(args.output))
//envoi de l'en-tête
//outfw << "finess;cp;commune;naissance;sexe;entree;mode_entree;provenance;transport;transport_pec;motif;gravite;dp;liste_da;liste_actes;sortie;mode_sortie;destination;orient\r\n"

// deminStr = args['demin']
// if (deminStr == null) {
    // throw new Exception("demin (date d'entree minimum) manquant")
// }

demin = null

// println "demin : $demin"
// 
// demaxStr = args['demax']
// if (demaxStr == null) {
    // throw new Exception("demax (date de d'entree maximum) manquant")
// }

demax = null

dsmin = null
dsmax = null

//println "demax : $demax"

def oscour = new XmlSlurper().parse(new File(args.input))
def finess = oscour.ETABLISSEMENT.FINESS
oscour.PASSAGES.PATIENT.each {p->
    /* Exemple de RPU :
      <CP>91150</CP> <!-- code postal de résidence. Format : 5 caractères -->
      <COMMUNE>ETAMPES</COMMUNE> <!-- nom de la commune de résidence. Format : caractères -->
      <NAISSANCE>02/03/1945</NAISSANCE> <!-- date de naissance. Format : JJ/MM/AAAA. vide correspond à incertain -->
      <SEXE>M</SEXE> <!-- sexe. Format : 1 caractère. Codes : M - masculin. F - féminin. I - inconnu -->
      <ENTREE>20/03/2020 14:44:00</ENTREE> <!-- date et heure d’entrée. Format : JJ/MM/AAAA hh:mm --> 
      <MODE_ENTREE></MODE_ENTREE> <!-- Mode d’entrée PMSI. Format : 1 caractère. Codes : 6 - mutation. 7 - transfert. 8 - domicile -->
      <PROVENANCE></PROVENANCE> <!-- Provenance PMSI. Format : 1 caractère. Codes : 1 - mutation ou transfert du MCO. 2 - mutation ou transfert du SSR. 3 - mutation ou transfert du SLD. 4 - mutation ou transfert du PSY. 5 - PE autre qu’organisationnelle. 6 - hospitalisation à domicile 2. . 2. Les codes 6 et 7 correspondent à des codes du PMSI et ont été ajoutés à la définition du RPU le 16/09/13.. InVS/DCAR/SSI SYSTÈME D’INFORMATION SUR L’ACTIVITÉ DES SERVICES D’URGENCES HOSPITALIERS. . 9. 7 - structure d’hébergement médicosociale. 8 - PE organisationnelle -->
      <TRANSPORT>PERSO</TRANSPORT> <!-- Mode de transport. Format : 5 caractères. Codes : PERSO - moyen personnel. AMBU - ambulance publique ou privée. VSAB - véhicule de secours et d’aide aux blessés. SMUR - véhicule de Service Mobile d’Urgence et de Réanimation. HELI - hélicoptère. FO - force de l’ordre -->
      <TRANSPORT_PEC>AUCUN</TRANSPORT_PEC> <!-- Mode de prise en charge pendant le transport. Format : 7 caractères. Codes : MED - médicalisée. PARAMED - paramédicalisée. AUCUN - sans prise en charge -->
      <MOTIF>plaie superficielle index droit</MOTIF> <!--motif de recours aux urgences. Format : alphanumérique. Codes : thésaurus SFMU -->
      <GRAVITE>1</GRAVITE> <!-- classification CCMU modifiée. Format : 1 caractère. Codes : 1 - état lésionnel ou pronostic fonctionnel jugé stable après le premier examen clinique éventuellement complété d’actes diagnostiques réalisés et interprétés au lit du malade, abstention d’actes complémentaires ou de thérapeutique. P - idem CCMU 1 avec problème dominant psychiatrique ou psychologique isolé ou associé à une pathologie somatique jugée stable. 2 - état lésionnel ou pronostic jugé stable, réalisation d’actes complémentaires aux urgences en dehors des actes diagnostiques éventuellement réalisés et interprétés au lit du malade et / ou d’actes thérapeutiques. 3 - état lésionnel ou pronostic fonctionnel jugé susceptible de s’aggraver aux urgences sans mettre en jeu le pronostic vital. 4 - situation pathologique engageant le pronostic vital aux urgences sans manœuvre de réanimation initiée ou poursuivie dès l’entrée aux urgences. 5 - situation pathologique engageant le pronostic vital aux urgences avec initiation ou poursuite de manœuvres de réanimation dès l’entrée aux urgences. D - patient décédé à l’entrée aux urgences sans avoir pu bénéficier d’initiation ou poursuite de manœuvres de réanimation aux urgences -->
      <DP></DP> <!-- diagnostic principal. Format : CIM 10  -->
      <LISTE_DA>
        <DA>I10</DA> <!-- Diagnostic associé. Format : CIM 10 -->
        <DA>E440</DA> <!-- InVS/DCAR/SSI SYSTÈME D’INFORMATION SUR L’ACTIVITÉ DES SERVICES D’URGENCES HOSPITALIERS...10. Diagnostic associé. Format : CIM 10  -->
      </LISTE_DA>
      <LISTE_ACTES >
        <ACTE>DEQP003</ACTE> <!-- Acte réalisé aux urgences. Format : CCAM  -->
      </LISTE_ACTES>
      <SORTIE>20/03/2020 14:45:00</SORTIE> <!-- Date et heure de sortie. Format : JJ/MM/AAAA hh :mm -->
      <MODE_SORTIE>8</MODE_SORTIE> <!-- Mode de sortie PMSI. Format : 1 caractère. Codes : 6 – mutation. 7 – transfert. 8 – domicile. 9 – décès -->
      <DESTINATION></DESTINATION> <!-- Destination PMSI. Format : 1 caractère. Codes : 1 – hospitalisation MCO. 2 – hospitalisation SSR. 3 – hospitalisation SLD. 4 – hospitalisation PSY. 6 – hospitalisation à domicile. 7 – structure d’hébergement médicosociale -->
      <ORIENT></ORIENT>
      */
  // def cp = p.CP
  // def commune = p.COMMUNE
  // def naissance = p.NAISSANCE
  // def sexe = p.SEXE
  def entree = p.ENTREE
  def dateEntree = parseDate(entree as String)
  //println dateEntree
  // def mode_entree = p.MODE_ENTREE
  // def provenance = p.PROVENANCE
  // def transport = p.TRANSPORT
  // def transport_pec = p.TRANSPORT_PEC
  // def motif = p.MOTIF
  // def gravite = p.GRAVITE
  // def dp = p.DP
  // def liste_da = toList(p.LISTE_DA, 'DA')
  // def liste_actes = toList(p.LISTE_ACTES, 'ACTE')
  def sortie = p.SORTIE
  def dateSortie = parseDate(sortie as String)
  //println dateSortie
  // def mode_sortie = p.MODE_SORTIE
  // def destination = p.DESTINATION
  // def orient = p.ORIENT

  if (demin == null || (dateEntree != null && dateEntree < demin)) demin = dateEntree
  if (demax == null || (dateEntree != null && dateEntree > demax)) demax = dateEntree
  if (dsmin == null || (dateSortie != null && dateSortie < dsmin)) dsmin = dateSortie
  if (dsmax == null || (dateSortie != null && dateSortie > dsmax)) dsmax = dateSortie

  //outfw << "$finess;$cp;$commune;$naissance;$sexe;$entree;$mode_entree;$provenance;$transport;$transport_pec;$motif;$gravite;$dp;$liste_da;$liste_actes;$sortie;$mode_sortie;$destination;$orient\r\n"
  if (demin <= dateEntree && dateEntree <= demax) {
//      outfw << "$entree;$sortie\r\n"
  }
}
//outfw.close()
println "date entree, minimum : $demin, maximum : $demax"
println "date sortie, minimum : $dsmin, maximum : $dsmax"

