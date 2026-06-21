package fr.gpmsi.da.rss

import fr.gpmsi.Chrono
import fr.gpmsi.da.CInteger
import fr.gpmsi.da.CNumeric
import fr.gpmsi.da.CVarchar
import fr.gpmsi.da.Dao
import groovy.sql.Sql

/*
*/

class Rss 
  extends Dao 
{
    static Rss instance = new Rss()

    Rss() {
      super("RSS")
      
      pkcol(new CVarchar('NRSS', 20)) //  String nrss //BIGINT, N° de RSS (Equivalent de HOSP-PMSI)
      
      colVarchar('GVC', 2) //String  gvc //VARCHAR(2), Groupage : version de la classification
      colVarchar('NCMD', 2) //String  ncmd //VARCHAR(2), Groupage : n° de GHM N° CMD
      colVarchar('NGHM', 4) //String  nghm //VARCHAR(4), N° GHM
      colVarchar('VRSS', 3) //String  vrss //VARCHAR(3), N° de version du format de RSS
      colVarchar('GCR', 3) //String  gcr //VARCHAR(3), Groupage : code retour
      colVarchar('FINESS', 9) //String  finess //VARCHAR(9), Numero FINESS d’inscription ePMSI
      colVarchar('VRUM', 3) //String  vrum //VARCHAR(3), Version du format du RUM
      colVarchar('NADL', 20) //String  nadl //VARCHAR(20), N° Administratif local de sejour
      //colVarchar('NRUM', 10) //String nrum //VARCHAR(10), N° de RUM
      colDate('DNAIS') //java.sql.Date dnais //DATE, Date de naissance
      colVarchar('SEXE', 1) //String sexe //VARCHAR(1), Sexe
      colVarchar('CPRE', 5)  //VARCHAR(5), Code postal de residence (ou 99 suivi du code Insee du pays pour les patients residant hors de France)
      colInteger('PNNE')   //INTEGER, Poids du nouveau-ne à l entree de l unite medicale (en grammes)
      colInteger('AGEG')   //INTEGER, Age gestationnel
      colDate('DDR')   // DATE, Date des dernieres regles (seulement à partir de v. 116)
      colInteger('NBSE')    //INTEGER, Nombre de seances
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
      //les colonnes suivantes sont calculées 
      colVarchar('PREM_RUM', 10)
      colVarchar('DERN_RUM', 10)
      colInteger('NB_RUMS')
      colDate('DEH')
      colDate('DSH')
      colInteger('DURSEJ')
      colVarchar('MEH', 1)
      colVarchar('PROV', 1)
      colVarchar('MSH', 1)
      colVarchar('DEST', 1)
      colVarchar('PSUR', 1)
    }    
    
    void createIndexes(Sql gsql) {
      gsql.execute("create index if not exists RSS_PREM_RUM on RSS(PREM_RUM)")
    }
  
    /**
     * Remplir la table à partir de la table des RUMs
     */
    void fillFromRum(Sql gsql) {
      gsql.execute("DELETE FROM RSS")
      /*
  INSERT INTO RSS(NRSS, NADL, GVC, NCMD, NGHM, VRSS, GCR, FINESS, VRUM, DNAIS, SEXE, CPRE, PNNE, AGEG, DDR, NBSE, 
      CCRS, TYMA, TYDO, NUMI, CONVHC, PECRAAC, CTXPSP, ADMPRH, RESCRT, CATNIT, NP, PREM_RUM, DERN_RUM,NB_RUMS,DEH,
      DSH,DURSEJ,MEH, PROV, MSH,DEST,PSUR)
 select NRSS, NADL, GVC, NCMD, NGHM, VRSS, GCR, FINESS, VRUM, DNAIS, SEXE, CPRE, 
(select PNNE from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) PNNE, 
(select AGEG from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) AGEG,  
(select DDR from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) DDR,
(select NBSE from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) NBSE, 
(select CCRS from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) CCRS, 
(select TYMA from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) TYMA,
(select TYDO from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) TYDO,
(select NUMI from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) NUMI,         
(select CONVHC from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) CONVHC,
(select PECRAAC from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) PECRAAC,
(select CTXPSP from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) CTXPSP, 
(select ADMPRH from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) ADMPRH,
(select RESCRT from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) RESCRT,
(select CATNIT from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) CATNIT, 
(select NP from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) NP,
(select NRUM from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) PREM_RUM, 
(select NRUM from RUM R2 where R1.NRSS = R2.NRSS order by deum desc, nrum desc fetch first row only) DERN_RUM,
count(distinct NRUM) NB_RUMS, 
(select DEUM from rum R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) DEH, 
(select DSUM from rum R2 where R1.NRSS = R2.NRSS order by DEUM desc, NRUM desc fetch first row only) DSH,
sum(DATEDIFF(DAY,DEUM,DSUM)) DURSEJ,
(select MEUM from rum R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) MEH, 
(select PROV from rum r2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) PROV,
(select MSUM from rum r2 where R1.NRSS = R2.NRSS order by DEUM desc, NRUM desc fetch first row only) MSH,
(select DEST from rum r2 where R1.NRSS = R2.NRSS order by DEUM desc, NRUM desc fetch first row only) DEST,
(select PSUR from rum r2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) PSUR 
from RUM r1
group by NRSS
       */
      Chrono ch = new Chrono()
      gsql.execute("""INSERT INTO RSS(NRSS, PREM_RUM, DERN_RUM, DEH, DSH, DURSEJ, NB_RUMS, MEH, PROV, MSH, DEST, PSUR)
 select NRSS,
        (select NRUM from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) PREM_RUM, 
        (select NRUM from RUM R2 where R1.NRSS = R2.NRSS order by DEUM desc, NRUM desc fetch first row only) DERN_RUM,
        (select DEUM from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) DEH, 
        (select DSUM from RUM R2 where R1.NRSS = R2.NRSS order by DEUM desc, NRUM desc fetch first row only) DSH,
        sum(DATEDIFF(DAY, DEUM, DSUM)) DURSEJ,
        count(distinct NRUM) NB_RUMS, 
        (select MEUM from RUM R2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) MEH, 
        (select PROV from RUM r2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) PROV,
        (select MSUM from RUM r2 where R1.NRSS = R2.NRSS order by DEUM desc, NRUM desc fetch first row only) MSH,
        (select DEST from RUM r2 where R1.NRSS = R2.NRSS order by DEUM desc, NRUM desc fetch first row only) DEST,
        (select PSUR from RUM r2 where R1.NRSS = R2.NRSS order by DEUM, NRUM fetch first row only) PSUR 
from RUM R1
group by NRSS
""")
      long elapsed = ch.elapsed()
      println "Temps de construction de RSS : $elapsed"
      
      ch.mark()
      /* Ce code est beaucoup trop lent ; apparemment en H2 avant la v. 2.2.224 il y aurait des problèmes de performance avec "merge". 
      //mettre à jour les enregistrements grâce à "merge"
      gsql.execute("""
merge into RSS(PREM_RUM,        GVC, NCMD, NGHM, VRSS, GCR, FINESS, VRUM, NADL, DNAIS, SEXE,
CPRE, PNNE, AGEG, DDR, NBSE, CCRS, TYMA, TYDO, NUMI, CONVHC, PECRAAC, CTXPSP, ADMPRH,
RESCRT, CATNIT, NP)
key(PREM_RUM)
select    RUM.NRUM PREM_RUM,        RUM.GVC, RUM.NCMD, RUM.NGHM, RUM.VRSS, RUM.GCR, RUM.FINESS, RUM.VRUM, RUM.NADL, RUM.DNAIS, RUM.SEXE,
RUM.CPRE, RUM.PNNE, RUM.AGEG, RUM.DDR, RUM.NBSE, RUM.CCRS, RUM.TYMA, RUM.TYDO, RUM.NUMI, RUM.CONVHC, RUM.PECRAAC, RUM.CTXPSP, RUM.ADMPRH,
RUM.RESCRT, RUM.CATNIT, RUM.NP
from RUM
join RSS on RUM.NRUM = RSS.PREM_RUM 
""")
      */
      //mettre à jour les enregistrements grâce à une requête update corrélée
      //il peut arriver que certains RUMs soient envoyés en double (bug DPI), et cela cause une erreur
      //de type "Error: La sous-requête scalaire contient plus d'une rangée
      //  Scalar subquery contains more than one row;"
      //Pour éviter ce problème on ajoute la clause "
      gsql.execute("""
update RSS
set   (    PREM_RUM,        GVC, NCMD, NGHM, VRSS, GCR, FINESS, VRUM, NADL, DNAIS, SEXE,
           CPRE, PNNE, AGEG, DDR, NBSE, CCRS, TYMA, TYDO, NUMI, CONVHC, PECRAAC, CTXPSP, ADMPRH, RESCRT, CATNIT, NP) = 
  (select NRUM PREM_RUM,    GVC, NCMD, NGHM, VRSS, GCR, FINESS, VRUM, NADL, DNAIS, SEXE,
           CPRE, PNNE, AGEG, DDR, NBSE, CCRS, TYMA, TYDO, NUMI, CONVHC, PECRAAC, CTXPSP, ADMPRH, RESCRT, CATNIT, NP
  from RUM 
  where RSS.PREM_RUM = RUM.NRUM
  order by DEUM, NRUM fetch first row only
  )
""")
      elapsed = ch.elapsed()
      println "Temps de complement de RSS : $elapsed"
      
  }
    
}
  
