package fr.gpmsi.da

import java.sql.Timestamp
import java.sql.ResultSet
import java.sql.PreparedStatement
import java.text.SimpleDateFormat
import java.text.DateFormat

/** Column of Data Type Timestamp */
class CTimestamp extends ColumnDef {
    static SimpleDateFormat sdf = new SimpleDateFormat('yyyy-mm-dd HH:MM:ss')
    DateFormat df = sdf
    
    CTimestamp(String name) { setName(name) }
    
    CTimestamp setDateFormat(DateFormat newDateFormat) {
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
                if (prefs.illegalDatesToNull) return null else throw iaex
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

    /** Set value in PreparedStatement (including correct type for setNull). Accepts Timestamp(s) and java.util.Date(s) */
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

}