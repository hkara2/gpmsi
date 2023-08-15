package fr.karadimas.gpmsi.da

import java.text.NumberFormat
import java.text.ParseException
import java.sql.ResultSet
import java.sql.PreparedStatement

class CInteger extends ColumnDef {
    NumberFormat nf
    CInteger(String name) { setName(name) }
    CInteger(String name, NumberFormat nf) { setName(name) ; this.nf = nf }
    
    CInteger setNumberFormat(NumberFormat nf) { this.nf = nf; return this}
    
    NumberFormat getNumberFormat() { return nf }
    
    //make a correctly typed value from the given String
    Object stringToValue(String str, DaPreferences prefs) {
        println("In CInteger#stringToValue(), str:$str")
        if (str == null || str.trim() == "") return null
        if (nf != null) {
            try {
                return nf.parse(str) //use supplied NumberFormat
            }
            catch (ParseException pex3) {
                if (prefs.illegalDatesToNull) return null else throw pex3
            }
        }
        return Long.valueOf(str)
    }
    
    //make a String from the given value
    String valueToString(Object val, DaPreferences prefs) {
        if (val == null) return ""
        return idoDf.format(val)
    }
    
    /** Set value in PreparedStatement (including correct type for setNull). Uses setLong */
    void setPsValue(PreparedStatement ps, int index, Object val) {
        if (val == null) ps.setNull(Types.INTEGER)
        else ps.setLong(val as long)
    }

    /** Get value from ResultSet */
    Object getRsValue(ResultSet rs, int index) {
        long v = rs.getLong()
        if (rs.wasNull()) return null else return v
    }
}
