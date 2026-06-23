package fr.gpmsi.da.rss

import fr.gpmsi.da.CInteger
import fr.gpmsi.da.Dao
import fr.gpmsi.pmsixml.FszNode
import groovy.sql.Sql

/**
 * Injection de RSS
 * @author hkaradimas
 *
 */
public class Injrss
extends Dao
{
  static Injrss instance = new Injrss()
  
  public Injrss() {
    super("INJRSS")
    pkcol(new CInteger('INJRSS_ID', true))   //BIGINT PRIMARY KEY, cle numero sequentiel unique
    
    colTimestamp('DT_INJ')     //TIMESTAMP, date/heure de l'injection
    colVarchar('DESCR', 128)   //VARCHAR(128) description de cette injection
    colVarchar('TYPINJ', 16)   //VARCHAR(16), type d'injection (ATIH, TEMP, ...)
    colDate('DEUM_MIN')        //DATE, date minimum d'entrée dans l'UM
    colDate('DSUM_MIN')        //DATE, date minimum de sortie de l'UM 
    colDate('DEUM_MAX')        //DATE, date minimum d'entrée dans l'UM
    colDate('DSUM_MAX')        //DATE, date minimum de sortie de l'UM
    colInteger('NB_RUM')       //BIGINT, nombre de RUMs
    colInteger('NB_RSS')       //BIGINT, nombre de RSSs
    colInteger('NB_DA')       //BIGINT, nombre de RSSs
    colInteger('NB_ZA')       //BIGINT, nombre de RSSs
  }

  void createIndexes(Sql gsql) {
    //rien à créer ici pour l'instant
  }

  List insertInjrss(Sql gsql, String description) {
    def values = makeEmptyValueList()
    setValue(values, 'DESCR', description)
    setValue(values, 'DT_INJ', new Date())
    insertInDb(gsql, values)
    return values
  }


}    