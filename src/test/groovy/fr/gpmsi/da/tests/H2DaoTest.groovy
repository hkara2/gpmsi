package fr.gpmsi.da.tests;

import static org.junit.Assert.*;

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Time
import java.util.Calendar

import org.h2.tools.Server
import org.junit.After
import org.junit.Before
import org.junit.Test;

import fr.gpmsi.da.CInteger
import fr.gpmsi.da.Dao
import fr.gpmsi.da.rss.Injrss
import fr.gpmsi.da.rss.Nadlr
import fr.gpmsi.da.rss.Rss
import fr.gpmsi.da.rss.Rum
import fr.gpmsi.da.rss.RssTables
import fr.gpmsi.pmsixml.FszNode
import fr.gpmsi.pmsixml.RssReader
import groovy.sql.Sql
import junit.framework.TestCase

public class H2DaoTest {
  Sql gsql

  @Before
  public void setUp()
    throws Exception 
  {
      String[] args = {};
      String h2loc = "mem" //mettre "mem" pour base en mémoire, ou "file" pour fichier dans C:\t\h2daotest
      if (h2loc == "mem") {
        gsql = Sql.newInstance('jdbc:h2:mem:', 'sa', '', 'org.h2.Driver')
      }
      else {
        //fichier sur disque, permet de regarder la base avec un outil (par ex. SquirrelSQL).
        //supprimer les fichiers à la main (C:/t/h2daotest.mv.db et C:/t/h2daotest.trace.db) avant de lancer les tests ! 
        gsql = Sql.newInstance('jdbc:h2:file:C:/t/h2daotest', 'sa', '', 'org.h2.Driver')
      }
  }

  @After
  public void tearDown()
    throws Exception 
  {
    gsql.close()
  }

  private void createTables() {
    RssTables.createTables(gsql)
    //println "[createTables]Tables créées"
  }

  @Test
  public void testCreateRumGp() {
    RssTables.createTables(gsql)
    //println "[testCreateRumGp]Tables créées"
  }

  @Test
  void testImportRss1() {
    //Dao.emitInsertDebugPrints = true
    boolean debugPrints = false //mettre à true pour envoyer des messages de débogage
    createTables()
    def vals = Injrss.instance.insertInjrss(gsql, 'For tests')
    def injrssId = Injrss.instance.getValue(vals, Injrss.instance.getColumn('INJRSS_ID'))
    ResultSet rs = gsql.eachRow("select * from RUM") {row->
      if (debugPrints) println "$row"
    }
    if (debugPrints) println "Remplissage RUM..."
    Rum rda = new Rum()
    RssReader rdr = new RssReader();
    File tf = new File("test-files/in/t02rss023.txt")
    tf.eachLine {line->
      if (line.length() == 0) return
      FszNode nd = rdr.readOne(line)
      rda.insertRum(gsql, nd, injrssId)
    }
    rs = gsql.eachRow("select * from RUM") {row->
      if (debugPrints) println "row: $row"      
    }
    rs = gsql.eachRow("select * from DA") {row->
      if (debugPrints) println "row: $row"      
    }
    rs = gsql.eachRow("select * from ZA") {row->
      if (debugPrints) println "row: $row"      
    }
    //créer une table RSS qui permet de raisonner au niveau du RSS
    Rss.instance.fillFromRum(gsql)
    
    /*
     * select nrss, nadl, gvc, ncmd, nghm, vrss, gcr, finess, vrum, dnais, sexe, cpre, pnne, ageg, ddr, nbse, ccrs, tyma, tydo, numi, convhc, pecraac, ctxpsp, admprh, rescrt, catnit, np, psur, 
(select nrum from rum r2 where r1.nrss = r2.nrss order by deum, nrum fetch first row only) PREM_RUM, 
(select nrum from rum r2 where r1.nrss = r2.nrss order by deum desc, nrum desc fetch first row only) DERN_RUM,
count(distinct nrum) NB_RUMS, sum(DATEDIFF(DAY,DEUM,DSUM)) DURSEJ,
(select MEUM from rum r2 where r1.nrss = r2.nrss order by deum, nrum fetch first row only) MEH, 
(select PROV from rum r2 where r1.nrss = r2.nrss order by deum, nrum fetch first row only) PROV,
(select MSUM from rum r2 where r1.nrss = r2.nrss order by deum desc, nrum desc fetch first row only) MSH,
(select DEST from rum r2 where r1.nrss = r2.nrss order by deum desc, nrum desc fetch first row only) DEST
from rum r1
group by nrss
     */
    //Dao.emitInsertDebugPrints = false
  }
  
