package fr.gpmsi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Properties;

/**
 * Utilitaire de réparation/transformation de fichier de bas niveau.
 * Utilise un fichier de propriétés pour donner :
 * <dl>
 * <dt>in
 *     <dd>Fichier en entrée (encodage ISO-8859-1)
 * <dt>out
 *     <dd>Fichier en sortie (encodage ISO-8859-1)
 * <dt>offset
 *     <dd>Décalage à partir duquel commencer la recherche
 * <dt>len
 *     <dd>Longueur à utiliser pour la recherche
 * </dl> 
 * Chaque couple est déclaré par "search.1" / "repl.1", "search.2" / "repl.2", etc.
 * Peut être utilisé pour remplacer une UF par une autre en travaillant
 * à une position fixe de façon "brutale" (sans analyser du tout les fichiers).
 * Voir le fichier associé 
 * <a href="doc-files/SearchReplaceExample.properties">SearchReplaceExample.properties</a>
 * pour un exemple. 
 * @author hkaradimas
 *
 */
public class SearchReplace
{
  Properties props = new Properties();
  HashMap<String, String> repByName = new HashMap<String, String>();
  String inPath;
  String outPath;
  int offset = 0;
  int len = 0;
  int nsubst = 0;

  /**
   * Constructeur avec chemin du fichier de propriétés.
   * @param propsPath Chemin du fichier de propriétés à charger
   * @throws IOException Si erreur E/S en lisant le fichier de propriétés
   */
  public SearchReplace(String propsPath) throws IOException {
    props = new Properties();
    FileInputStream fis = new FileInputStream(propsPath);
    props.load(fis);
    fis.close();
    for (int i = 1; i <= 99; i++) {
      String search = props.getProperty("search."+i);
      String repl = props.getProperty("repl."+i);
      if (!isEmpty(search)) {
        if (repl == null) repl = "";
        repByName.put(search, repl);
      }
    }//for
    offset = Integer.parseInt(props.getProperty("offset"));
    len =  Integer.parseInt(props.getProperty("len"));
    inPath = props.getProperty("in");
    outPath = props.getProperty("out");
  }

  /** 
   * Méthode triviale pour tester si une chaîne est nulle ou vide 
   * @see String#trim()
   * @param str La chaîne à tester
   * @return true si la chaîne est vide
   */
  public static final boolean isEmpty(String str) {
    return str == null || str.trim().equals("");
  }
  
  private void run() throws Exception {
    FileInputStream fis = new FileInputStream(inPath);
    InputStreamReader isr = new InputStreamReader(fis, "ISO-8859-1");
    BufferedReader br = new BufferedReader(isr);
    File outFile = new File(outPath);
    FileOutputStream fos = new FileOutputStream(outFile);
    
    OutputStreamWriter osw = new OutputStreamWriter(fos, "ISO-8859-1");
    BufferedWriter bw = new BufferedWriter(osw);
    try {
      String line = br.readLine();
      while (line != null) {
        String outline;
        if (line.length() >= offset+len) {
          String toSearch = line.substring(offset, offset+len);
          String before = line.substring(0, offset);
          String after = line.substring(offset+len);
          String replacement;
          if (repByName.containsKey(toSearch)) {
            replacement = repByName.get(toSearch);
            nsubst++;
          }
          else replacement = toSearch;
          outline = before + replacement + after;
        }
        else outline = line;
        bw.write(outline);
        bw.newLine();
        line = br.readLine();
      }//while
      System.out.println("Finished, "+nsubst+" substitutions.");
    }
    finally {
      br.close();
      isr.close();
      fis.close();
      bw.close();
      osw.close();
      fos.close();
    }
  }
  
  /**
   * main
   * @param args Arguments
   * @throws Exception -
   */
  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage : SearchReplace <path_of_properties_file>");
      return;
    }
    String path = args[0];
    SearchReplace app = new SearchReplace(path);
    app.run();
  }

}
