package fr.gpmsi.da

import java.sql.Timestamp
import java.sql.Types
import java.sql.ResultSet
import java.sql.PreparedStatement
import java.text.SimpleDateFormat

import groovy.transform.EqualsAndHashCode

import java.text.DateFormat

/**
 * Définition d'une colonne de type DATETIME (pour java c'est pareil que Timestamp, mais dans la base de données, DATETIME permet de stocker
 * date + temps avant 1970
 */
@EqualsAndHashCode(callSuper = true)
class CDatetime extends ColumnDef {
    static SimpleDateFormat sdf = new SimpleDateFormat('yyyy-mm-dd HH:MM:ss')
    DateFormat df = sdf
    
    CDatetime(String name) { setName(name); setSqlType(Types.TIMESTAMP) }
    
    CDatetime setDateFormat(DateFormat newDateFormat) {
        df = newDateFormat
        return this
    }
    
    DateFormat getDateFormat() { return df }
    
    //make a correctly typed value from the given String
    Object stringToValue(String str, DaPreferences prefs) {
        if (str == null || str.trim() == "") return null
        else {
            try { return new Timestamp(df.parse(str).getTime()) }
            catch (IllegalArgumentException iaex) {
                if (prefs.illegalDatesToNull) return null
                else throw new Exception("original String '$str'", iaex)
            }
        }
    }
    
    //make a String from the given value
    //returns empty String if val is not of type Date
    String valueToString(Object val, DaPreferences prefs) {
        if (val == null) return ""
        if (val instanceof Date) return df.format(val) //
        else return ""
    }

    /** Set value in PreparedStatement (including correct type for setNull). Accepts Datetime(s) and java.util.Date(s) */
    void setPsValue(PreparedStatement ps, int index, Object val) {
        if (val == null) ps.setNull(index, Types.TIMESTAMP)
        else {
            if (val instanceof Timestamp) ps.setTimestamp(index, val as Timestamp)
            else {
                java.util.Date d2 = val as java.util.Date
                Timestamp t2 = new Timestamp()
                t2.setTime(d2.getTime())
                ps.setTimestamp(index, t2)
            }
        }
    }

    /** Get value from ResultSet */
    Object getRsValue(ResultSet rs, int index) {
        return rs.getTimestamp(index)
    }

    String getDdl(String dialect) {
      if (dialect.equalsIgnoreCase("H2")) {
        String eddl = extraDdl ? ' ' + extraDdl : '' 
        return "$name DATETIME$eddl"
      }
      else return "Dialecte non pris en charge : $dialect"
    }


}