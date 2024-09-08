package fr.gpmsi;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Un ensemble de transformateurs de String prêts à l'emploi.
 * @author hkaradimas
 *
 */
public class StringTransformers {
	
	private static StringTransformable frToUsNr = new StringTransformable() {
		
		NumberFormat fnf = NumberFormat.getInstance(Locale.FRENCH);
		
		@Override
		public String transform(String input) throws ParseException {
			String transformed = "";
			if (input != null && input.trim().length() > 0) {
				/* Number nr = */ fnf.parse(input);
				//if we are here, this means format is correct.
				transformed = input.replaceAll(",", ".");
			}
			return transformed;
		}
	};

	private static StringTransformable frToIsoDt = new StringTransformable() {
		SimpleDateFormat fdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat isodf = new SimpleDateFormat("yyyy-MM-dd");
		
		@Override
		public String transform(String input) throws ParseException {
			String transformed = "";
			if (input != null && input.trim().length() > 0) {
				Date dt = fdf.parse(input);
				transformed = isodf.format(dt);
			}
			return transformed;
		}
	};
	
	private static StringTransformable trim = new StringTransformable() {
      
      @Override
      public String transform(String input) throws ParseException {
        if (input == null) return "";
        return input.trim();
      }
    };
    
    /**
     * Constructeur par défaut
     */
	public StringTransformers() {	}

	/**
	 * Convertir un nombre français (avec des virgules) en nombre US (avec des points)
	 * @return le transformateur "frToUsNr" qui fait cette conversion
	 */
	public static StringTransformable frenchToUsNumber() { return frToUsNr; }
	
	/**
	 * Convertir format francais (jj/mm/aaaa) en format iso (aaaa-mm-jj).
	 * @return le transformateur "frToIsoDt" qui fait cette conversion
	 */
	public static StringTransformable frenchToIsoDate() { return frToIsoDt; }
	
	/**
	 * Enlever les espaces avant et après (même chose que {@link String#trim()}).
	 * Si la string est null, renvoie une chaine vide ("")
	 * @return le transformateur "trim" qui fait cette conversion
	 */
	public static StringTransformable trim() { return trim; }
	
}
