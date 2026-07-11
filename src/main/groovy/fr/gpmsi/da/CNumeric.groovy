package fr.gpmsi.da

import java.sql.ResultSet
import java.sql.Types
import java.sql.PreparedStatement
import java.text.NumberFormat
import java.text.ParseException
import fr.gpmsi.pmsixml.NumUtils
import groovy.transform.EqualsAndHashCode

/**
 * Définition d'une colonne de type Numeric (représenté en java par un BigDecimal)
 */
@EqualsAndHashCode(callSuper = true)
class CNumeric extends ColumnDef implements IZeroForNullAllowed {
    int precision = 15
    int scale = 0
    NumberFormat nf
    boolean zeroForNullAllowed = true    

    CNumeric(String name_p) { setName(name_p); setSqlType(Types.NUMERIC) }

    CNumeric(String name_p, int precision) { 
        setName(name_p)
        this.precision = precision
        setSqlType(Types.NUMERIC)
    }
    
    CNumeric(String name_p, int precision, int scale) { 
        setName(name_p)
        this.precision = precision
        this.scale = scale
        setSqlType(Types.NUMERIC)
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
                if (prefs.illegalDatesToNull) return null
                else throw new Exception("original string '$str'", pex3)
            }
        }
        try {
            return NumUtils.parse(str) //elimine les zéros initiaux et convertit en BigDecimal
        }
        catch (NumberFormatException nfex) {
            throw new Exception("original String '$str'", nfex)
        }
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

    String getDdl(String dialect) {
      String eddl = extraDdl ? ' ' + extraDdl : ''
      if (dialect.equalsIgnoreCase("H2")) {
        if (scale == 0) return "$name NUMERIC($precision)$eddl"
        else return "$name NUMERIC($precision, $scale)$eddl"
      }
      else return "Dialecte non pris en charge : $dialect"
    }

    /**
     * Est-ce que si NULL n'est pas autorisé, on peut envoyer 0 à la place ?
     * @return true si zero est autorise à la place de null
     */
    boolean isZeroForNullAllowed() { return zeroForNullAllowed; }

    /**
     * Définir si lorsque NULL n'est pas autorisé, on peut envoyer 0 à la place
     * @param zeroForNullAllowed true si lorsque NULL n'est pas autorisé, on peut envoyer 0 à la place
     */
    void setZeroForNullAllowed(boolean zeroForNullAllowed) { this.zeroForNullAllowed = zeroForNullAllowed }

    @Override
    public Object getObjectForZero() { return BigDecimal.ZERO }

    /**
     * Retourne le nombre total de chiffres décimaux. Par exemple pour enregistrer 1234.56 il faut setPrecision(6) au minimum.
     * @return Le nombre total de chiffres décimaux
     */
    public int getPrecision() {
      return precision;
    }

    /**
     * Définit le nombre total de chiffres décimaux. Par exemple pour enregistrer 1234.56 il faut setPrecision(6) au minimum.
     * @param precision Le nombre total de chiffres décimaux
     */
    public void setPrecision(int precision) {
      this.precision = precision;
    }

    /**
     * Retourne le nombre de chiffre derrière la virgule à enregistrer. Par exemple pour enregistrer 1234.56 il faut setScale(2) au minimum.
     * Par défaut à 0.
     * @return Le nombre de chiffres derrière la virgule
     */
    public int getScale() {
      return scale;
    }

    /**
     * Définit le nombre de chiffre derrière la virgule à enregistrer. Par exemple pour enregistrer 1234.56 il faut setScale(2) au minimum.
     * Par défaut à 0.
     * @param scale Le nombre de chiffres derrière la virgule
     */
    public void setScale(int scale) {
      this.scale = scale;
    }

}
