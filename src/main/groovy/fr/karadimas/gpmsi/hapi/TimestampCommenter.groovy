package fr.karadimas.gpmsi.hapi;

import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Ajouter pour tous les elements XML de type &lt;TS. un commentaire avec la date decodee
 * @author hkaradimas
 *
 */
class TimestampCommenter {
  static SimpleDateFormat longsdf = new SimpleDateFormat('yyyyMMddHHmmss')
  static SimpleDateFormat shortsdf = new SimpleDateFormat('yyyyMMdd')
  static SimpleDateFormat fdf = new SimpleDateFormat('dd/MM/yyyy HH:mm:ss')
  
  String commentTimestamps(String xml) {
    xml.replaceAll('<TS.\\d>(\\d+)</TS.\\d>') {String all, String m1 ->
      try {
        def dateFr
        if (m1.length() < 10) dateFr = shortsdf.parse(m1)
        else dateFr = longsdf.parse(m1)
        def dateFrStr = fdf.format( dateFr ) 
        "$all <!--$dateFrStr-->"
      }
      catch (ParseException e) {
        all
      }  
    }
  }
  
}