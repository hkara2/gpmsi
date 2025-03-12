// :encoding=utf-8:
/**
 * Exporter quelques champs du RSS vers un fichier csv
 * arguments :
 * -a:input
 * -a:output
 * -f:libelles : ajout de colonnes pour les libelles des codes
 *
 * La CIM 10 au format csv doit être dans le fichier cim10_utf8.csv,
 * dans un sous-répertoire .gpmsi, lui-même situé dans le home de 
 * l'utilisateur (~ sous unix, %userprofile% sous Windows NT)
 * Exemple d'exécution :
 * C:\Local\GROUPAGE\2019\M05\190609-d\DXC190609>gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rss-vers-csv.groovy -a:input MCO_19M05_RSSG_20190609111205.txt -a:output MCO_19M05_RSSG_20190609111205.csv
 *
 * #190802 hk ajout du drapeau 'libelles' pour avoir les libelles des codes, qui sont lourds et posent probleme.
 * #220608 hk ajout d'une colonne "parcours" pour avoir le parcours de soins
 * #230327 hk ajout de deux colonnes dadm et dsor pour dates admission a l'hopital et sortie de l'hopital
 * #250307 hk ajout de la colonne psur (passage par un service d'urgences) et np (non programmé)
 */
import groovy.xml.XmlSlurper
import groovy.time.TimeCategory

import fr.gpmsi.StringTable
import fr.gpmsi.CsvDestination
import fr.gpmsi.StringTransformable

def nomCim(cde) {
  if (cde == null || cde.equals("")) return ""
  def libl = cim10.find('CDE', cde.trim(), 'LIBL')
  if (libl == null) return ""
  return libl
}

def nomCcam(cde) {
  if (cde == null || cde.equals("")) return ""
  def libc = ccam48.find('CDE', cde.trim(), 'LIBC')
  if (libc == null) return ""
  return libc
}

def listerDiags(da) {
    StringBuffer sb = new StringBuffer()
    da.txtTDA.each {tda->
        if (sb.length() > 0) sb << "\r\n"
        sb << tda << " " << nomCim(tda.toUpperCase())
    }
    sb.toString()
}

def listerActes(za) {
    StringBuffer sb = new StringBuffer()
    za.txtCCCA.each {ccca->
        if (sb.length() > 0) sb << "\r\n"
        sb << ccca << nomCcam(ccca.toUpperCase())
    }
    sb.toString()
}

rumsParNrss = [:] //map globale des RUMS par numero de RSS
dadmParNrss = [:] //dates d'admisssion par numero de RSS
dsorParNrss = [:] //dates de sortie par numero de RSS

libelles = false
rums = [] //liste des RUMs des RSS dans l'ordre où ils sont rencontrés

if (flags.contains('libelles')) libelles = true
    
rss {
    name 'Selection des RUMs'
    
    input args['input']
        
    onInit {
    }
    
    onItem {item->
        def rum = item.rum
        def nrss = rum.txtNRSS
        def listeRss
        if (rumsParNrss.containsKey(nrss)) {
            listeRss = rumsParNrss[nrss]
        }
        else {
            listeRss = []
            rumsParNrss[nrss] = listeRss
        }
        listeRss << rum
        rums << rum
        //gestion date d'admission dans l'hôpital
        def deum = rum.DEUM.toDate()
        def dadm = dadmParNrss[nrss]
        if (dadm == null) dadmParNrss[nrss] = deum
        else {
            if (deum.before(dadm)) dadmParNrss[nrss] = deum
        }
        //gestion date de sortie de l'hôpital
        def dsum = rum.DSUM.toDate()
        def dsor = dsorParNrss[nrss]
        if (dsor == null) dsorParNrss[nrss] = dsum
        else {
            if (dsum.after(dsor)) dsorParNrss[nrss] = dsum
        }
    }//onItem
    
    onEnd {
    }
}

