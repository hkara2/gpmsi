package fr.gpmsi.da.rss

import fr.gpmsi.da.CInteger
import fr.gpmsi.da.Dao
import fr.gpmsi.pmsixml.FszNode
import groovy.sql.Sql

/**
 * Diagnostic associé (significatif)
 * @author hkaradimas
 *
 */
public class Da
extends Dao
{
  static Da instance = new Da()
  
  /**
   * Constructeur normal
   */
  public Da() {
    super("DA")
    pkcol(new CInteger('DA_ID', true))   //BIGINT PRIMARY KEY, cle numero sequentiel unique
    
    colInteger('RUM_ID').setExtraDdl('REFERENCES RUM')       //BIGINT, lien vers RUM parent
    colVarchar('TDA', 8)       //VARCHAR(8),  texte du code de diagnostic associé
  }
  
  /**
   * Insérer un nouveau DA à partir du noeud donné
   * @param gsql la connexion
   * @param DaNode le noeud pour le Da
   * @param rumId l'id du rum parent
   * @return le tableau de valeurs (permet d'accéder au numéro de DA_ID qui vient d'être créé)
   */
  def insertDa(Sql gsql, FszNode DaNode, int rum_id) {
    def values = makeValues(DaNode)
    setValue(values, 'RUM_ID', rum_id) //mettre aussi l'ID du RUM parent
    insertInDb(gsql, values)
    return values
  }
 
  /**
   * Mettre à jour le Da qui a la clé Da_id donnée, à partir du noeud donné
   * @param gsql La connexion
   * @param rumNode Le noeud de type FszGroup
   * @param pk La valeur de clé primaire pour DA_ID
   * @return le nombre de rangées mises à jour
   */
  def updateDa(Sql gsql, FszNode DaNode, int pk) {
    def values = makeValues(DaNode)
    setValue(values, 'DA_ID', pk) //ajouter la clé primaire
    updateToDb(gsql, values)
  }

  /**
   * Créer les index
   * @param gsql La connection à utiliser
   */
  void createIndexes(Sql gsql) {
    gsql.execute("create index if not exists DA_RUM_ID on DA(RUM_ID)")
  }



}    