package fr.karadimas.gpmsi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Représente un ensemble de codes.
 * Chaque code est garanti ne pas commencer ni ne finir par un espace, et toutes
 * les lettres sont converties en majuscules, sinon il n'y a pas d'autres restrictions.
 * Utilisé pour stocker des ensembles de codes CIM 10, CCAM, etc.
 * En interne un "Set" est aussi utilisé ce qui garantit que chaque code est unique
 * dans l'ensemble.
 * Deux méthodes statiques sont disponibles pour faciliter la lecture d'ensembles
 * de codes. 
 * @see #readCodeSetFromFile(File) Pour lire un ensemble de codes depuis un fichier
 * @see #readCodeSetsFromDir(File) Pour lire plusieurs ensembles de fichiers depuis
 *   un répertoire, on peut avoir ainsi par exemple un répertoire "ensemblesCim10", 
 *   un autre "ensemblesCcam", etc. et lire facilement les ensembles dans 
 *   un script.
 * @author hkaradimas
 */
public class CodeSet {
  /** nom du codeset */
  String name;
  /** Ensemble des codes */
  HashSet<String> uniqueCodes = new HashSet<>();

  /**
   * Lire une liste de codes à partir d'un fichier, chaque ligne représente un code,
   * les lignes vides et les lignes qui commencent par # sont ignorées.
   * Les espaces de début et fin sont enlevés.
   * Les lettres sont converties en majuscules.
   * @param f Le fichier à utiliser. Le nom du CodeSet est déduit de ce fichier, c'est le
   *   nom moins l'extension, converti en majuscules. 
   *   Par exemple pour C:\foo\bar.txt le nom sera "BAR".
   * @return
   * @throws IOException
   */
  static CodeSet readCodeSetFromFile(File f)
      throws IOException 
  {
    String name = f.getName();
    int ix = name.lastIndexOf('.');
    if (ix > 0) {
      name = name.substring(0, ix);
    }
    CodeSet cl = new CodeSet(name.toUpperCase());
    ArrayList<String> lines = new ArrayList<>();
    FileReader fr = new FileReader(f); 
    BufferedReader br = new BufferedReader(fr);
    String line = null;
    while ((line = br.readLine()) != null) {
      String tl = line.trim();
      if (tl.length() == 0 || tl.startsWith("#")) {
        //on ignore les lignes qui commencent par # et les lignes vides
      }
      else {
        lines.add(tl);
      }
    }
    br.close();
    fr.close();
    cl.setCodes(lines);
    return cl;
  }

  /**
   * Lit un ensemble de CodeSet depuis un répertoire de fichier.
   * Chaque fichier sera utilisé pour lire des CodeSets, mais par sécurité on
   * ne prend que les fichiers qui se terminent par ".txt".
   * @param dir
   * @return Une HashMap d'ensembles de codes, par nom.
   * @throws IOException 
   */
  static HashMap<String, CodeSet> readCodeSetsFromDir(File dir) 
      throws IOException 
  {
    FilenameFilter txtFnFilter = new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".txt");
      }
    };
    HashMap<String, CodeSet> codeSetsByName = new HashMap<>();
    String[] fileNames = dir.list(txtFnFilter);
    for (String fileName : fileNames) {
      CodeSet cs = CodeSet.readCodeSetFromFile(new File(dir, fileName));
      codeSetsByName.put(cs.getName(), cs);
    }
    return codeSetsByName;
  }
  
  /** Constructeur sans nom d'ensemble. Définir le nom par la suite */
  public CodeSet() {
  }

  /**
   * Constructeur avec le nom de l'ensemble
   * @param name nom de l'ensemble
   */
  public CodeSet(String name) {
    this.name = name;
  }

  /**
   * Mettre les codes dans l'ensemble
   * @param codes Une collection de codes
   */
  public void setCodes(Collection<String> codes) {
    uniqueCodes.clear();
    for (String code : codes) {
      uniqueCodes.add(code.trim().toUpperCase());
    }
  }
 
  /**
   * Récupérer les codes depuis l'ensemble
   * @return Un ensemble de codes
   */
  public Set<String> getCodes() {
    return uniqueCodes;
  }
  
  /**
   * L'ensemble contient-il le code ?
   * @param code Le code à rechercher
   * @return true si l'ensemble contient le code
   */
  public boolean containsCode(String code) {
    return uniqueCodes.contains(code);
  }

  /**
   * Nom de l'ensemble de codes. Sera utilisé comme partie de nom de fichier donc
   * ne pas utiliser de caractères spéciaux ni d'accents, le mieux est de rester
   * avec les caractères [a-z] [0-9] [-_]
   * @return Le nom du CodeSet
   */
  public String getName() {
    return name;
  }

  /**
   * Définir le nom de l'ensemble
   * @param name Le nouveau nom
   */
  public void setName(String name) {
    this.name = name;
  }
  
}

