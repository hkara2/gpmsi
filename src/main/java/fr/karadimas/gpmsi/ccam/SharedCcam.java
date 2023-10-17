package fr.karadimas.gpmsi.ccam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.karadimas.gpmsi.StringTable;

/**
 * Accès à la CCAM chargée en mémoire et partagée entre les différentes
 * utilisations, pour éviter qu'elle ne soit chargée de multiples fois.
 * Le chemin d'accès à la CCAM au format csv ATIH mais en encodage UTF-8 doit être
 * présent dans la variable statique 'sharedCcamPath' (il est permis de modifier cette variable). 
 * Pour obtenir ce fichier :
 * <ul>
 * <li>Prendre comme point de départ la CCAM descriptive à usage PMSI. Ex : <a href="https://www.atih.sante.fr/ccam-descriptive-usage-pmsi-2023">https://www.atih.sante.fr/ccam-descriptive-usage-pmsi-2023</a>
 * <li>Transformer le fichier : fichier_complementaire_ccam_descriptive_a_usage_pmsi...
 * <li>Dans la première ligne, remplacer les titres de colonne par les abréviations suivantes : <br>
 *   <pre>CODE7;XPM;CODEP;SUBCTL;SUBC7;LIBELLE;INFCOMP;CONSCOD;TYPMOD;VCCAMC;TYPLL;CACT;PHASE;RC;AP;ETM;RGT;CCL;PFG20P0;PFG20P1;PFG20P2;PFG20P3;PFG19P0;PFG19P1;PFG19P2;PFG19P3;PF20;PF19;ICRPUB1;ICRPRIV1;ICR4;ICRA;ICRR;MODACT;GESTCTXT;GESTC123;GESTC4;GESTC5;ANESC;NCONSPEC;NOMPHAS;DAVIS;NSD1;TSD1;NSD2;TSD2;NSD3;TSD3;NSD4;TSD4;DDVAL;DFVAL</pre>
 * <li>Fermer le fichier si on est sous Excel.
 * <li>Le réouvrir avec Libre Office Calc.
 * <li>Exporter en .csv avec séparateur point-virgule, encodage UTF-8, avec le nom : <code>ccam_descr_pmsi_utf8.csv</code>
 * </ul>
 * @author hkaradimas
 *
 */
public class SharedCcam {

  /** Constructeur privé car cette classe ne doit pas être instanciée */
  private SharedCcam() {}
  
  /** Chemin de la CCAM à charger */
  public static String sharedCcamPath = "~/.gpmsi/ccam/ccam_descr_pmsi_utf8.csv";

  private static StringTable ccam = null;
  private static String ccamcodes7 = null;
  private static String ccamcodesp = null;
  
  /** Si mis à true, des informations sur le temps de chargement seront envoyees sur System.out */
  public static boolean PROFILING = false;
    
