package fr.gpmsi;

/**
 * Commande simple pour voir l'environnement courant.
 * Utilisé pour vérifier que les classes sont chargées correctement, et ecrit
 * les informations de l'environnement courant.
 * @author hkaradimas
 *
 */
public class ShowEnv {

  /** Print system property */
  private static final void psp(String name) {
    System.out.println(name + " : " + System.getProperty(name));
  }

  /** default constructor */
  ShowEnv() {}
  
  /**
   * Méthode principale
   * @param args Arguments
   * @throws Exception -
   */
  public static void main(String[] args)
  throws Exception
  {
    //special printing of class path for more readabilit
    String cp = System.getProperty("java.class.path");
    cp = cp.replace(System.getProperty("path.separator"), "\n\t");
    System.out.println("java.class.path : \n\t" + cp);
    //regular printing of other system properties
    psp("java.home");
    psp("java.version");
    psp("java.vendor");
    psp("java.vendor.url");
    psp("os.arch");
    psp("os.name");
    psp("os.version");
    psp("user.name");
    psp("user.home");
    psp("user.dir");

    //String version = FileUtils.readUtf8Resource("/version.txt");
    //System.out.println("version gpmsi : " + version);
  }

}