  @Test
  void testUpdateTstaDaViaDao() {
    //creation de la table
    TstaDa tda = new TstaDa()
    def tdaDdl = tda.makeTableDdl("", "H2")
    //println "tdaDdl: $tdaDdl"
    int r = gsql.executeUpdate(tdaDdl)
    //creation d'une rangée
    def vals = tda.makeEmptyValueList()
    tda.setValue(vals, 'ID', null)
    tda.setValue(vals, 'FOO', "Toto")
    tda.setValue(vals, 'AMOUNT', 10)
    tda.insertInDb(gsql, vals)
    def generatedKey = tda.getValue(vals, 'ID')
    //println "Insertion tsta faite, clé générée : $generatedKey"
    //incrementer amount et mettre a jour dans la table
    tda.setValue(vals, 'AMOUNT', tda.getValue(vals, 'AMOUNT') + 1)
    tda.updateToDb(gsql, vals)
    //maintenant on relit l'enregistrement mais dans la base directement, via select
    int nrows = 0
    int amount = 0
    gsql.eachRow("select * from TSTA where FOO='Toto'") {row->
      amount = row.'AMOUNT'
      nrows++
    }
    assertEquals(1, nrows) //seule 1 seule rangée doit avoir été lue
    assertEquals(11, amount) //la valeur de amount doit être de 11
  }
  
  @Test
  void testTableExistence() {
    //tester si la table existe
    boolean r = Dao.isTableExistent(gsql, 'TSTA')
    //println "Before creation, TSTA exists : $r"
    assertFalse("La table TSTA ne doit pas exister lors de l'exécution de ce test", r)
    //creation de la table
    TstaDa tda = new TstaDa()
    def tdaDdl = tda.makeTableDdl("", "H2")
    //println "tdaDdl: $tdaDdl"
    int ri = gsql.executeUpdate(tdaDdl)
    //println "Tsta created, ri=$ri" //ri est 0, c'est un peu contre-intuitif
    r = Dao.isTableExistent(gsql, 'TSTA')
    //println "After creation, TSTA exists : $r"
    assertTrue("La table TSTA devrait exister", r)
  }
  
  @Test
  void testNadlFill() {
    boolean debugPrints = true //mettre à true pour envoyer des messages de débogage
    createTables()
    Injrss injrss = new Injrss()
    def vals = injrss.insertInjrss(gsql, 'For testNadlFill')
    def injrssId = injrss.getValue(vals, 'INJRSS_ID')
    if (debugPrints) println "Remplissage RUM..."
    Rum rda = new Rum()
    RssReader rdr = new RssReader();
    File tf = new File("test-files/in/t02rss023.txt")
    tf.eachLine {line->
      if (line.length() == 0) return
      FszNode nd = rdr.readOne(line)
      rda.insertRum(gsql, nd, injrssId)
    }
    if (debugPrints) println "Remplissage NADLR avec les NADLs des RUMs..."
    Nadlr.insertNewNadlsFromRum(gsql)
    gsql.eachRow("select NADL, NADLR from NADLR") { row -> 
      if (debugPrints) println "$row.NADL , $row.NADLR"
    }
    if (debugPrints) println "Création des nouveaux NADLRs..."
    Nadlr.insertNewNadlrs(gsql, injrssId)
    gsql.eachRow("select NADL, NADLR from NADLR") { row ->
      if (debugPrints) println "$row.NADL , $row.NADLR"
    }

    /* 
    def nadls = []
    gsql.eachRow("select distinct NADL from RUM") { row ->
      nadls << row.NADL
    }
    //remplir la table des NADLR
    nadls.each { nadl -> 
      gsql.execute("insert into NADLR(NADL) values (?)", [nadl])
    }
    */
  }
  
