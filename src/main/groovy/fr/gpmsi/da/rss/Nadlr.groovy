package fr.gpmsi.da.rss

import fr.gpmsi.StringTable
import fr.gpmsi.da.CInteger
import fr.gpmsi.da.CVarchar
import fr.gpmsi.da.Dao
import fr.gpmsi.pmsixml.FszNode
import groovy.sql.Sql

/**
 * Nadl associé à un Nadl randomisé (utilisé pour cacher le nadl réel)
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
  static void insertNadlrTable(Sql gsql, StringTable nadlrSt) {
    nadlrSt.each {row-> 
      gsql.execute("insert into NADLR(NADL, NADLR) values (?,?)", [row.NADL, row.NADLR])
    }
  }

  /**
   * Trouver un nouveau NADLR qui n'existe pas encore dans la table   
   * @param gsql La connexion
   * @return un nouveau NADLR qui n'existe pas encore dans la table
   */
  static String newNadlr(Sql gsql) {
    while (true) {
      BigDecimal v = BigDecimal.valueOf(10000000000)
      v = v.multiply(BigDecimal.valueOf(Math.random()))
      v = v.toBigInteger()
      v = v.add(new BigDecimal('20000000000'))
      String candidate = v.toString()
      int n = 0
      gsql.eachRow("select count(*) RC from NADLR where NADLR=?", [candidate]) { row ->
        n = row.RC
      }
      if (n == 0) return candidate
    }
    
  }
  
  /**
   * Pour chaque NADLR qui est à null, en insérer un nouveau
   * @param gsql
   */
  static void insertNewNadlrs(Sql gsql) {
    Nadlr nadlr = new Nadlr()
    while (true) {
      def row = gsql.firstRow("select NADL from NADLR where NADLR is null fetch first row only") 
      String nadl = row ? row.NADL : null
      if (nadl == null) break //c'est bon, il n'y a plus de NADLR null, on peut sortir de la boucle
      else {
        def vals = nadlr.makeEmptyValueList()
        nadlr.setValue(vals, 'NADL', nadl)
        String newNadlr = Nadlr.newNadlr(gsql)
        nadlr.setValue(vals, 'NADLR', newNadlr)
        nadlr.updateToDb(gsql, vals)
      }
    }
  }
  
  public Nadlr() {
    super("NADLR")
    pkcol(new CVarchar('NADL', 20))   //BIGINT PRIMARY KEY, cle numero sequentiel unique
    
    colVarchar('NADLR', 20)       //VARCHAR(20), NADL "randomise", sur 11 digits, commence par un 2
  }
  
  void createIndexes(Sql gsql) {
    gsql.execute("create index if not exists NADLR_NADL on NADLR(NADL)")
    gsql.execute("create index if not exists NADLR_NADLR on NADLR(NADLR)")
  }



}    