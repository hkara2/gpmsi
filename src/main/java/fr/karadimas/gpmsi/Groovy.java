/*:encoding=UTF-8:*/
package fr.karadimas.gpmsi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilerConfiguration;

import fr.karadimas.pmsixml.Log4j2Utils;
import groovy.lang.Binding;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Lanceur de scripts. C'est la classe centrale, qui permet
 * d'exécuter des traitements en utilisant un script Groovy fourni par
 * l'utilisateur.
 * On utilise pour cela l'option -script &lt;chemin_du_script&gt;
 * <p>
 * Les arguments du script peuvent être passés dynamiquement. On peut passer un 
 * drapeau (<code>-f:&lt;nom&gt;</code>) ou un argument avec une valeur (<code>-a:&lt;nom&gt; &lt;valeur&gt;</code>).
 * Les préfixes spéciaux qui commencent par <code>"?"</code> servent à demander via une petite
 * fenêtre surgissante une réponse à l'utilisateur.
 * <ul>
 * <li><code>?s</code> : demander une String
 * <li><code>?d</code> : demander une date (au format jj/mm/aaaa)
 * <li><code>?f</code> : demander un fichier
 * <li><code>?g</code> : demander un répertoire
 * <li><code>?p</code> : demander un mot de passe
 * </ul>
 * <p>
 * Ex : passage de la valeur "simpson" pour l'argument "monnom" :
 * <code>-a:monnom simpson</code>.
 * <br>
 * Ex : demander à l'utilisateur d'entrer un nom :
 * <code>-a:monnom ?</code>
 * <br>
 * Ex : demander à l'utilisateur d'entrer un nom, avec un texte d'invite :
 * <code>-a:monnom "?sVeuillez entrer un nom de personnage"</code>
 * <br>
 * Ex : demander à l'utilisateur de choisir un fichier :
 * <code>-a:monfic "?fVeuillez choisir un fichier"</code>
 * <br>
 * Ex : demander à l'utilisateur de choisir un répertoire :
 * <code>-a:monrep "?gVeuillez choisir un répertoire"</code>
 * <br>
 * Ex : demander à l'utilisateur de choisir une date :
 * <code>-a:madate "?dVeuillez choisir une date"</code>
 * <br>
 * Ex : demander à l'utilisateur d'entrer un mot de passe :
 * <code>-a:monmot "?pVeuillez entrer un mot de passe pour truc"</code>
 * <br>
 * Ex : Paramètre commence vraiment par ?, deviendra directement "?interro" sans rien demander à l'utilisateur :
 * <code>-a:moninterrogation "??interro"</code>
 * <p>
 * Dans le code groovy, on pourra récupérer la valeur via le tableau "args".<br>
 * Ex : <code>def monnom = args['monnom']</code>
 * <p>
 * Il y a quelques objets prédéfinis dans la liaison ('binding') avec l'environnement de script :
 * 
 * <ul>
 * <li><code>args</code> : les arguments du script 
 * <li><code>flags</code> : les drapeaux
 * <li><code>lg</code> : le log
 * <li><code>scriptPath</code> : le chemin du script
 * <li><code>nl</code> : le séparateur de ligne (System.getProperty("line.separator"))
 * <li><code>userHome</code> : le répertoire de l'utilisateur (System.getProperty("user.home"))
 * <li><code>frenchDateFormat</code> : un SimpleDateFormat au format francais dd/MM/yyyy
 * <li><code>pmsiDateFormat</code> : un SimpleDateFormat au format francais mais sans séparateurs, pour les fichiers PMSI ddMMyyyy
 * </ul>
 * 
 * Autres options permises :
 * 
 * <ul>
 * <li>debug : passer en mode debogage (emet des messages de debogage)
 * <li>enc &lt;encodage&gt; : change l'encodage par defaut des script (par defaut c'est UTF-8)
 * <li>scripturi : a utiliser à la place de l'option "script" si on veut définir un chemin relatif sous forme d'URI pour rechercher un script.
 *                 C'est nécessaire notamment si le script que l'on donne en argument fait partie d'un package.
 *                 Par exemple si mon script se trouve dans <code>C:\mes_scripts\valorisation\calcul2.groovy</code> et qu'en
 *                 fait "valorisation" est un package, il faut appeler le script de la façon suivante :
 *                 <code>-extracp file:/C:/mes_scripts/ -scripturi valorisation/calcul2.groovy</code>
 * </ul>
 * 
 * <p>
 * Il est également possible d'utiliser un objet interne déjà compilé à l'aide de l'option <code>-run</code>
 * (au lieu de l'option <code>-script</code>). Il faut alors donner le nom de la classe et pas un chemin.
 * <br>
 * Ex : <code>-run fr.karadimas.groovytests.LibsTest</code>
 * <br>
 * La classe doit étendre {@link Script}
 * 
 * <p>
 * 
 * 
 * 
 * @author hkaradimas
 *
 */
public class Groovy {
    static Logger lg = LogManager.getLogger(Groovy.class); 

    String scriptPath; //chemin de fichier pour le script
    String scriptUri; //uri pour le script si on définit un chemin relatif 
    String runClass; //si on veut executer une classe directement, sans passer par le script engine ; scriptPath sera à "" dans ce cas.
    Object returnedObject; //l'objet retourné par le script
    HashMap<String, String> scriptArgs = new HashMap<>();
    HashSet<String> scriptFlags = new HashSet<>();
    ArrayList<String> extraCps = new ArrayList<>();
    
    String encoding = "UTF-8"; //encodage des scripts, par défaut UTF-8

    public String mandatory(Args args, String errorMessage)
    throws Exception
    {
        if (!args.hasNext()) {
            throw new Exception(errorMessage);
        }
        return args.next();
    }

    public void init(String[] argsp) 
            throws Exception 
    {
        boolean helpRequested = false;
        Args args = new Args(argsp);
        while (args.hasNext()) {
            String arg = args.next();
            if (arg.equals("-script")) {
                scriptPath = mandatory(args, "Argument manquant pour -script");
            }
            else if (arg.equals("-scripturi")) {
              scriptUri = mandatory(args, "Argument manquant pour -scripturi");
            }
            else if (arg.equals("-run")) {
              runClass = mandatory(args, "Argument manquant pour -run");
            }
            else if (arg.equals("-debug")) {
              //LogManager.getRootLogger().setLevel(Level.DEBUG);
              //equivalent en log4j2
              Log4j2Utils.changeRootLogLevel(Level.DEBUG);
            }
            else if (arg.startsWith("-f:")) {
                String flagName = arg.substring(3);
                scriptFlags.add(flagName);
            }
            else if (arg.startsWith("-a:")) {
                String argName = arg.substring(3);
                //arg value is the value that follows
                if (!args.hasNext()) {
                    throw new Exception("Argument manquant pour '"+arg+"'");
                }
                arg = args.next();
                if (arg.startsWith("?")) {
                    //arg value is interactive. Ex : "?sEnter name" or "?fChoose file"
                    char typ = 's';
                    String prompt = "Entrez une valeur pour l'argument '"+argName+"'";
                    if (arg.length() > 1) {
                        typ = arg.charAt(1);
                        //S ystem.out.println("Typ : '"+typ+"'");
                        prompt = arg.substring(2);
                    }
                    switch (typ) {
                    case 'f': //file
                        JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
                        fc.setDialogTitle(prompt);
                        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        fc.showOpenDialog(null);
                        File selFile = fc.getSelectedFile();
                        if (selFile == null) arg = null;
                        else arg = selFile.getCanonicalPath();
                        break;
                    case 'g': //dir
                        fc = new JFileChooser(new File(System.getProperty("user.dir")));
                        fc.setDialogTitle(prompt);
                        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        fc.showOpenDialog(null);
                        selFile = fc.getSelectedFile();
                        if (selFile == null) arg = null;
                        else arg = selFile.getCanonicalPath();
                        break;
                    case 'd' : //date with format dd/MM/yyyy
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false);
                        boolean ok = false;
                        while (!ok) {
                            String txtd = JOptionPane.showInputDialog(prompt);
                            if (txtd == null) {    arg = null; ok = true; }
                            else {
                                try { sdf.parse(txtd); arg = txtd; ok = true; }
                                catch (ParseException ex) {
                                    JOptionPane.showMessageDialog(null, "date invalide '"+txtd+"'");
                                }
                            }
                        }//while
                        break;
                    case 'p' : //mot de passe, masqué
                        arg = ""; //par defaut vide
                        JPanel pwPanel = new JPanel();
                        JLabel pwLabel = new JLabel("Mot de passe :");
                        JPasswordField pwPf = new JPasswordField(20);
                        pwPanel.add(pwLabel); pwPanel.add(pwPf);
                        pwPf.addAncestorListener(new RequestFocusListener()); //astuce extrêmement rusée pour avoir le focus
                        int option = JOptionPane.showConfirmDialog(null, pwPanel, prompt, 
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                        if (option == JOptionPane.OK_OPTION) arg = new String(pwPf.getPassword());
                        break;
                    case '?' : //?, but escaped
                        arg = "?" + prompt;
                        break;
                    default: //string
                        arg = JOptionPane.showInputDialog(prompt);
                    }                      
                }// if (arg.startsWith("?"))
                lg.debug("Setting script arg '"+argName+"' to '"+arg+"'");
                scriptArgs.put(argName, arg);
            }
            else if (arg.equals("-enc")) {
              //verifier qu'il y a bien un argument qui suit
              if (!args.hasNext()) {
                  throw new Exception("Argument manquant pour -enc");
              }
              arg = args.next();
              //Charset dummy = Charset.forName(arg);
              lg.debug("Verification de l'encodage '"+arg+"' : chargement correct.");
              encoding = arg;
            }
            else if (arg.equals("-help") || arg.equals("-h") || arg.equals("--help")) {
                System.out.println("Utilisation : Groovy -script <chemin_du_script> [ options ] [ arguments ] [ drapeaux ]");
                System.out.println("Options :");
                System.out.println("  -help : montre de l'aide (fonctionne aussi avec -h et --help)");
                System.out.println("  -debug : activer le mode debogage (ecrit des messages detailles)");
                System.out.println("  -extracp <URL> : ajoute une URL supplementaire au classpath Groovy");
                System.out.println("  -enc <NOM> : change l'encodage utilise (cf. doc detaillee)");
                System.out.println("Arguments :");
                System.out.println("  Les arguments commencent par -a: puis le nom de l'argument");
                System.out.println("  suivi d'un espace et de la valeur de l'argument");
                System.out.println("  Exemple : -a:finess 910019447");
                System.out.println("Drapeaux :");
                System.out.println("  Les drapeaux (flags) commencent par -f: puis le nom du drapeau.");
                System.out.println("  Exemple : -f:modedetaille");
                System.out.println("");
                helpRequested = true;
            }
            else if (arg.equals("-extracp")) {
              //declare an extra classpath
              //Note that the extra path must be in URL form and end with a slash, e.g.
              //file:/C:/hkchse/dev/pmsixml/groovy-samples/
              if (!args.hasNext()) {
                throw new Exception("Argument manquant pour '"+arg+"'");
              }
              arg = args.next();
              extraCps.add(arg);
            }
            else {
                throw new Exception("Argument illegal : '"+arg+"'");
            }
        }//while
        if (scriptPath == null && scriptUri == null && runClass == null && !helpRequested) {
            System.err.println("Pas de -script donne, et pas de -run donne non plus, rien ne sera execute ! Utilisez -h pour avoir de l'aide.");
        }
        if (runClass != null) scriptPath = ""; //runClass a priorité sur scriptPath
    }
    
    public int run() 
        throws ResourceException, ScriptException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        if (scriptPath == null && scriptUri == null && runClass == null) return 99; //error -99 pas de script specifie
        try {
            URL parentURL = null;
            File scriptFile = null;
            if (scriptPath != null) {
              scriptFile = new File(new File(scriptPath).getCanonicalPath());            
              File parent = scriptFile.getParentFile();
              parentURL = parent.toURI().toURL();
            }
            //S ystem.out.println("Resources :");
            //getClass().getClassLoader().getResource("/"); //this should be the root of class loader, but it only works inside Eclipse !
            //Enumeration<URL> resources = Groovy.class.getClassLoader().getResources("fr/karadimas/pmsixml/inits.groovy"); //this is the root of class loader !
            //while (resources.hasMoreElements()) {
            //    URL u = resources.nextElement();
            //    System.out.println("Resource : '"+u+"'");
            //}
            //This is the most reliable way : look for a known resource to infer the root URL
            String rootURLStr = Groovy.class.getResource("/root.txt").toString();
            URL rootURL = new URL( rootURLStr.substring(0, rootURLStr.length()-8) );
            CompilerConfiguration cc = new CompilerConfiguration();
            cc.setSourceEncoding(encoding); //forcage de l'encodage (par defaut UTF-8 mais peut etre change par le parametre -enc)
            cc.setScriptBaseClass("fr.karadimas.gpmsi.GroovyScriptsBase");
            
            ArrayList<URL> roots = new ArrayList<>();
            for (String extracp : extraCps) {
              lg.debug("Ajout du extra cp '"+extracp+"'");
              roots.add(new URL(extracp));
            }
            lg.debug("Ajout du rootURL '"+rootURL+"'");
            roots.add(rootURL);
            if (parentURL != null) {
              lg.debug("Ajout du parentURL '"+parentURL+"'");
              roots.add(parentURL);
            }
            //ajout du repertoire ~/.gpmsi/scripts/ dans le chemin de recherche des classes
            String userHome = System.getProperty("user.home");
            File uhScriptsDir = new File(userHome);
            uhScriptsDir = new File(uhScriptsDir, ".gpmsi");
            uhScriptsDir = new File(uhScriptsDir, "scripts");
            roots.add(uhScriptsDir.toURI().toURL());
            lg.debug("Liste des racines de classpath :");
            for (URL root : roots) {
              lg.debug("root:"+root);
            }
            GroovyScriptEngine gse = new GroovyScriptEngine(roots.toArray(new URL[0]));
            gse.setConfig(cc);
            Binding bnd = new Binding();
            //ajout de quelques variables prédéfinies pour plus de confort
            bnd.setVariable("args", scriptArgs);
            bnd.setVariable("flags", scriptFlags);
            bnd.setVariable("lg", lg);
            bnd.setVariable("scriptPath", scriptPath);
            bnd.setVariable("scriptUri", scriptUri);
            bnd.setVariable("nl", System.getProperty("line.separator"));
            bnd.setVariable("userHome", System.getProperty("user.home"));
            SimpleDateFormat frenchDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            bnd.setVariable("frenchDateFormat", frenchDateFormat);
            SimpleDateFormat pmsiDateFormat = new SimpleDateFormat("ddMMyyyy");
            bnd.setVariable("pmsiDateFormat", pmsiDateFormat);
            SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            bnd.setVariable("isoDateFormat", isoDateFormat);
            //problem : The script fr/karadimas/gpmsi/inits.groovy can't be found.
            //This works when hardcoded : 'jar:file:/C:/app/pmsixml/1.6/lib/pmsixml-1.6.jar!/fr/karadimas/pmsixml/inits.groovy'
            gse.run("fr/karadimas/gpmsi/initengine.gtxt", bnd); //start with init script (inits.groovy copied to initengine.gtxt)
            if (scriptFile != null) lg.debug("running script file '"+scriptFile+"'");
            if (scriptUri != null) lg.debug("running script uri : '"+scriptUri+"'");
            if (runClass != null) {
              //execution directe d'une classe déjà compilée en tant que script (interne) distribuée dans le jar
              @SuppressWarnings("unchecked")
              Class<Script> scriptClass = (Class<Script>) Class.forName(runClass);
              Script scriptObj = scriptClass.newInstance(); 
              scriptObj.setBinding(bnd);
              returnedObject = scriptObj.run();
            }
            else {
              if (scriptUri == null) returnedObject = gse.run(scriptFile.getName(), bnd); //appel avec un chemin normal de fichier 
              else returnedObject = gse.run(scriptUri, bnd); //un classpath différent a été donné, utiliser le scriptUri car il doit donner un chemin relatif pour trouver le script
            }
            //dans les versions précédentes on renvoyait la valeur entière si le script retournait une valeur numérique
            //Ce n'est pas très clair, et maintenant on renvoie toujours 0.
            //S'il y a eu une erreur, on considère que le script va déclencher une exception.
            //L'objet qui a été retourné par le script peut être consulté via getReturnedObject().
            return 0; //sinon on considère que tout s'est bien passé et on envoie 0. L'objet lui-même peut être accédé via getReturnedObject().
        } catch (ResourceException e) {
          lg.error("Erreur d'execution", e); throw e;
        } catch (ScriptException e) {
          lg.error("Erreur d'execution", e); throw e;
        } catch (MalformedURLException e) {
          lg.error("Erreur d'execution", e); throw e;
        } catch (IOException e) {
          lg.error("Erreur d'execution", e); throw e;
        } catch (ClassNotFoundException e) {
          lg.error("Erreur d'execution", e); throw e;
        } catch (InstantiationException e) {
          lg.error("Erreur d'execution", e); throw e;
        } catch (IllegalAccessException e) {
          lg.error("Erreur d'execution", e); throw e;
        }        
    }
    
    public static void main(String[] argsp) 
            throws Exception 
    {
        Groovy app = new Groovy();
        app.init(argsp);
        int exitCode = app.run();
        if (exitCode != 0) System.exit(exitCode);
    }
    
    /**
     *  ( Repris depuis http://www.camick.com/java/source/RequestFocusListener.java )
     *  
     *  Convenience class to request focus on a component.
     *
     *  When the component is added to a realized Window then component will
     *  request focus immediately, since the ancestorAdded event is fired
     *  immediately.
     *
     *  When the component is added to a non realized Window, then the focus
     *  request will be made once the window is realized, since the
     *  ancestorAdded event will not be fired until then.
     *
     *  Using the default constructor will cause the listener to be removed
     *  from the component once the AncestorEvent is generated. A second constructor
     *  allows you to specify a boolean value of false to prevent the
     *  AncestorListener from being removed when the event is generated. This will
     *  allow you to reuse the listener each time the event is generated.
     */
    public class RequestFocusListener implements AncestorListener
    {
        private boolean removeListener;

        /*
         *  Convenience constructor. The listener is only used once and then it is
         *  removed from the component.
         */
        public RequestFocusListener() { this(true); }

        /*
         *  Constructor that controls whether this listen can be used once or
         *  multiple times.
         *
         *  @param removeListener when true this listener is only invoked once
         *                        otherwise it can be invoked multiple times.
         */
        public RequestFocusListener(boolean removeListener) {
            this.removeListener = removeListener;
        }

        @Override
        public void ancestorAdded(AncestorEvent e) {
            JComponent component = e.getComponent();
            component.requestFocusInWindow();

            if (removeListener) component.removeAncestorListener( this );
        }

        @Override
        public void ancestorMoved(AncestorEvent e) {}

        @Override
        public void ancestorRemoved(AncestorEvent e) {}
    }

    public Object getReturnedObject() {
      return returnedObject;
    }

    public void setReturnedObject(Object returnedObject) {
      this.returnedObject = returnedObject;
    }
    
}