single {
    
  output args['output']

  onInit {
      outp = new CsvDestination(new File(outputFilePath), "cp1252")
      outp.f 'nadl'   //numéro administratif de dossier local (NDA)
      outp.f 'nrss'   //numéro du RSS
      outp.f 'nrum'   //numéro du RUM
      outp.f 'nadlq'  //nadl avec "quote" (apostrophe) pour garder le zéro initial
      outp.f 'ghm'    //numéro de GHM
      outp.f 'dnais'  //date de naissance
      outp.f 'sexe'   //sexe
      outp.f 'num'    //numéro d'UM
      //outp.f 'tald'
      outp.f 'deum'   //date d'entrée dans l'UM
      outp.f 'meum'   //mode d'entrée dans l'UM
      outp.f 'prov'   //provenance
      outp.f 'psur'   //passage par une structure des urgences
      outp.f 'dsum'   //date de sortie de l'UM
      outp.f 'msum'   //mode de sortie de l'UM
      outp.f 'dest'   //destination
      outp.f 'dsu'     //duree de sejour dans l'UM
      outp.f 'np'      //non programmé
      //outp.f 'cpre'
      //outp.f 'pnne'
      //outp.f 'ageg'
      //outp.f 'ddr'
      outp.f 'nbse'  //nombre de séances
      outp.f 'nda'   //nombre de diagnostics associés significatifs
      outp.f 'ndad'  //nombre de diagnostics associés documentaires
      outp.f 'nza'   //nombre de zones d'actes
      outp.f 'dp'    //diagnostic principal
      if (libelles) outp.f 'libldp'  //libellé du dp
      outp.f 'dr'    //diagnostic relié
      if (libelles) outp.f 'libldr'  //libellé du DR
      outp.f 'igs2'  //IGS2
      //outp.f 'ccrs'
      //outp.f 'tyma'
      //outp.f 'tydo'
      //outp.f 'numi'
      //outp.f 'nivg'
      //outp.f 'apivg'
      //outp.f 'nnva'
      outp.f 'dascs' //diagnostics associés concaténés
      if (libelles) outp.f 'das' //libellés des das concaténés
      outp.f 'actscs' //actes concaténés
      if (libelles) outp.f 'acts' //libellés des actes concaténés
      outp.f 'parcours' //parcours de soins (UFS dans l'ordre du fichier)
      outp.f 'dadmh'    //date d'admission dans l'hôpital
      outp.f 'dsorh'    //date de sortie de l'hôpital
      outp.f 'dsh'      //durée du séjour à l'hôpital
      outp.f 'cat'      //categorie (C,M,K)
      outp.f 'racg'     //racine GHM
      outp.f 'sev'
      outp.endRow()
      def uh = System.getProperty('user.home')
      //charger cim10 en encodage utf8, avec indexation de la colonne 0
      cim10 = new StringTable(new File(uh + "\\.gpmsi\\cim\\cim10_utf8.csv"), "utf-8", 0)
      ccam48 = new StringTable(new File(uh + "\\.gpmsi\\ccam\\ccam_utf8.csv"), "utf-8", 0)
  }
  
  onItem { item ->  
    rums.each() {rum ->
        def nadl = rum.txtNADL          
        outp.f "$nadl" //envoyer nadl brut
        def nrss = rum.txtNRSS
        outp.f nrss
        def nrum = rum.txtNRUM
        outp.f nrum
        outp.f "'$nadl" //ajouter une apostrophe pour forcer le mode texte (garde les zéros initiaux)
        def ghm = ""
        if (rum.NCMD != null) ghm = rum.txtNCMD + rum.txtNGHM
        outp.f ghm
        def dnais = rum.DNAIS.toDate() //Date de naissance  DNAIS
        outp.f frenchDateFormat.format(dnais)
        def sexe  = rum.txtSEXE//Sexe   SEXE
        outp.f sexe
        def num  = rum.txtNUM//Numéro de l'unité médicale   NUM
        outp.f num
        //def tald  = rum.txtTALD//Type d'autorisation du lit dédié TALD
        //outp.f tald
        def deum  = rum.DEUM.toDate() //Date d'entrée dans l'unité médicale DEUM
        outp.f frenchDateFormat.format(deum)
        def meum  = rum.txtMEUM//Mode d'entrée dans l'unité médicale    MEUM
        outp.f meum
        def prov  = rum.txtPROV//Provenance (si mode d'entrée est mutation, transfert ou domicile)  PROV
        outp.f prov
        def psur  = rum.txtPSUR //passage par une structure des urgences
        outp.f psur
        def dsum  = rum.DSUM.toDate() //Date de sortie de l'unité médicale  DSUM
        if (dsum == null) outp.f '' else outp.f frenchDateFormat.format(dsum)
        def msum  = rum.txtMSUM //Mode de sortie de l'unité médicale    MSUM
        outp.f msum
        def dest  = rum.txtDEST //Destination (si mode de sortie est mutation, transfert ou domicile)   DEST
        outp.f dest
        //calcul duree de sejour dans l'UM (dsu), en jours
        use(TimeCategory) {
          if (deum == null || dsum == null) outp.f '' else {
              def dur = dsum - deum
              outp.f dur.days
          }
        }
        outp.f rum.txtNP   //non programmé
        //def cpre  = rum.txtCPRE //Code postal de résidence (ou 99 suivi du code Insee du pays pour les patients résidant hors de France)  CPRE
        //outp.f cpre
        //def pnne  = rum.txtPNNE //Poids du nouveau-né à l'entrée de l'unité médicale (en grammes) PNNE
        //outp.f pnne
        //def ageg  = rum.txtAGEG //Age gestationnel    AGEG
        //outp.f ageg
        //def ddr  = rum.DDR.toDate() //Date des dernières règles   DDR
        //outp.f ddr == null ? '' : frenchDateFormat.format(ddr)
        def nbse  = rum.txtNBSE //Nombre de séances NBSE
        outp.f nbse
        def nda  = rum.txtNDA //Nombre de diagnostics associés (nDA) dans ce RUM    NDA
        outp.f nda
        def ndad  = rum.txtNDAD //Nombre de données à visée documentaire (nDAD) dans ce RUM NDAD
        outp.f ndad
        def nza  = rum.txtNZA //Nombre de zone d'actes (nZA) dans ce RUM    NZA
        outp.f nza
        def dp  = rum.txtDP //Diagnostic principal (DP) DP
        outp.f dp
        if (libelles) {
            def libldp = nomCim(dp)
            outp.f libldp
        }
        def dr  = rum.txtDR //Diagnostic relié (DR) DR
        outp.f dr
        if (libelles) {
            def libldr = nomCim(dr)
            outp.f libldr
        }
        def igs2 = rum.txtIGS2 //IGS 2  IGS2
        outp.f igs2
        //def ccrs = rum.txtCCRS //Confirmation du codage du RSS    CCRS
        //outp.f ccrs
        //def tyma = rum.txtTYMA //Type de machine en radiothérapie TYMA
        //outp.f tyma
        //def tydo = rum.txtTYDO //Type de dosimétrie   TYDO
        //outp.f tydo
        //def numi = rum.txtNUMI //Numéro d'innovation  NUMI
        //outp.f numi
        //def nivg = rum.txtNIVG //Nombre d’IVG antérieures NIVG
        //outp.f nivg
        //def apivg = rum.txtAPIVG //Année de l’IVG précédente  APIVG
        //outp.f apivg
        //def nnva = rum.txtNNVA //Nombres de naissances vivantes antérieures   NNVA
        //outp.f nnva
        def dascs = rum.DA.txtTDA*.trim().join(',')
        outp.f dascs
        if (libelles) {
            def das = listerDiags(rum.DA)
            outp.f das
        }
        def actscs = rum.ZA.txtCCCA*.trim().join(',')
        outp.f actscs
        if (libelles) {
            def acts = listerActes(rum.ZA)
            outp.f acts
        }
        //ajout parcours de soins
        def parcours = []
        rumsParNrss[nrss].each {r->
            if (r.txtMEUM == '8' && r.txtPROV == '5') parcours << 'URG'
            parcours << r.txtNUM
        }
        outp.f "'" + parcours*.trim().join('-')
        //ajout date admission a l'hopital
        def dadm = dadmParNrss[nrss]
        if (dadm == null) outp.f '' else outp.f frenchDateFormat.format(dadm)
        //ajout date sortie de l'hopital
        def dsor = dsorParNrss[nrss]
        if (dsor == null) outp.f '' else outp.f frenchDateFormat.format(dsor)
        //ajout duree sejour a l'hopital, en jours
        use(TimeCategory) {
          if (dadm == null || dsor == null) outp.f ''
          else {
              def dur = dsor - dadm
              //println "dur class : $dur.class"
              outp.f dur.days
          }
        }
        //envoi du numero de GHMN decomposé pour pouvoir filtrer et trier
        def nghm = rum.txtNGHM + '    '
        outp.f nghm[0]    //categorie
        outp.f nghm[1..2] //racine de GHM
        outp.f nghm[3]    //severite
        outp.endRow()
    }// rums.each
    
  }
  onEnd {
    outp.close()
  }
}
