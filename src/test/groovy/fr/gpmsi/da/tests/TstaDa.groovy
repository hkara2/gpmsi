package fr.gpmsi.da.tests

import fr.gpmsi.da.CInteger
import fr.gpmsi.da.Dao

/**
 * Classe Dao pour une table de test nommée 'tsta'
 * @author hkaradimas
 *
 */
class TstaDa extends Dao {
  
  TstaDa() {
    super("TSTA")
    pkcol(new CInteger('ID', true))
    colChar('FOO', 20)
    colInteger('AMOUNT')
  }
  
}