// :encoding=utf-8:
/**
 * Exporter les RSS vers un fichier Excel xlsx.
 * arguments :
 * -a:input chemin_fichier
 * -a:output chemin_fichier
 * -f:paslibcim      Ne pas mettre les libelles CIM
 * -f:paslibccam     Ne pas mettre les libelles CCAM
 *
 * La CIM 10 au format csv doit être dans le fichier cim/cim10_utf8.csv,
 * dans un sous-répertoire .pmsixml, lui-même situé dans le home de
 * l'utilisateur (~ sous unix, %userprofile% sous Windows NT)
 * Idem pour la CCAM qui doit etre dans un fichier nomme ccam/ccam_descr_pmsi_utf8.csv
 * L'envoi vers un fichier xlsx préserve mieux le contenu des cellules que
 * l'envoi vers du csv ; cependant le traitement est plus long, et la première
 * ouverture du fichier xlsx généré est très longue.
 */
import groovy.xml.XmlSlurper

import fr.gpmsi.StringTable
import fr.gpmsi.CsvDestination
import fr.gpmsi.StringTransformable
import fr.gpmsi.poi.XlsxHelper
import static fr.gpmsi.StringUtils.isEmpty

pasDeLibelleCim = false
pasDeLibelleCcam = false

def nomCim(cde) {
  if (cde == null || cde.equals("")) return ""
  def libl = cim10.find('CDE', cde.trim(), 'LIBL')
  if (libl == null) return ""
  return libl
}

/** retrouver le nom CCAM qui correspond au code passé en paramètre.
 * Utilise la CCAM descriptive à usage PMSI, la mettre à jour dès qu'il y en a
 * une nouvelle.
 */
def nomCcam(cde) {
  if (cde == null) return ""
  cde = cde.trim()
  if (cde.equals("")) return ""
  //attention lorsqu'il n'y a pas d'extension PMSI parfois on retrouve "-00", l'enlever si c'est le cas
  if (cde.endsWith('-00')) cde = cde[0..-4]
  def libc = ccam.find('CODEP', cde, 'LIBELLE')
  if (libc == null) return ""
  return libc
}

def listerDiags(da) {
    StringBuffer sb = new StringBuffer()
    da.txtTDA.each {tda->
        if (sb.length() > 0) sb << "\r\n"
        sb << tda << " " << (pasDeLibelleCim ? "" : nomCim(tda.toUpperCase()))
    }
    sb.toString()
}

def listerActes(za) {
    StringBuffer sb = new StringBuffer()
    za.txtCCCA.each {ccca->
        if (sb.length() > 0) sb << "\r\n"
        sb << ccca << " " << (pasDeLibelleCcam ? "" : nomCcam(ccca.toUpperCase()))
    }
    sb.toString()
}

// Limiter la longueur à 32700 pour éviter l'erreur  "The maximum length of cell contents (text) is 32767 characters"
def limit32K(str) {
    (str != null && str.length() > 32700) ? str.substring(0, 32700) : str
}

listeRumsParNrss = [:] //map globale des RUMS par numero de RSS

listeRss = []

ensembleRss = [] as Set

XlsxHelper xh = new XlsxHelper('RSS')

//Verifier que les fichiers nécessaires sont bien présents.
def uh = System.getProperty('user.home')
cimFile = new File(uh + "\\.gpmsi\\cim\\cim10_utf8.csv")
ccamFile = new File(uh + "\\.gpmsi\\ccam\\ccam_descr_pmsi_utf8.csv")
if (!cimFile.exists()) throw new Exception("L'exécution de ce script nécessite la CIM-10 dans le fichier $cimFile")
if (!ccamFile.exists()) throw new Exception("L'exécution de ce script nécessite la CCAM descriptive PMSI dans le fichier $ccamFile")
pasDeLibelleCcam = flags.contains('paslibccam')
pasDeLibelleCim = flags.contains('paslibcim')