  /**
   * Retourner la CCAM en la chargeant si besoin
   * @return Une StringTable avec la CCAM
   * @throws IOException Si erreur E/S lors de la lecture de la CCAM
   */
  public static StringTable getOrLoadCcam()
      throws IOException 
  {
    if (ccam == null) {
      long startTime = 0;
      long endTime = 0;
      if (PROFILING) startTime = System.currentTimeMillis();
      String path = sharedCcamPath;
      //remplacer le tilde par le repertoire home de l'utilisateur (%userprofile% dans Windows)
      path = path.replace("~", System.getProperty("user.home"));
      path = path.replace('/', File.separatorChar); //remplacer le / par le séparateur normal de plateforme
      ccam = new StringTable("CCAMPMSI");
      File ccamFile = new File(path);
      if (!ccamFile.exists()) throw new IOException("Fichier non trouve " + ccamFile);
      ccam.readFrom(ccamFile, "UTF-8");
      if (PROFILING) {
        endTime = System.currentTimeMillis();
        System.err.println("Temps de chargement CCAM : "+(endTime-startTime)+"ms");
      }
      //ajouter un index pour trouver plus vite les codes CCAM normaux
      int code7Col = ccam.getColumnNumber("CODE7");
      if (code7Col < 0) throw new RuntimeException("Erreur colonne CODE7 non trouvee dans "+ccamFile.getName());
      ccam.addIndex(code7Col);
      //ajouter un index pour trouver plus vite les codes CCAM avec extension PMSI (le code plein)
      int codepCol = ccam.getColumnNumber("CODEP");
      if (codepCol < 0) throw new RuntimeException("Erreur colonne CODEP non trouvee dans "+ccamFile.getName());
      ccam.addIndex(codepCol);
      //construire deux Strings qui vont contenir tous les codes CCAM (7 car. et pleins) ce qui va accélérer
      //la recherche par expressions régulières
      StringBuilder sb7 = new StringBuilder();
      StringBuilder sbp = new StringBuilder();
      int rowCount = ccam.getRowCount();
      for (int row = 0; row < rowCount; row++) {
        sb7.append(ccam.getValue(row, code7Col));
        sb7.append('\n');
        sbp.append(ccam.getValue(row, codepCol));
        sbp.append('\n');
      }
      ccamcodes7 = sb7.toString();
      ccamcodesp = sbp.toString();
    }
    return ccam;
  }
  
  /**
   * Normaliser le code CCAM :
   * <ul>
   * <li>Les espaces de début et fin sont enlevés
   * <li>Le code est converti en majuscules
   * </ul>
   * @param code Le code
   * @return Le code normalisé
   */
  public static String normalizeCode(String code) {
    return code == null ? "" : code.trim().toUpperCase();
  }
  
  /**
   * Rechercher les codes à 7 caractères qui repondent au motif donné ; par ex. JAFC.+ pour
   * avoir tous les codes enfant de JAFC.
   * @param pattern Le motif regex déjà compilé
   * @return Une liste de codes
   * @throws IOException Si erreur E/S
   */
  public static List<String> findCodes7(Pattern pattern)
      throws IOException 
  {
    getOrLoadCcam();
    long startTime = 0L;
    long endTime = 0L;
    if (PROFILING) startTime = System.currentTimeMillis();
    ArrayList<String> result = new ArrayList<>();
    Matcher m = pattern.matcher(ccamcodes7);
    while (m.find()) result.add(m.group());
    if (PROFILING) {
      endTime = System.currentTimeMillis();
      System.err.println("Temps de recherche des codes pour '"+pattern.pattern()+"' : "+(endTime-startTime)+"ms");
    }
    return result;
  }

  /**
   * Rechercher les codes pleins (7 caractères + extension PMSI) qui repondent au motif donné ; par ex. JAFC005-.+ pour
   * avoir tous les codes enfant de JAFC005 qui ont une extension PMSI.
   * @param pattern Le motif regex déjà compilé
   * @return Une liste de codes
   * @throws IOException Si erreur E/S
   */
  public static List<String> findCodesp(Pattern pattern)
      throws IOException 
  {
    getOrLoadCcam();
    long startTime = 0L;
    long endTime = 0L;
    if (PROFILING) startTime = System.currentTimeMillis();
    ArrayList<String> result = new ArrayList<>();
    Matcher m = pattern.matcher(ccamcodesp);
    while (m.find()) result.add(m.group());
    if (PROFILING) {
      endTime = System.currentTimeMillis();
      System.err.println("Temps de recherche des codes pour '"+pattern.pattern()+"' : "+(endTime-startTime)+"ms");
    }
    return result;
  }

