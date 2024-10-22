/*:encoding=UTF-8:*/
package fr.gpmsi;

import java.io.IOException;
import java.util.ArrayList;

import fr.gpmsi.pmsixml.FieldParseException;
import fr.gpmsi.pmsixml.MissingMetafileException;
import groovy.lang.Closure;
import groovy.lang.Script;


//TODO ajouter des formes du type "rsa(boolean execNow, Closure<?> taskClosure)"
//     pour pouvoir déclarer des blocs sans les exécuter tout de suite
/**
 * Classe de base des scripts Groovy du projet. Basé sur {@link Script}, et rajoute les méthodes
 * qui sont proposées à l'auteur de script : rsa, rss, etc. qui font de ces appels un mini-DSL (domain
 * specific language).
 * Maintenant après avoir enregistré chaque Closure, elle est exécutée directement plutôt qu'être
 * juste stockée dans "steps". Ainsi le déroulement du script paraît plus naturel.
 * @see ScriptStep pour plus d'informations.
 * @author hkaradimas
 */
public abstract class GroovyScriptsBase 
extends Script 
{  
	ArrayList<ScriptStep> steps = new ArrayList<>();
	int callLevel = 0;
	
	/**
	 * Constructeur par défaut
	 */
	public GroovyScriptsBase() {
    }
	
  /**
   * Méthode hello qui peut être utilisée dans un script pour voir si {@link GroovyScriptsBase} est bien chargée
   */
  public void hello() {
	  System.out.println("Hello from GroovyScriptsBase");
  }
  
  /** 
   * Traitement d'un fichier RSA
   * @param taksClosure La closure à utiliser
   * @return un objet ScriptStep qui représente l'étape de script créée
   * @throws FieldParseException _
   * @throws IOException _
   * @throws MissingMetafileException _
   */
  public ScriptStep rsa(Closure<?> taksClosure) 
		  throws FieldParseException, IOException, MissingMetafileException 
  {
	  return task(taksClosure, ScriptStep.ST_RSA); 
  }
  
  /**
   * Traitement d'un fichier RSS
   * @param taksClosure La closure à utiliser
   * @return un objet ScriptStep qui représente l'étape de script créée
   * @throws FieldParseException _
   * @throws IOException _
   * @throws MissingMetafileException _
   */
  public ScriptStep rss(Closure<?> taksClosure) 
		  throws FieldParseException, IOException, MissingMetafileException 
  {
    return task(taksClosure, ScriptStep.ST_RSS); 
  }
  
  /**
   * Traitement d'un fichier RSF-ACE.
   * La variable "rsf" contient le RSF qui a été lu.
   * @param taksClosure _
   * @return un objet ScriptStep qui représente l'étape de script créée
   * @throws FieldParseException _
   * @throws IOException _
   * @throws MissingMetafileException _
   */
  public ScriptStep rsface(Closure<?> taksClosure) 
      throws FieldParseException, IOException, MissingMetafileException 
  {
    return task(taksClosure, ScriptStep.ST_RSFACE); 
  }

  /**
   * Traitement d'un fichier RHS
   * @param taksClosure _
   * @return un objet ScriptStep qui représente l'étape de script créée
   * @throws FieldParseException _
   * @throws IOException _
   * @throws MissingMetafileException _
   */
  public ScriptStep rhs(Closure<?> taksClosure) 
      throws FieldParseException, IOException, MissingMetafileException 
  {
    return task(taksClosure, ScriptStep.ST_RHS); 
  }

  /**
   * Traitement d'un fichier RHA
   * @param taksClosure _
   * @return un objet ScriptStep qui représente l'étape de script créée
   * @throws FieldParseException _
   * @throws IOException _
   * @throws MissingMetafileException _
   */
  public ScriptStep rha(Closure<?> taksClosure) 
      throws FieldParseException, IOException, MissingMetafileException 
  {
    throw new RuntimeException("rha non encore implemente");
    //task(taksClosure, ScriptStep.ST_RHA); 
  }

  /**
   * Traitement d'un fichier VIDHOSP.
   * @param taksClosure La closure
   * @return un objet ScriptStep qui représente l'étape de script créée
   * @throws FieldParseException _
   * @throws IOException _
   * @throws MissingMetafileException _
   */
  public ScriptStep vidhosp(Closure<?> taksClosure) 
      throws FieldParseException, IOException, MissingMetafileException 
  {
    return task(taksClosure, ScriptStep.ST_VIDHOSP); 
  }

  /**
   * Traitement d'un fichier csv (comma separated value)
   * @param taksClosure La closure à passer
   * @return un objet ScriptStep qui représente l'étape de script créée
   * @throws FieldParseException _
   * @throws IOException _
   * @throws MissingMetafileException _
   */
  public ScriptStep csv(Closure<?> taksClosure) 
		  throws FieldParseException, IOException, MissingMetafileException 
  {
    return task(taksClosure, ScriptStep.ST_CSV); 
  }
  
  /**
   * Traitement d'un fichier excel au travers de Apache POI (.xls ou .xlsx)
   * @param taksClosure La closure à passer
   * @return un objet ScriptStep qui représente l'étape de script créée
   * @throws FieldParseException _
   * @throws IOException _
   * @throws MissingMetafileException _
   */
  public ScriptStep xlpoi(Closure<?> taksClosure) 
          throws FieldParseException, IOException, MissingMetafileException 
  {
    return task(taksClosure, ScriptStep.ST_XLPOI); 
  }
  
  /**
   * Traitement d'un fichier dbf au travers de javadbf
   * @param taksClosure La closure à passer
   * @return un objet ScriptStep qui représente l'étape de script créée
   * @throws FieldParseException _
   * @throws IOException _
   * @throws MissingMetafileException _
   */
  public ScriptStep dbf(Closure<?> taksClosure) 
          throws FieldParseException, IOException, MissingMetafileException 
  {
    return task(taksClosure, ScriptStep.ST_DBF); 
  }
  
  /**
   * Etape avec pseudo-élément unique, pour faire exactement 1 exécution de "onItem".
   * Peu utilisé, car maintenant on peut faire tourner du code directement 
   * en script de façon synchrone avec les étapes.
   * @param taksClosure La closure de la tâche 
   * @return un objet ScriptStep qui représente l'étape de script créée
   * @throws FieldParseException Si erreur analyse
   * @throws IOException Si erreur E/S
   * @throws MissingMetafileException Si métadonnées non retrouvées
   */
  public ScriptStep single(Closure<?> taksClosure) 
		  throws FieldParseException, IOException, MissingMetafileException 
  {
    return task(taksClosure, ScriptStep.ST_SINGLE); 
  }
  
  /**
   * Traitement de fichiers mono-niveau, qui n'ont pas d'enfants
   * @param taksClosure La closure à utiliser
   * @return un objet ScriptStep qui représente l'étape de script créée
   * @throws FieldParseException Si erreur analyse
   * @throws IOException Si erreur E/S
   * @throws MissingMetafileException Si métadonnées non retrouvées
   */
  public ScriptStep mono(Closure<?> taksClosure) 
      throws FieldParseException, IOException, MissingMetafileException 
{
    return task(taksClosure, ScriptStep.ST_MONO); 
}

  /**
   * Traitement oriente lignes
   * @param taksClosure La closure à utiliser
   * @return un objet ScriptStep qui représente l'étape de script créée
   * @throws FieldParseException Si erreur analyse
   * @throws IOException Si erreur E/S
   * @throws MissingMetafileException Si métadonnées non retrouvées
   */
  public ScriptStep line(Closure<?> taksClosure) 
		  throws FieldParseException, IOException, MissingMetafileException 
  {
	  return task(taksClosure, ScriptStep.ST_LINE); 
  }

  /**
   * Créer directement une tâche à partir d'une closure. Utilisé par rsa,
   * rss, rsface, rhs, rha, vidhosp,csv,single,mono et line.
   * @param taskClosure La closure à utiliser
   * @param type Le numéro du type d'étape
   * @return un objet ScriptStep qui représente l'étape de script créée
   * @throws FieldParseException Si erreur analyse
   * @throws IOException Si erreur E/S
   * @throws MissingMetafileException Si métadonnées non retrouvées
   */
  public ScriptStep task(Closure<?> taskClosure, int type) 
      throws FieldParseException, IOException, MissingMetafileException 
  {
    boolean doExec = (callLevel == 0);
    callLevel++;
    return task(taskClosure, type, doExec); //will decrement callLevel
  }

  /**
   * Identique à {@link #task(Closure, int)} mais avec un paramètre pour
   * contrôler si on exécute ou non directement la séquence onInit(), onItem() et onEnd().
   * @param taskClosure La closure à utiliser
   * @param type Le numéro du type d'étape
   * @param doCalls Si true on exécute directement l'étape (onInit, onItem, onEnd) sinon on ne le fait pas.
   * @return un objet ScriptStep qui représente l'étape de script créée
   * @throws FieldParseException Si erreur analyse
   * @throws IOException Si erreur E/S
   * @throws MissingMetafileException Si métadonnées non retrouvées
   */
  public ScriptStep task(Closure<?> taskClosure, int type, boolean doCalls) 
		  throws FieldParseException, IOException, MissingMetafileException 
  {
	  ScriptStep ss = new ScriptStep(this);
	  ss.setStepType(type);
	  Closure<?> cl = taskClosure.rehydrate(ss, this, this);
	  cl.setDirective(Closure.DELEGATE_ONLY);
	  cl.call();
	  steps.add(ss);
	  getBinding().setVariable("steps", steps);
	  if (doCalls) {
    	  ss.callOnInit();
    	  ss.processItems();
    	  ss.callOnEnd();
	  }
	  if (callLevel > 0) callLevel--;
	  return ss;
  }
  
  /**
   * Vérifie qu'un objet n'est pas null, sinon jette une {@link NullPointerException} avec le message donné.
   * @param obj l'objet à vérifier
   * @param message le message à afficher avec l'exception
   */
  public void verifyNotNull(Object obj, String message)
  {
    if (obj == null) throw new NullPointerException(message);
  }
  
}