//Reconstituer une liste de RUMs pour chaque numero de RSS
rss {
    name 'Selection des RUMs par RSS'

    input args['input']

    onItem {item->
        def rum = item.rum
        def nrss = rum.txtNRSS
        def listeRums
        if (listeRumsParNrss.containsKey(nrss)) {
            listeRums = listeRumsParNrss[nrss]
        }
        else {
            listeRums = []
            listeRumsParNrss[nrss] = listeRums
        }
        listeRums << rum
        if (!ensembleRss.contains(nrss)) {
            ensembleRss << nrss
            listeRss << nrss
        }
    }//onItem

}

xh.addCell('nrum')
xh.addCell('nadl')
xh.addCell('ghm')
xh.addCell('dnais')
xh.addCell('sexe')
xh.addCell('num')
//xh.addCell('tald')
xh.addCell('deum')
xh.addCell('meum')
xh.addCell('prov')
xh.addCell('psur')
xh.addCell('dsum')
xh.addCell('msum')
xh.addCell('dest')
//xh.addCell('cpre')
//xh.addCell('pnne')
//xh.addCell('ageg')
//xh.addCell('ddr')
xh.addCell('nbse')
xh.addCell('nda')
xh.addCell('ndad')
xh.addCell('nza')
xh.addCell('dp')
xh.addCell('libldp')
xh.addCell('dr')
xh.addCell('libldr')
xh.addCell('igs2')
//xh.addCell('ccrs')
//xh.addCell('tyma')
//xh.addCell('tydo')
//xh.addCell('numi')
//xh.addCell('nivg')
//xh.addCell('apivg')
//xh.addCell('nnva')
xh.addCell('dascs')
xh.addCell('das')
xh.addCell('actscs')
xh.addCell('acts')
xh.newRow()
//charger cim10 en encodage utf8, avec indexation de la colonne 0
cim10 = new StringTable(cimFile, "utf-8", 0)
//charger ccam en encodage utf8, avec indexation de la colonne 2 (CODEP, le code "plein", cad PMSI+extension)
ccam = new StringTable(ccamFile, "utf-8", 2)

//creation d'un style spécial pour autoriser le multiligne dans la cellule
styleMultiligne = xh.sheet.workbook.createCellStyle()
styleMultiligne.setWrapText(true);