  /**
   * Test qui illustre une erreur dans le calcul, où on donne directement un nombre de millisecondes à Time(),
   * mais ensuite c'est affiché en heure locale, donc avec un décalage.
   */
  @Test
  void testCTime1() {
    Dao t1 = new Dao('T1')
    t1.pkcol(new CInteger('T1_ID', true))
    t1.colTime('TIM')
    gsql.execute(t1.makeTableDdl("", "H2", true))
    def t1Rec = t1.makeEmptyValueList()
    t1.setValue(t1Rec, 'TIM', new Time( 23*60*60000 + 59*60000 +  59*1000 +11)) //23:59:59.011
    t1.insertInDb(gsql, t1Rec)
    Long t1Id = t1.getValue(t1Rec, 'T1_ID')
    gsql.eachRow("select '>' || TIM TIMSTR from T1 where T1_ID=?", [t1Id]) {row -> 
      def timstr = row.TIMSTR
      //println "tim : $timstr"
      assertEquals('>00:59:59', timstr)
    }
  }
  
  /**
   * Test qui montre aussi qu'il y a une erreur
   */
  @Test
  void testCTime2() {
    Dao t1 = new Dao('T1')
    t1.pkcol(new CInteger('T1_ID', true))
    t1.colTime('TIM', 2)
    gsql.execute(t1.makeTableDdl("", "H2", true))
    def t1Rec = t1.makeEmptyValueList()
    t1.setValue(t1Rec, 'TIM', new Time( 23*60*60000 + 59*60000 +  59*1000 +11)) //23:59:59.011
    t1.insertInDb(gsql, t1Rec)
    Long t1Id = t1.getValue(t1Rec, 'T1_ID')
    gsql.eachRow("select '>' || TIM TIMSTR from T1 where T1_ID=?", [t1Id]) {row ->
      def timstr = row.TIMSTR
      println "tim : $timstr"
      assertEquals('>00:59:59.01', timstr) //incorrect, on s'attendait à 23:59:59.01
    }
  }

  /**
   * Test qui fait le bon calcul, on rentre l'heure voulue via un Calendar, qui est dans le fuseau horaire local, et c'est lui qui donne
   * le bon nombre de millisecondes qu'il faut mettre dans Time()
   */
  @Test
  void testCTime3() {
    println "In testCTime3..."
    Dao t1 = new Dao('T1')
    t1.pkcol(new CInteger('T1_ID', true))
    t1.colTime('TIM', 4)
    gsql.execute(t1.makeTableDdl("", "H2", true))
    def t1Rec = t1.makeEmptyValueList()
    def timeVal = new Time(0) // new Time(0*60*60*1000 + 59*60*1000 + 59*1000 + 11)
    def cal = Calendar.getInstance()
    cal.set(1970, 0, 1, 23, 59, 59)
    cal.set(Calendar.MILLISECOND, 11)
    timeVal = new Time(cal.getTimeInMillis())
    println "timeVal : $timeVal"
    t1.setValue(t1Rec, 'TIM', timeVal) //23:59:59.011
    t1.insertInDb(gsql, t1Rec)
    Long t1Id = t1.getValue(t1Rec, 'T1_ID')
    gsql.eachRow("select '>' || TIM TIMSTR, TIM from T1 where T1_ID=?", [t1Id]) {row ->
      def timstr = row.TIMSTR
      def tim = row.TIM
      println "timstr : $timstr"
      //assertEquals('>00:59:59.011', timstr)
      println "tim : $tim (class ${tim.class.name})"
    }
  }

  /**
   * Test qui utilise la fonction setTimeValue() pour mettre la bonne valeur dans une colonne TIME
   */
  @Test
  void testCTime4() {
    println "In testCTime4..."
    Dao t1 = new Dao('T1')
    t1.pkcol(new CInteger('T1_ID', true))
    t1.colTime('TIM', 4)
    gsql.execute(t1.makeTableDdl("", "H2", true))
    def t1Rec = t1.makeEmptyValueList()
    t1.setTimeValue(t1Rec, 'TIM', 23, 59, 59, 11) //23:59:59.011
    t1.insertInDb(gsql, t1Rec)
    Long t1Id = t1.getValue(t1Rec, 'T1_ID')
    gsql.eachRow("select '>' || TIM TIMSTR, TIM from T1 where T1_ID=?", [t1Id]) {row ->
      def timstr = row.TIMSTR
      def tim = row.TIM
      println "timstr : $timstr"
      //assertEquals('>00:59:59.011', timstr)
      println "tim : $tim (class ${tim.class.name})"
    }
  }

}
