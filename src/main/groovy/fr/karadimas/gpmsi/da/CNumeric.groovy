package fr.karadimas.gpmsi.da

import java.sql.ResultSet
import java.sql.PreparedStatement
import java.text.NumberFormat
import java.text.ParseException
import fr.karadimas.pmsixml.NumUtils

class CNumeric extends ColumnDef {
    int precision = 15
    int scale = 0
    NumberFormat nf

    CNumeric(String name_p) { setName(name_p) }

    CNumeric(String name_p, int precision) { 
        setName(name_p)
        this.precision = precision
    }
    
    CNumeric(String name_p, int precision, int scale) { 
        setName(name_p)
        this.precision = precision
        this.scale = scale
    }
    
    CNumeric setNumberFormat(NumberFormat nf) { this.nf = nf; return this }
    
    //make a correctly typed value from the given String
    Object stringToValue(String str, DaPreferences prefs) {
        if (str == null || str.trim() == "") return null
        if (nf != null) {
            try {
                return nf.parse(str) //use supplied NumberFormat
            }
            catch (ParseException pex3) {
                if (prefs.illegalDatesToNull) return null else throw pex3
            }
        }
        return NumUtils.parse(str) //elimine les z�ros initiaux et convertit en BigDecimal
    }
    
    //make a String from the given value
    String valueToString(Object val, DaPreferences prefs) {
        if (val == null) return ""
        if (nf != null) return nf.toString()
        else return val.toString()
    }

    /** Set value in PreparedStatement (including correct type for setNull). Uses BigDecimal. */
    void setPsValue(PreparedStatement ps, int index, Object val) {
        if (val == null) ps.setNull(index, Types.NUMERIC)
        else ps.setBigDecimal(val as BigDecimal)
    }
    
    /** Get value from ResultSet */
    Object getRsValue(ResultSet rs, int index) {
        return ps.getBigDecimal(index)
    }

}
