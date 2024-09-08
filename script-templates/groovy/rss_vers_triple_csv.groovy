// :encoding=utf-8:
/**
 * Exporter les RSS vers trois fichiers csv en utilisant un prefixe donne.
 * Les 3 fichiers seront : _rums.csv , _da.csv , _actes.csv
 * Le format de date utilise sera le format ISO 8601 qui est detecte automatiquement par R.
 *
 * arguments :
 * -a:input chemin_du_fichier_rss
 * -a:outprefix prefixe_des_fichiers_generes
 * -f:libelles : ajout de colonnes pour les libelles des codes
 *
 * La CIM 10 au format csv doit être dans le fichier cim10_utf8.csv,
 * dans un sous-répertoire .gpmsi, lui-même situé dans le home de 
 * l'utilisateur (~ sous unix, %userprofile% sous Windows NT)
 * Exemple d'exécution :
 * C:\Local\GROUPAGE\2019\M05\190609-d\DXC190609>gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\rss-vers-csv.groovy -a:input MCO_19M05_RSSG_20190609111205.txt -a:output MCO_19M05_RSSG_20190609111205.csv
 *
 * #221130 hk creation a partir du script rss_vers_csv.groovy
 */

import fr.gpmsi.StringTable
import fr.gpmsi.CsvDestination
import fr.gpmsi.StringTransformable

import fr.gpmsi.CuMap

def nomCim(cde) {
  if (cde == null || cde.equals("")) return ""
  def libl = cim10.find('CDE', cde.trim(), 'LIBL')
  if (libl == null) return ""
  return libl
}