listeRss.each() {nrss ->
    def listeRums = listeRumsParNrss[nrss]
    if (listeRums != null) {
        //pour chaque RUM, emettre les infos.
        listeRums.each {rum->
            def nrum = rum.txtNRUM
            xh.addCell(nrum)
            def nadl = rum.txtNADL
            xh.addCell(nadl)
            def ghm = ""
            if (rum.NCMD != null) ghm = rum.txtNCMD + rum.txtNGHM
            xh.addCell(ghm)
            def dnais = rum.DNAIS.toDate() //Date de naissance    DNAIS
            xh.addCell(dnais, 'dd/mm/yyyy')
            def sexe  = rum.txtSEXE//Sexe    SEXE
            xh.addCell(sexe)
            def num  = rum.txtNUM//Numéro de l'unité médicale    NUM
            xh.addCell(num)
            //def tald  = rum.txtTALD//Type d'autorisation du lit dédié    TALD
            //xh.addCell(tald)
            def deum  = rum.DEUM.toDate() //Date d'entrée dans l'unité médicale    DEUM
            xh.addCell((Date)deum, 'dd/mm/yyyy')
            def meum  = rum.txtMEUM//Mode d'entrée dans l'unité médicale    MEUM
            xh.addCell(meum)
            def prov  = rum.txtPROV//Provenance (si mode d'entrée est mutation, transfert ou domicile)    PROV
            xh.addCell(prov)
            def psur = rum.txtPSUR //Passage par une structure des urgences PSUR
            xh.addCell(psur)
            def dsum  = rum.DSUM.toDate() //Date de sortie de l'unité médicale    DSUM
            xh.addCell((Date)dsum, 'dd/mm/yyyy')
            def msum  = rum.txtMSUM //Mode de sortie de l'unité médicale    MSUM
            xh.addCell(msum)
            def dest  = rum.txtDEST //Destination (si mode de sortie est mutation, transfert ou domicile)    DEST
            xh.addCell(dest)
            //def cpre  = rum.txtCPRE //Code postal de résidence (ou 99 suivi du code Insee du pays pour les patients résidant hors de France)    CPRE
            //xh.addCell(cpre)
            //def pnne  = rum.txtPNNE //Poids du nouveau-né à l'entrée de l'unité médicale (en grammes)    PNNE
            //xh.addCell(pnne)
            //def ageg  = rum.txtAGEG //Age gestationnel    AGEG
            //xh.addCell(ageg)
            //def ddr  = rum.DDR.toDate() //Date des dernières règles    DDR
            //xh.addCell(ddr == null ? '' : frenchDateFormat.format(ddr))
            def nbse  = rum.NBSE.toInt() //Nombre de séances    NBSE
            xh.addCell(nbse)
            def nda  = rum.NDA.toInt() //Nombre de diagnostics associés (nDA) dans ce RUM    NDA
            xh.addCell(nda)
            def ndad  = rum.NDAD.toInt() //Nombre de données à visée documentaire (nDAD) dans ce RUM    NDAD
            xh.addCell(ndad)
            def nza  = rum.NZA.toInt() //Nombre de zone d'actes (nZA) dans ce RUM    NZA
            xh.addCell(nza)
            def dp  = rum.txtDP //Diagnostic principal (DP)    DP
            xh.addCell(dp)
            def libldp = nomCim(dp)
            xh.addCell(libldp)
            def dr  = rum.txtDR //Diagnostic relié (DR)    DR
            xh.addCell(dr)
            def libldr = nomCim(dr)
            xh.addCell(libldr)
            def igs2 = rum.IGS2.toInt() //IGS 2    IGS2
            xh.addCell(igs2)
            //def ccrs = rum.txtCCRS //Confirmation du codage du RSS    CCRS
            //xh.addCell(ccrs)
            //def tyma = rum.txtTYMA //Type de machine en radiothérapie    TYMA
            //xh.addCell(tyma)
            //def tydo = rum.txtTYDO //Type de dosimétrie    TYDO
            //xh.addCell(tydo)
            //def numi = rum.txtNUMI //Numéro d'innovation    NUMI
            //xh.addCell(numi)
            //def nivg = rum.txtNIVG //Nombre d’IVG antérieures    NIVG
            //xh.addCell(nivg)
            //def apivg = rum.txtAPIVG //Année de l’IVG précédente    APIVG
            //xh.addCell(apivg)
            //def nnva = rum.txtNNVA //Nombres de naissances vivantes antérieures    NNVA
            //xh.addCell(nnva)
            def dascs = rum.DA.txtTDA*.trim().join(',')
            xh.addCell(dascs)
            //les cellules suivantes sont multilignes, et donc ajoutees avec le style multiligne.
            def das = listerDiags(rum.DA)
            xh.addCell(das).setCellStyle(styleMultiligne)
            //ici on limite la longueur car cela peut devenir très long (réa...)
            def actscs = rum.ZA.txtCCCA*.trim().join(',')
            xh.addCell(limit32K(actscs)) //on ne met pas cette cellule en style multiligne car les lignes sont trop grandes sinon
            def acts = listerActes(rum.ZA)
            xh.addCell(limit32K(acts)) //on ne met pas cette cellule en style multiligne car les lignes sont trop grandes sinon
            xh.newRow()
        }//listeRums.each
    }
    else {
        //indiquer que rien n'a été retrouvé pour ce numéro de rss !
        //(Ne devrait jamais se produire normalement)
        xh.addCell(nrss)
        xh.addCell('?')
        xh.addCell('?')
        xh.newRow()
    }
}//~listeRss.each()

//ecrire le resultat dans le fichier de sortie
xh.setOutput(new File(args.output))
xh.writeFileAndClose()

