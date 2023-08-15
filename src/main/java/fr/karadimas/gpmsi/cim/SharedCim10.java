package fr.karadimas.gpmsi.cim;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.karadimas.gpmsi.StringTable;
import fr.karadimas.gpmsi.StringTransformable;

/**
 * Accès à la Cim 10 chargée en mémoire et partagée entre les différentes
 * utilisations, pour éviter qu'elle ne soit chargée de multiples fois.
 * Le chemin d'accès à la CIM au format csv ATIH mais en encodage UTF-8 doit être
 * présent dans la variable statique 'sharedCim10Path'. 
 * Pour obtenir ce fichier :
 * <ul>
 * <li>Télécharger sur le site de l'ATIH le kit de nomenclature cim10 (fichier NomenclatureCim10.zip)
 * <li>En extraire le fichier LIBCIM10MULTI.TXT qui contient l'ensemble de la CIM10 usage PMSI
 * <li>Transformer le fichier (utiliser pour cela Excel ou Libre Office) :
 * <li>Remplacer les '|' par ';'
 * <li>Enlever tous les espaces des codes CIM10
 * <li>Ajouter au début une ligne de colonnes :
 * <pre>CDE;C2;C3;C4;LIBC;LIBL</pre>
 * <li>Exporter le tout avec l'encodage UTF-8, sous le nom <code>cim10_utf8.csv</code>
 * </ul>
 * @author hkaradimas
 *
 */
public class SharedCim10 {
  public static String sharedCim10Path = "~/.gpmsi/cim/cim10_utf8.csv";

  private static StringTable cim10 = null;
  private static String cim10codes = null;
  
  /** Si mis à true, des informations sur le temps de chargement seront envoyees sur System.out */
  public static boolean PROFILING = false;
  
  /** Constructeur privé car cette classe ne doit pas être instanciée */
  private SharedCim10() {}

  public static StringTable getOrLoadCim10()
      throws IOException 
  {
    if (cim10 == null) {
      long startTime = 0;
      long endTime = 0;
      if (PROFILING) startTime = System.currentTimeMillis();
      String path = sharedCim10Path;
      //remplacer le tilde par le repertoire home de l'utilisateur (%userprofile% dans Windows)
      path = path.replace("~", System.getProperty("user.home"));
      path = path.replace('/', File.separatorChar);
      cim10 = new StringTable("CIM10PMSI");
      cim10.readFrom(new File(path), "UTF-8");
      if (PROFILING) {
        endTime = System.currentTimeMillis();
        System.err.println("Temps de chargement CIM10 : "+(endTime-startTime)+"ms");
      }
      try {
        //normaliser tous les codes CIM10
        cim10.transform(0, new StringTransformable() {          
          @Override
          public String transform(String input) throws ParseException {
            return normalizeCode(input);
          }
        });
      } 
      catch (ParseException e) {
        throw new RuntimeException(e);
      }
      //ajouter un index pour trouver plus vite les codes CIM 10
      cim10.addIndex(0);
      //construire une String qui va contenir tous les codes CIM10 ce qui va accélérer
      //la recherche par expressions régulières
      StringBuilder sb = new StringBuilder();
      int rowCount = cim10.getRowCount();
      for (int row = 0; row < rowCount; row++) {
        sb.append(cim10.getValue(row, 0));
        sb.append('\n');
      }
      cim10codes = sb.toString();
    }
    return cim10;
  }
  
  
  /**
   * Normaliser le code CIM10 :
   * <ul>
   * <li>Les espaces de début et fin sont enlevés
   * <li>Les points '.' sont supprimés
   * <li>Le code est converti en majuscules
   * </ul>
   * @param code Le code
   * @return Le code normalisé
   */
  public static String normalizeCode(String code) {
    return code == null ? "" : code.trim().toUpperCase().replace(".","");
  }
  
  /**
   * Rechercher les codes qui repondent au motif donné ; par ex. A03.+ pour
   * avoir tous les codes enfant de A03.
   * @param pattern Le motif regex déjà compilé
   * @return Une liste de codes
   * @throws IOException Si erreur E/S
   */
  public static List<String> findCodes(Pattern pattern)
      throws IOException 
  {
    getOrLoadCim10();
    long startTime = 0L;
    long endTime = 0L;
    if (PROFILING) startTime = System.currentTimeMillis();
    ArrayList<String> result = new ArrayList<>();
    Matcher m = pattern.matcher(cim10codes);
    while (m.find()) result.add(m.group());
    if (PROFILING) {
      endTime = System.currentTimeMillis();
      System.err.println("Temps de recherche des codes pour '"+pattern.pattern()+"' : "+(endTime-startTime)+"ms");
    }
    return result;
  }
  
  /**
   * Parcourir toute la table et rechercher les codes qui sont entre first et last inclus ou exclus.
   * 
   * @param first Premier 
   * @param last Dernier
   * @param includeLast Le dernier est inclus
   * @return Une liste de codes
   * @throws IOException Si erreur E/S
   */
  public static List<String> findCodes(String first, String last, boolean includeLast)
      throws IOException 
  {
    getOrLoadCim10();
    String first_n = normalizeCode(first);
    String last_n = normalizeCode(last);
    ArrayList<String> codes = new ArrayList<>();
    int nrows = cim10.getRowCount();
    for (int row = 0; row < nrows; row++) {
      String cc = cim10.getValue(row, 0);
      if (includeLast) {
        if (cc.compareTo(first_n) >= 0 && cc.compareTo(last_n) <= 0) codes.add(cc);
      }
      else {
        if (cc.compareTo(first_n) >= 0 && cc.compareTo(last_n) < 0) codes.add(cc);
      }
    }
    return codes;
  }
  
  /**
   * Trouver un code à l'aide d'une expression régulière
   * @param regexPattern L'expression régulière à utiliser
   * @return La liste de codes
   * @throws IOException Si erreur E/S
   */
  public static List<String> findCodes(String regexPattern)
      throws IOException 
  {
    return findCodes(Pattern.compile(regexPattern)); 
  }
  
  /**
   * Recherche si un code CIM10 existe dans la table CIM10 PMSI
   * @param code Le code à chercher (la fonction le normalisera avant de le rechercher)
   * @return true si le code existe
   * @throws IOException si erreur e/s
   */
  public static boolean codeExists(String code)
      throws IOException 
  {
    getOrLoadCim10();
    return cim10.contains(0, normalizeCode(code));
  }
}
