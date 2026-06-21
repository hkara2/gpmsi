package fr.gpmsi.da.rss

import fr.gpmsi.da.CInteger
import fr.gpmsi.da.Dao
import fr.gpmsi.pmsixml.FszNode
import groovy.sql.Sql

public class Za
extends Dao
{
  static Za instance = new Za()

  public Za() {
    super("ZA")
    pkcol(new CInteger('ZA_ID', true))   //BIGINT PRIMARY KEY, cle numero sequentiel unique
    
    colInteger('RUM_ID')       //BIGINT, lien vers RUM_GP parent

    colDate('DR')              //DATE, Date de réalisation
    colVarchar('CCCA', 10)     //VARCHAR(10),  Code CCAM
    colVarchar('PHAS', 1)      //CHAR(1),  Phase
    colVarchar('ACTI', 1)      //CHAR(1),  Activité
    colVarchar('XDOCU', 1)     //CHAR(1),  Extension documentaire
    colVarchar('MODIF', 4)     //VARCHAR(4),  Modificateurs
    colVarchar('REXC', 1)      //CHAR(1),  Remboursement exceptionnel
    colVarchar('ASSNP', 1)     //CHAR(1),  Association non prévue
    colInteger('NREAL')        //INTEGER  Nombre de réalisations de l acte n° nZA pendant le séjour
  }
  
  /**
   * Insérer un nouveau ZA à partir du noeud donné
   * @param gsql la connexion
   * @param zaNode le noeud pour le ZA
   * @param rumId l'id du rum parent
   * @return le tableau de valeurs (permet d'accéder au numéro de ZA_ID qui vient d'être créé)
   */
  def insertZa(Sql gsql, FszNode zaNode, int rum_id) {
    def values = makeValues(zaNode)
    setValue(values, 'RUM_ID', rum_id) //mettre aussi l'ID du RUM parent
    insertInDb(gsql, values)
    return values
  }
 
  /**
   * Mettre à jour le za qui a la clé za_id donnée, à partir du noeud donné
   * @param gsql La connexion
   * @param rumNode Le noeud de type FszGroup
   * @param pk La valeur de clé primaire pour za_id
   * @return le nombre de rangées mises à jour
   */
  def updateZa(Sql gsql, FszNode zaNode, int pk) {
    def values = makeValues(zaNode)
    setValue(values, 'ZA_ID', pk) //ajouter la clé primaire
    updateToDb(gsql, values)
  }
  
  void createIndexes(Sql gsql) {
    gsql.execute("create index if not exists ZA_RUM_ID on ZA(RUM_ID)")
  }

}    