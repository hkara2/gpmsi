package fr.gpmsi;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

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
   * Pour un nom de fichier, séparer le nom simple (ce qui est avant le dernier point) et l'extension (ce qui est après le dernier point).
   * Exemple :
   * <pre>
   * String[] parties = FileUtils.splitNameAndExtension("bonjour.xlsx");
   * </pre>
   * L'élément parties[0] contiendra "bonjour"<br>
   * L'élément parties[1] contiendra ".xlsx"
   * 
   * @param filename Le nom de fichier
   * @return Un tableau de deux éléments. L'élément 0 est le nom simple, et l'élément 1 est soit une chaîne vide s'il n'y avait pas
   *     de point dans le nom de fichier, soit le point + l'extension si un point était présent.
   */
  public static String[] splitNameAndExtension(String filename) {
    if (filename == null) return null;
    String[] result = new String[2];
    int dotIndex = filename.lastIndexOf('.');
    if (dotIndex == -1) {
      result[0] = filename;
      result[1] = "";
    }
    else {
      result[0] = filename.substring(0, dotIndex); //ce qui est avant le point
      result[1] = filename.substring(dotIndex); //le point + ce qui est après le point
    }
    return result;
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
  
  /**
   * Retrouver dans un fichier zip le premier fichier qui répond au pattern.<br>
   * Cette méthode rend la recherche dans les fichier IN et OUT (de l'ATIH) plus facile.<br>
   * Exemple de recherche d'un fichier <code>.tra.txt</code> :
   * <pre>
   * File zipOut = new File("C:/Local/GROUPAGE/2024/12/250127/INOUT/910019447.2024.0.20250129121840.out.zip");
   * Path tra = FileUtils.findFirstInZip(zipOut, "*.tra.txt");
   * if (tra != null) {
   *    Reader rdr = Files.newBufferedReader(tra);
   *    String content = FileUtils.toString(rdr);
   *    //...utilisation de "content"
   *  }
   * </pre>
   * 
   * @param zipFile Le fichier zip
   * @param pattern Le pattern à rechercher, de type "glob" ( cf. {@link FileSystem#getPathMatcher(String)} )
   * @return L'objet {@link Path} qui a été trouvé, ou null si rien n'a été trouvé
   * @throws IOException Si erreur E/S
   */
  public static Path findFirstInZip(File zipFile, String pattern)
      throws IOException 
  {
    URI zipFileUri = zipFile.toURI();
    URI zipUri = URI.create("jar:"+zipFileUri); //doit etre 'jar:', pas 'zip:'
    FileSystem zfs = FileSystems.newFileSystem(zipUri, Collections.emptyMap());

    //Ensuite, utilisation de ce nouveau FileSystem :

    Iterable<Path> rootDirs = zfs.getRootDirectories();
    Path rd = null;
    for (Path d:rootDirs) rd = d; //recuperation du "root dir" (il n'y en a qu'un)
    //System.out.println("Root dir : "+rd);
    PathMatcher pm = zfs.getPathMatcher("glob:/"+pattern); //matcher qui recherchera le pattern "glob" donné
    //Stream<Path> sp = Files.walk(rd);
    //Iterator<Path> ip = sp.iterator();
    //while (ip.hasNext()) System.out.println("path:"+ip.next());
    Optional<Path> foundPath = Files.walk(rd).filter(p -> pm.matches(p)).findFirst();
    return foundPath.orElse(null);
  }
  
}