def nomCcam(cde) {
  if (cde == null || cde.equals("")) return ""
  def libc = ccam48.find('CDE', cde.trim()[0..6], 'LIBC')
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
libelles = false
rums = [] //liste des RUMs des RSS dans l'ordre où ils sont rencontrés

datesSortieHopMaxParNadl = new CuMap({a,n-> n > a}) // on garde nouveau si nouveau sup a ancien
datesEntreeHopMinParNadl = new CuMap({a,n-> n < a}) // on garde nouveau si nouveau inf a ancien

if (flags.contains('libelles')) libelles = true
    
rss {
    name 'Selection des RUMs et maj des dates'
    
    input args['input']
        
    onInit {
    }
    
    onItem {item->
        def rum = item.rum
        def nrss = rum.txtNRSS
        def nadl = rum.txtNADL
        def deum = rum.DEUM.toDate()
        def dsum = rum.DSUM.toDate()
        def listeRss
        if (rumsParNrss.containsKey(nrss)) {
            listeRss = rumsParNrss[nrss]
        }
        else {
            listeRss = []
            rumsParNrss[nrss] = listeRss
        }
        datesEntreeHopMinParNadl.update(nadl, deum) //pour estimation date entree hopital
        datesSortieHopMaxParNadl.update(nadl, dsum) //pour estimation date sortie hopital
        listeRss << rum
        rums << rum
    }//onItem
    
    onEnd {
    }
}

single {


  onInit {
      outputFilePrefix = args['outprefix']
      out_rums = new CsvDestination(new File(outputFilePrefix+"_rums.csv"), "cp1252")
      out_das = new CsvDestination(new File(outputFilePrefix+"_das.csv"), "cp1252")
      out_actes = new CsvDestination(new File(outputFilePrefix+"_actes.csv"), "cp1252")
      out_rums.f 'nadl'
      out_rums.f 'nrss'
      out_rums.f 'nrum'
      out_rums.f 'nadlq'
      out_rums.f 'ghm'
      out_rums.f 'dnais'
      out_rums.f 'sexe'
      out_rums.f 'num'
      //out_rums.f 'tald'
      out_rums.f 'deum'
      out_rums.f 'meum'
      out_rums.f 'prov'
      out_rums.f 'dsum'
      out_rums.f 'msum'
      out_rums.f 'dest'
      out_rums.f 'dehop' //date entree a l'hopital
      out_rums.f 'dshop' //date sortie a l'hopital
      out_rums.f 'dursh' //duree sejour a l'hopital
      //out_rums.f 'cpre'
      //out_rums.f 'pnne'
      //out_rums.f 'ageg'
      //out_rums.f 'ddr'
      out_rums.f 'nbse'
      out_rums.f 'nda'
      out_rums.f 'ndad'
      out_rums.f 'nza'
      out_rums.f 'dp'
      if (libelles) out_rums.f 'libldp'
      out_rums.f 'dr'
      if (libelles) out_rums.f 'libldr'
      out_rums.f 'igs2'
      out_rums.f 'convhc'
      out_rums.f 'pecraac'
      //out_rums.f 'ccrs'
      //out_rums.f 'tyma'
      //out_rums.f 'tydo'
      //out_rums.f 'numi'
      //out_rums.f 'nivg'
      //out_rums.f 'apivg'
      //out_rums.f 'nnva'
      //out_rums.f 'dascs'
      //if (libelles) out_rums.f 'das'
      //out_rums.f 'actscs'
      //if (libelles) out_rums.f 'acts'
      out_rums.f 'parcours'
      out_rums.endRow()
      
      out_das.f 'nrss'
      out_das.f 'nrum'
      out_das.f 'da'
      if (libelles) out_das.f 'libda'
      out_das.endRow()
      
      out_actes.f 'nrss'
      out_actes.f 'nrum'
      out_actes.f 'dr'
      out_actes.f 'ccca'
      out_actes.f 'acti'
      out_actes.f 'xdocu'
      if (libelles) out_actes.f 'libccam'
      out_actes.endRow()
      
      def uh = System.getProperty('user.home')
      //charger cim10 en encodage utf8, avec indexation de la colonne 0
      cim10 = new StringTable(new File(uh + "\\.gpmsi\\cim\\cim10_utf8.csv"), "utf-8", 0)
      ccam48 = new StringTable(new File(uh + "\\.gpmsi\\ccam\\ccam_utf8.csv"), "utf-8", 0)
  }

  onItem { item ->
    rums.each() {rum ->
        def nadl = rum.txtNADL
        out_rums.f "$nadl" //envoyer nadl brut
        def nrss = rum.txtNRSS
        out_rums.f nrss
        def nrum = rum.txtNRUM
        out_rums.f nrum
        out_rums.f "'$nadl" //ajouter une apostrophe pour forcer le mode texte (garde les zéros initiaux)
        def ghm = ""
        if (rum.NCMD != null) ghm = rum.txtNCMD + rum.txtNGHM
        out_rums.f ghm
        def dnais = rum.DNAIS.toDate() //Date de naissance  DNAIS
        out_rums.f isoDateFormat.format(dnais)
        def sexe  = rum.txtSEXE//Sexe   SEXE
        out_rums.f sexe
        def num  = rum.txtNUM//Numéro de l'unité médicale   NUM
        out_rums.f num
        //def tald  = rum.txtTALD//Type d'autorisation du lit dédié TALD
        //out_rums.f tald
        def deum  = rum.DEUM.toDate() //Date d'entrée dans l'unité médicale DEUM
        out_rums.f isoDateFormat.format(deum)
        def meum  = rum.txtMEUM//Mode d'entrée dans l'unité médicale    MEUM
        out_rums.f meum
        def prov  = rum.txtPROV//Provenance (si mode d'entrée est mutation, transfert ou domicile)  PROV
        out_rums.f prov
        def dsum  = rum.DSUM.toDate() //Date de sortie de l'unité médicale  DSUM
        if (dsum == null) out_rums.f '' else out_rums.f isoDateFormat.format(dsum)
        def msum  = rum.txtMSUM //Mode de sortie de l'unité médicale    MSUM
        out_rums.f msum
        def dest  = rum.txtDEST //Destination (si mode de sortie est mutation, transfert ou domicile)   DEST
        out_rums.f dest
        def dehop = datesEntreeHopMinParNadl[nadl]
        def dshop = datesSortieHopMaxParNadl[nadl]
        if (dehop == null) out_rums.f '' else out_rums.f isoDateFormat.format(dehop) //date entree a l'hopital (estimee a partir des dates d'entree des UM)
        if (dshop == null) out_rums.f '' else out_rums.f isoDateFormat.format(dshop) //date de sortie de l'hopital (estimee a partir des dates de sortie des UM)
        if (dehop != null || dshop != null) out_rums.f dshop - dehop
        else out_rums.f ''
        //def cpre  = rum.txtCPRE //Code postal de résidence (ou 99 suivi du code Insee du pays pour les patients résidant hors de France)  CPRE
        //out_rums.f cpre
        //def pnne  = rum.txtPNNE //Poids du nouveau-né à l'entrée de l'unité médicale (en grammes) PNNE
        //out_rums.f pnne
        //def ageg  = rum.txtAGEG //Age gestationnel    AGEG
        //out_rums.f ageg
        //def ddr  = rum.DDR.toDate() //Date des dernières règles   DDR
        //out_rums.f ddr == null ? '' : isoDateFormat.format(ddr)
        def nbse  = rum.txtNBSE //Nombre de séances NBSE
        out_rums.f nbse
        def nda  = rum.txtNDA //Nombre de diagnostics associés (nDA) dans ce RUM    NDA
        out_rums.f nda
        def ndad  = rum.txtNDAD //Nombre de données à visée documentaire (nDAD) dans ce RUM NDAD
        out_rums.f ndad
        def nza  = rum.txtNZA //Nombre de zone d'actes (nZA) dans ce RUM    NZA
        out_rums.f nza
        def dp  = rum.txtDP //Diagnostic principal (DP) DP
        out_rums.f dp
        if (libelles) {
            def libldp = nomCim(dp)
            out_rums.f libldp
        }
        def dr  = rum.txtDR //Diagnostic relié (DR) DR
        out_rums.f dr
        if (libelles) {
            def libldr = nomCim(dr)
            out_rums.f libldr
        }
        def igs2 = rum.txtIGS2 //IGS 2  IGS2
        out_rums.f igs2
        def convhc = rum.txtCONVHC
        out_rums.f convhc
        def pecraac = rum.txtPECRAAC
        out_rums.f pecraac
        //def ccrs = rum.txtCCRS //Confirmation du codage du RSS    CCRS
        //out_rums.f ccrs
        //def tyma = rum.txtTYMA //Type de machine en radiothérapie TYMA
        //out_rums.f tyma
        //def tydo = rum.txtTYDO //Type de dosimétrie   TYDO
        //out_rums.f tydo
        //def numi = rum.txtNUMI //Numéro d'innovation  NUMI
        //out_rums.f numi
        //def nivg = rum.txtNIVG //Nombre d’IVG antérieures NIVG
        //out_rums.f nivg
        //def apivg = rum.txtAPIVG //Année de l’IVG précédente  APIVG
        //out_rums.f apivg
        //def nnva = rum.txtNNVA //Nombres de naissances vivantes antérieures   NNVA
        //out_rums.f nnva
        rum.DA.txtTDA.each {da->
            out_das.f nrss
            out_das.f nrum
            out_das.f da
            if (libelles) out_das.f nomCim(da)
            out_das.endRow()
        }
        rum.ZA.each {acte->
            out_actes.f nrss
            out_actes.f nrum
            out_actes.f isoDateFormat.format(acte.DR.toDate())
            out_actes.f acte.txtCCCA.trim()
            out_actes.f acte.txtACTI
            out_actes.f acte.txtXDOCU
            if (libelles) out_actes.f nomCcam(acte.txtCCCA.trim())
            out_actes.endRow()
        }
        //ajout parcours de soins
        def parcours = []
        rumsParNrss[nrss].each {r->
            if (r.txtMEUM == '8' && r.txtPROV == '5') parcours << 'URG'
            parcours << r.txtNUM
        }
        out_rums.f "'" + parcours*.trim().join('-')
        out_rums.endRow()
    }// rums.each

  }
  onEnd {
    out_rums.close()
    out_das.close()
    out_actes.close()
  }
}
