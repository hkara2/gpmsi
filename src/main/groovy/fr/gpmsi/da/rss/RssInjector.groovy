package fr.gpmsi.da.rss

import fr.gpmsi.pmsixml.FszNode
import fr.gpmsi.pmsixml.RssReader
import groovy.sql.Sql

/**
 * Injecteur de RSS.
 * Les tables doivent avoir été créées
 * A partir d'une source de RUMs remplit les tables RUM, ZA, DA et RSS.
 * @see RssTables#createTables
 * @author hkaradimas
 *
 */
class RssInjector {
  
  Sql connection
  
  void injectRss(Reader rdr, Long injrssId) {
    Rum rum = new Rum()
    RssReader rssrdr = new RssReader();
    rdr.eachLine {line->
      FszNode rumNd = rssrdr.readOne(line)
      rum.insertRum(connection, rumNd)
    }
    //créer une table RSS qui permet de raisonner au niveau du RSS
    Rss.instance.fillFromRum(connection)
  }

  public Sql getConnection() {
    return connection;
  }

  public void setConnection(Sql connection) {
    this.connection = connection;
  }
  
}