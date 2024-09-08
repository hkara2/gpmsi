/**:encoding=UTF-8: < - définit l encodage dans jEdit
 * Lister les patients du RPU dans un fichier Excel 2007 et ultérieur (xlsx).
 * Exemple :
 * cd C:\Local\RPU\exports\2020\01-03
 * gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rpu_vers_xlsx.groovy -a:input RPU_7002_20200318145530.xml -a:output RPU_7002_20200318145530.csv
 */
import java.text.SimpleDateFormat
import groovy.xml.XmlSlurper
import fr.gpmsi.poi.XlsxHelper;
import static fr.gpmsi.StringUtils.isTrimEmpty

//actes CCMU2+
actesCcmu2plus = ['BACA002', 'BACA005', 'BACA007', 'BACA008', 'BAGA001',
'CAJA002', 'GAJA002', 'HAJA003', 'HAJA006', 'HAJA007', 'HAJA009', 'HAJA010',
'QCJA001', 'QZJA021', 'QZJA022', 'QAJA004', 'QAJA006', 'QAJA012', 'QZJA016',
'QZJA012', 'QZJA001', 'MEEP001', 'MEEP002', 'MFEP001', 'MFEP002', 'MCEP001',
'MCEP002', 'MDEP001', 'MDEP002', 'MGEP002', 'MHEP001', 'MHEP002', 'MHEP004',
'NFEP001', 'NFEP002', 'NDEP001', 'NGEP001', 'LBEP005', 'QAGA001', 'QAGA004',
'QZGA003', 'QZGA006', 'CAJA001', 'MJFA003', 'MJPA010', 'EGFA007', 'EGJA001',
'QZJA011', 'JDJD002', 'GGJB001', 'GABD001', 'GABD002', 'GGHB001', 'GGJB002',
'HPHB003', 'HPJB001', 'JDLF001', 'NZHB002', 'NZJB001', 'MZHB002', 'MZJB001',
'QZMP001', 'QZFA020', 'QZFA029', 'QZFA039', 'GELD005' ] as Set

dfNaiss = new SimpleDateFormat('dd/MM/yyyy')
dfEntreeSortie = new SimpleDateFormat('dd/MM/yyyy HH:mm')

/**
 * Get a diff between two dates
 * @param date1 the oldest date
 * @param date2 the newest date
 * @param timeUnit the unit in which you want the diff
 * @return the diff value, in the provided unit
 */
public static double getDateDiffInFractDays(Date date1, Date date2) {
    long diffMs = date2.getTime() - date1.getTime();
    return diffMs / (24d * 60d * 60d * 1000d);
}

/**
 * Transformer une liste d'éléments enfants en une chaîne de caractères.
 * Utilisé pour avoir la liste des actes et la liste des da.
 */
def toList(parentElement, childName) {
    def sb = new StringBuffer()
    sb << parentElement[childName].join(', ')
    sb.toString()
}

/** Renvoie true si au moins un des enfants fait partie de la liste actesCcmu2plus */
def isCcmu2plus(parentElement, childName) {
    parentElement[childName].any {nd -> actesCcmu2plus.contains(nd.text())}
}

destFile = new File(args.output);
classeur = new XlsxHelper("RPUs");

//Ajouter les en-tetes
enTetes = 'finess,cp,commune,naissance,sexe,entree,mode_entree,sortie,mode_sortie,dursej,provenance,transport,transport_pec,motif,gravite,dp,liste_da,liste_actes,destination,orient,acteccmu2p'
enTetes.split(',').each {enTete -> classeur.addCell(enTete) }
classeur.newRow()

def oscour = new XmlSlurper().parse(new File(args.input))
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
  def finess = oscour.ETABLISSEMENT.FINESS
  classeur.addCell(finess.text())
  def cp = p.CP.text()
  classeur.addCell(cp)
  def commune = p.COMMUNE.text()
  classeur.addCell(commune)
  def naissance = p.NAISSANCE.text()
  if (isTrimEmpty(naissance)) classeur.addCell('')
  else classeur.addCell(dfNaiss.parse(naissance), "dd/mm/yyyy")
  def sexe = p.SEXE.text()
  classeur.addCell(sexe)
  def entree = p.ENTREE.text()
  def entreeDateTime = isTrimEmpty(entree) ? null : dfEntreeSortie.parse(entree)
  if (isTrimEmpty(entree)) classeur.addCell('')
  else classeur.addCell(dfEntreeSortie.parse(entree), "dd/mm/yyyy hh:mm")
  def mode_entree = p.MODE_ENTREE.text()
  classeur.addCell(mode_entree)
  def sortie = p.SORTIE.text()
  def sortieDateTime = isTrimEmpty(sortie) ? null : dfEntreeSortie.parse(sortie)
  if (isTrimEmpty(sortie)) classeur.addCell('')
  else classeur.addCell(dfEntreeSortie.parse(sortie), "dd/mm/yyyy hh:mm")
  def mode_sortie = p.MODE_SORTIE.text()
  classeur.addCell(mode_sortie)
  //ajout duree sejour (hk 240220)
  def dursej = 999.0
  if (entreeDateTime != null && sortieDateTime != null) {
      dursej = getDateDiffInFractDays(entreeDateTime, sortieDateTime)
  }
  classeur.addCell(dursej)
  def provenance = p.PROVENANCE.text()
  classeur.addCell(provenance)
  def transport = p.TRANSPORT.text()
  classeur.addCell(transport)
  def transport_pec = p.TRANSPORT_PEC.text()
  classeur.addCell(transport_pec)
  def motif = p.MOTIF.text()
  classeur.addCell(motif)
  def gravite = p.GRAVITE.text()
  classeur.addCell(gravite)
  def dp = p.DP.text()
  classeur.addCell(dp)
  def liste_da = toList(p.LISTE_DA, 'DA')
  classeur.addCell(liste_da)
  def liste_actes = toList(p.LISTE_ACTES, 'ACTE')
  classeur.addCell(liste_actes)
  def destination = p.DESTINATION.text()
  classeur.addCell(destination)
  def orient = p.ORIENT.text()
  classeur.addCell(orient)
  def acteccmu2p = isCcmu2plus(p.LISTE_ACTES, 'ACTE') ? "VRAI" : "FAUX"
  classeur.addCell(acteccmu2p)

  classeur.newRow()
}

classeur.setOutput(destFile);
classeur.writeFileAndClose();
