package fr.gpmsi.da.rss;

import fr.gpmsi.da.CInteger;
import fr.gpmsi.da.Dao;
import fr.gpmsi.pmsixml.FszGroup
import fr.gpmsi.pmsixml.FszNode
import groovy.sql.Sql

/**
 * Data access pour une table "rum_gp". Est amené à changer à chaque fois que le format des RUMs est mis à jour par l'ATIH.
 * Les colonnes sont déclarées avec les même noms que dans la resource "fr\gpmsi\pmsixml\rss123.csv"
 * @author hkaradimas
 *
 */
public class Rum
extends Dao
{
  static Rum instance = new Rum()

  public Rum() {
    super("RUM");
    pkcol(new CInteger('RUM_ID', true)) //Integer rumg_id //BIGINT PRIMARY KEY, cle numero sequentiel unique
    //,   , , , , , , , convhc, pecraac, ctxpsp, admprh, rescrt, catnit, np, psur,
    // -- Integer rsa_inj_id //BIGINT, numero de l injection de RSA
    colVarchar('GVC', 2) //String  gvc //VARCHAR(2), Groupage : version de la classification
    colVarchar('NCMD', 2) //String  ncmd //VARCHAR(2), Groupage : n° de GHM N° CMD
    colVarchar('NGHM', 4) //String  nghm //VARCHAR(4), N° GHM
    colVarchar('VRSS', 3) //String  vrss //VARCHAR(3), N° de version du format de RSS
    colVarchar('GCR', 3) //String  gcr //VARCHAR(3), Groupage : code retour
    colVarchar('FINESS', 9) //String  finess //VARCHAR(9), Numero FINESS d’inscription ePMSI
    colVarchar('VRUM', 3) //String  vrum //VARCHAR(3), Version du format du RUM
    colNumeric('NRSS', 20, 0) //  BigDecimal nrss //BIGINT, N° de RSS (Equivalent de HOSP-PMSI)
    colVarchar('NADL', 20) //String  nadl //VARCHAR(20), N° Administratif local de sejour
    colInteger('NRUM') //Integer nrum //INTEGER, N° de RUM
    colDate('DNAIS') //java.sql.Date dnais //DATE, Date de naissance
    colInteger('SEXE') //Integer sexe //INTEGER, Sexe
    colVarchar('NUM', 4) //String  num //VARCHAR(4), Numero de l unite medicale
    colVarchar('TALD', 2) //String  tald //VARCHAR(2), Type d autorisation du lit dedie
    colDate('DEUM') //java.sql.Date deum //DATE, Date d entree dans l unite medicale
    colVarchar('MEUM', 1) //String meum //CHAR(1) (anciennement INTEGER), Mode d entree dans l unite medicale
    colVarchar('PROV', 1) //String prov //CHAR(1), Provenance (si mode d entree est mutation, transfert ou domicile)
    colDate('DSUM') //java.sql.Date dsum //DATE, Date de sortie de l unite medicale
    colInteger('MSUM')  //INTEGER, Mode de sortie de l unite medicale
    colInteger('DEST')  //INTEGER, Destination (si mode de sortie est mutation, transfert ou domicile)
    colVarchar('CPRE', 5)  //VARCHAR(5), Code postal de residence (ou 99 suivi du code Insee du pays pour les patients residant hors de France)
    colInteger('PNNE')   //INTEGER, Poids du nouveau-ne à l entree de l unite medicale (en grammes)
    colInteger('AGEG')   //INTEGER, Age gestationnel
    colDate('DDR')   // DATE, Date des dernieres regles (seulement à partir de v. 116)
    colInteger('NBSE')    //INTEGER, Nombre de seances
    colInteger('NDA')     //INTEGER, Nombre de diagnostics associes (nDA) dans ce RUM
    colInteger('NDAD')    //INTEGER, Nombre de donnees à visee documentaire (nDAD) dans ce RUM
    colInteger('NZA')     //INTEGER, Nombre de zone d actes (nZA) dans ce RUM
    colVarchar('DP', 8)   // VARCHAR(8), Diagnostic principal (DP)
    colVarchar('DR', 8)   // VARCHAR(8), Diagnostic relie (DR)
    colInteger('IGS2')    //INTEGER, IGS 2
    colVarchar('CCRS', 1)  //VARCHAR(1), Confirmation du codage du RSS
    colVarchar('TYMA', 1)  // VARCHAR(1), Type de machine en radiotherapie
    colVarchar('TYDO', 1)  // VARCHAR(1), Type de dosimetrie
    colVarchar('NUMI', 15) // VARCHAR(15), Numero d innovation (seulement à partir de v. 116)
    colVarchar('CONVHC', 1)  //alpha
    colVarchar('PECRAAC', 1) //alpha
    colVarchar('CTXPSP', 1)  //alpha
    colVarchar('ADMPRH', 1)  //alpha
    colVarchar('RESCRT', 1)  //alpha
    colVarchar('CATNIT', 1)  //alpha
    colVarchar('NP', 1)      //alpha
    colVarchar('PSUR', 1)    //alpha
  }
 
  /**
   * Insère le noeud RUM en tant qu'enregistrement dans la table
   * @param gsql La connexion
   * @param rumNode le noeud FszNode
   * @return Le tableau de valeurs (permet d'accéder au RUM_ID qui a été généré
   */
  def insertRum(Sql gsql, FszNode rumNode) {
    FszGroup rumGroup = rumNode
    def values = makeValues(rumGroup)
    insertInDb(gsql, values)
    Integer rum_id = getValue(values, 'RUM_ID') //récupérer la clé primaire qui vient d'être créée
    //println "rum_id:$rum_id"
    //prendre le conteneur des DA
    FszGroup daContainer = rumGroup.getChildGroup('DA')
    //insérer tous les DA enfants
    daContainer.children.each {da ->
      Da.instance.insertDa(gsql, da, rum_id)
    }//daContainer.children.each
    //prendre le conteneur des ZA
    FszGroup zaContainer = rumGroup.getChildGroup('ZA')
    //insérer tous les ZA enfants
    zaContainer.children.each {za->
      Za.instance.insertZa(gsql, za, rum_id)
    }//ru.DA.each
    return values
  }
 
  /**
   * Mettre à jour le rum_gp qui a la clé rum_id donnée, à partir du noeud donné
   * @param gsql La connexion
   * @param rumNode Le noeud de type FszGroup
   * @param pk La clé primaire
   * @return le nombre de rangées mises à jour
   */
  def updateRum(Sql gsql, FszNode rumNode, int pk) {
    def values = makeValues(rumNode)
    setValue(values, 'RUM_ID', pk) //ajouter la clé primaire
    updateToDb(gsql, values)
  } 

  void createIndexes(Sql gsql) {
    gsql.execute("create index if not exists RUM_NRSS on RUM(NRSS)")
    gsql.execute("create index if not exists RUM_NRUM on RUM(NRUM)")
  }

}
