package fr.gpmsi.tests;

/**
 * Classe de script qui ne fait rien d'autre que renvoyer "true", utilisée pour tests.
 * @author hkaradimas
 *
 */
public class DummyScriptClass extends Script {

  public DummyScriptClass() {
  }

  @Override
  public Object run() {
    return true;
  }

}