  /**
   * Parcourir toute la table et rechercher les codes à 7 caractères qui sont entre first et last inclus ou exclus.
   * Chaque code n'est ajouté qu'une fois.
   * 
   * @param first Premier 
   * @param last Dernier
   * @param includeLast true si le dernier est inclus
   * @return Une liste de codes
   * @throws IOException Si erreur E/S
   */
  public static List<String> findCodes7(String first, String last, boolean includeLast)
      throws IOException 
  {
    getOrLoadCcam();
    String first_n = normalizeCode(first);
    String last_n = normalizeCode(last);
    HashSet<String> codeSet = new HashSet<>();
    ArrayList<String> codes = new ArrayList<>();
    int code7Col = ccam.getColumnNumber("CODE7");
    if (code7Col < 0) throw new RuntimeException("Column CODE7 not found");
    int nrows = ccam.getRowCount();
    for (int row = 0; row < nrows; row++) {
      String cc = ccam.getValue(row, code7Col);
      if (includeLast) {
        if (cc.compareTo(first_n) >= 0 && cc.compareTo(last_n) <= 0 && !codeSet.contains(cc)) {
          codes.add(cc); codeSet.add(cc);
        }
      }
      else {
        if (cc.compareTo(first_n) >= 0 && cc.compareTo(last_n) < 0 && !codeSet.contains(cc))  {
          codes.add(cc); codeSet.add(cc);
        }
      }
    }//for
    return codes;
  }
  
  /**
   * Parcourir toute la table et rechercher les codes pleins (7 caractères + extension PMSI) qui sont entre first et last inclus ou exclus.
   * Chaque code n'est ajouté qu'une fois.
   * 
   * @param first Premier 
   * @param last Dernier
   * @param includeLast true si le dernier est inclus
   * @return Une liste de codes
   * @throws IOException Si erreur E/S
   */
  public static List<String> findCodesp(String first, String last, boolean includeLast)
      throws IOException 
  {
    getOrLoadCcam();
    String first_n = normalizeCode(first);
    String last_n = normalizeCode(last);
    HashSet<String> codeSet = new HashSet<>();
    ArrayList<String> codes = new ArrayList<>();
    int codepCol = ccam.getColumnNumber("CODEP");
    if (codepCol < 0) throw new RuntimeException("Column CODEP not found");
    int nrows = ccam.getRowCount();
    for (int row = 0; row < nrows; row++) {
      String cc = ccam.getValue(row, codepCol);
      if (includeLast) {
        if (cc.compareTo(first_n) >= 0 && cc.compareTo(last_n) <= 0 && !codeSet.contains(cc)) {
          codes.add(cc); codeSet.add(cc);
        }
      }
      else {
        if (cc.compareTo(first_n) >= 0 && cc.compareTo(last_n) < 0 && !codeSet.contains(cc))  {
          codes.add(cc); codeSet.add(cc);
        }
      }
    }//for
    return codes;
  }
 
  /**
   * Trouver un code à 7 caractères à l'aide d'une expression régulière
   * @param regexPattern L'expression régulière à utiliser
   * @return La liste de codes
   * @throws IOException Si erreur E/S
   */
  public static List<String> findCodes7(String regexPattern)
      throws IOException 
  {
    return findCodes7(Pattern.compile(regexPattern)); 
  }
  
  /**
   * Trouver un code ccam plein (7 caractères + extension PMSI) à l'aide d'une expression régulière
   * @param regexPattern L'expression régulière à utiliser
   * @return La liste de codes
   * @throws IOException Si erreur E/S
   */
  public static List<String> findCodesp(String regexPattern)
      throws IOException 
  {
    return findCodesp(Pattern.compile(regexPattern)); 
  }
  
  /**
   * Recherche si un code CCAM plein (7 caractères + extension PMSI) existe dans la table CCAM PMSI
   * @param code Le code à chercher (la fonction le normalisera avant de le rechercher)
   * @return true si le code existe
   * @throws IOException si erreur e/s
   */
  public static boolean codeExists(String code)
      throws IOException 
  {
    getOrLoadCcam();
    int codepCol = ccam.getColumnNumber("CODEP");
    if (codepCol < 0) throw new RuntimeException("Column CODEP not found");
    return ccam.contains(codepCol, normalizeCode(code));
  }
  
}
