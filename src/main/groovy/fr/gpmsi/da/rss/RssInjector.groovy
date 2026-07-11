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
  
  /**
   * Constructeur sans arguments
   */
  RssInjector() {}
  
  /**
   * Constructeur avec la connection à utiliser
   * @param connection La connection sql
   */
  RssInjector(Sql connection) {
    this.connection = connection
  }
  
  /**
   * A partir du Reader fourni, lire les Rums et les injecter en table
   * @param rdr Le Reader
   * @param injrssId L'id d'injection
   */
  void injectRss(Reader rdr, Long injrssId) {
    Rum rum = new Rum()
    RssReader rssrdr = new RssReader();
    rdr.eachLine {line->
      FszNode rumNd = rssrdr.readOne(line)
      rum.insertRum(connection, rumNd, injrssId)
    }
    //créer une table RSS qui permet de raisonner au niveau du RSS
    Rss.instance.fillFromRum(connection)
  }

  /**
   * Ramener la connection
   * @return la connection
   */
  public Sql getConnection() {
    return connection;
  }

  /**
   * Définir la connection
   * @param connection la connection
   */
  public void setConnection(Sql connection) {
    this.connection = connection;
  }
  
}