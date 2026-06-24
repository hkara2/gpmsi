package fr.gpmsi.da.rss

import fr.gpmsi.StringTable
import fr.gpmsi.da.CInteger
import fr.gpmsi.da.CVarchar
import fr.gpmsi.da.Dao
import fr.gpmsi.pmsixml.FszNode
import groovy.sql.Sql

/**
 * Nadl associé à un Nadl randomisé (utilisé pour cacher le nadl réel), pour un numéro d'injection donné
 * @author hkaradimas
 *
 */
public class Nadlr
extends Dao
{
  static Da instance = new Da()
  
  static void insertNewNadlsFromRum(Sql gsql) {
    gsql.execute("insert into NADLR(NADL) select distinct NADL from RUM where NADL not in (select NADL from NADLR)")
  }

  /**
   * Insérer de nouveaux couples NADL,NADLR à partir de la StringTable passée en argument
   * @param gsql la connexion
   * @param nadlrSt une stringtable qui contient une colonne NADL et une colonne NADLR
   */
  static void insertNadlrTable(Sql gsql, StringTable nadlrSt, long injrssId) {
    nadlrSt.each {row-> 
      gsql.execute("insert into NADLR(NADL, NADLR) values (?,?)", [row.NADL, row.NADLR], injrssId)
    }
  }

  /**
   * Trouver un nouveau NADLR qui n'existe pas encore dans la table   
   * @param gsql La connexion
   * @return un nouveau NADLR qui n'existe pas encore dans la table
   */
  static String newNadlr(Sql gsql, long injrssId) {
    while (true) {
      BigDecimal v = BigDecimal.valueOf(10000000000)
      v = v.multiply(BigDecimal.valueOf(Math.random()))
      v = v.toBigInteger()
      v = v.add(new BigDecimal('20000000000'))
      String candidate = v.toString()
      int n = 0
      gsql.eachRow("select count(*) RC from NADLR where NADLR=? and INJRSS_ID=?", [candidate, injrssId]) { row ->
        n = row.RC
      }
      if (n == 0) return candidate
    }
    
  }
  
  /**
   * Pour chaque NADLR qui est à null, en insérer un nouveau
   * @param gsql
   */
  static void insertNewNadlrs(Sql gsql, long injrssId) {
    Nadlr nadlr = new Nadlr()
    while (true) {
      def row = gsql.firstRow("select NADL from NADLR where NADLR is null and injrssId = ? fetch first row only", [injrssId]) 
      String nadl = row ? row.NADL : null
      if (nadl == null) break //c'est bon, il n'y a plus de NADLR null, on peut sortir de la boucle
      else {
        def vals = nadlr.makeEmptyValueList()
        nadlr.setValue(vals, 'NADL', nadl)
        nadlr.setValue(vals, 'INJRSS_ID', injrssId)
        String newNadlr = Nadlr.newNadlr(gsql, injrssId)
        nadlr.setValue(vals, 'NADLR', newNadlr)
        nadlr.updateToDb(gsql, vals)
      }
    }
  }
  
  public Nadlr() {
    super("NADLR")
    pkcol(new CVarchar('NADL', 20))   //BIGINT PRIMARY KEY, cle numero sequentiel unique
    
    colInteger('INJRSS_ID').extraDdl('REFERENCES INJRSS')
    colVarchar('NADLR', 20)       //VARCHAR(20), NADL "randomise", sur 11 digits, commence par un 2
  }
  
  void createIndexes(Sql gsql) {
    gsql.execute("create index if not exists NADLR_INJRSS_ID_NADL on NADLR(INJRSS8ID, NADL)")
    gsql.execute("create index if not exists NADLR_INJRSS_ID_NADLR on NADLR(INJRSS8ID, NADLR)")
  }



}    