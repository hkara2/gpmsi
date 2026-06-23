package fr.gpmsi.da.rss

import fr.gpmsi.Chrono
import groovy.sql.Sql

class RssTables {
  
  static boolean printDebugMessages = false //à réactiver manuellement si on a un doute sur la bonne création des tables
  
  /**
   * Créer les tables nécessaires pour gérer un RUM et ses sous-tables
   * @param gsql
   */
  static void createTables(Sql gsql) {
    Injrss injrss = new Injrss()
    def injrssDdl = injrss.makeTableDdl("", "H2")
    if (printDebugMessages) println "injrssDdl: $injrssDdl"
    gsql.execute(injrssDdl)

    Rum rum = new Rum()
    def rumDdl = rum.makeTableDdl("", "H2")
    if (printDebugMessages) println "rumDdl: $rumDdl"
    gsql.execute(rumDdl)
    //rum.createIndexes(gsql) //indexs à créer après les insertions
    
    Rss rss = new Rss()
    def rssDdl = rss.makeTableDdl("", "H2")
    if (printDebugMessages) println "rssDdl: $rssDdl"
    gsql.execute(rssDdl)
    rss.createIndexes(gsql)
    
    Da da = new Da()
    def daDdl = da.makeTableDdl("", "H2")
    if (printDebugMessages) println "daDdl: $daDdl"
    gsql.execute(daDdl)
    da.createIndexes(gsql)
    
    Za za = new Za()
    def zaDdl = za.makeTableDdl("", "H2")
    if (printDebugMessages) println "zaDdl: $zaDdl"
    gsql.execute(zaDdl)
    za.createIndexes(gsql)
    
    Nadlr nadlr = new Nadlr()
    def nadlrDdl = nadlr.makeTableDdl("", "H2")
    if (printDebugMessages) println "nadlrDdl: $nadlrDdl"
    gsql.execute(nadlrDdl)
    nadlr.createIndexes(gsql)
  }
  
}
