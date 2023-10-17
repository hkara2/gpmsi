package fr.karadimas.gpmsi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;

/**
 * Fonctions utilitaires sur les fichiers.
 * @author hkaradimas
 *
 */
public class FileUtils {

  /**
   * Constructeur par défaut
   */
  public FileUtils() {
  }

  /**
   * enlever le suffixe du nom de fichier. Par exemple, toto.txt devient toto.
   * N'enlève que le dernier suffixe ; ainsi "toto.bar.baz.csv" devient "toto.bar.baz".
   * @param name Le nom à transformer
   * @return Le nom sans le suffixe
   */
  public static String stripSuffix(String name) {
    int ix = name.lastIndexOf('.');
    if (ix < 0) return name; //pas de suffixe, retourner tel quel
    return name.substring(0, ix); //retourner le nom sans le suffixe
  }
  
  /**
   * Lire un fichier donné à partir de son nom.
   * Appelle {@link #readFile(File)}
   * @param path Le chemin du fichier
   * @return Le contenu textuel du fichier, avec l'encodage par défaut du système
   * @throws IOException si erreur E/S
   */
  public static String readFile(String path) throws IOException {
    return readFile(new File(path));
  }
  
  /**
   * Lire un fichier File fourni en une seule fois (-&gt; ne convient pas aux 
   * très gros fichiers sauf si vous avez beaucoup de mémoire).
   * @see Files#copy(java.nio.file.Path, java.io.OutputStream)
   * @see Files#readAllLines(java.nio.file.Path)
   * @param f L'objet File qui représente le fichier
   * @return Le contenu textuel du fichier, avec l'encodage par défaut du système
   * @throws IOException Si erreur E/S
   */
  public static String readFile(File f) throws IOException {
    FileReader fr = new FileReader(f);
    try {
      char[] buf = new char[(int) f.length()];
      fr.read(buf);
      return new String(buf);
    }
    finally {
      fr.close();
    }
  }
  
  /**
   * Lire tout le contenu d'un Reader dans une String, en
   * passant par un tampon intermédiaire de 4096 caractères
   * 
   * @param rdr Le Reader à utiliser
   * @return Le contenu du Reader
   * @throws IOException Si erreur E/S
   */
  public static String toString(Reader rdr)
      throws IOException 
  {
    char buf[] = new char[4096];
    StringBuilder sb = new StringBuilder();
    int n = 0;
    while ((n = rdr.read(buf)) != -1) sb.append(buf, 0, n);
    return sb.toString();
  }
  
  /**
   * Lire une Resource en UTF-8
   * @param resourcePath Le chemin de la resource à lire
   * @return Le texte de la resource 
   * @throws IOException Si erreur lors de la lectuer de la resource
   */
  public static String readUtf8Resource(String resourcePath)
      throws IOException 
  {
    try (
      InputStream is = FileUtils.class.getResourceAsStream(resourcePath);
    ) {
      if (is == null) throw new FileNotFoundException("Resource '"+resourcePath+"' not found");
      return toString(new InputStreamReader(is, "UTF-8"));
    }
  }
  
  /**
   * Rechercher la première ligne vide, et ramener son numéro (débute à 0)
   * @param f Le fichier dans lequel il faut chercher la ligne vide
   * @param trim Faut il appeler trim() avant de voir si la ligne est vide (ce qui revient à considérer comme vide une ligne avec des espaces)
   * @return Le numéro de la ligne vide, ou -1 si pas de ligne vide trouvée
   * @throws IOException Si erreur d'E/S
   */
  public static int findFirstBlankLine(File f, boolean trim)
      throws IOException 
  {
    int linePos = -1;
    int curLineNr = 0;
    FileReader fr = new FileReader(f);
    BufferedReader br = new BufferedReader(fr);
    String line = "?";
    while (line != null) {
      line = br.readLine();
      if (line == null) break;
      if (trim) line = line.trim();
      if (line.length() == 0) {
        linePos = curLineNr; //on a trouvé la première ligne vide
        break; //on peut sortir
      }
      curLineNr++;
    }
    br.close();
    fr.close();
    return linePos;
  }
  
}
