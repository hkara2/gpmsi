package fr.karadimas.gpmsi.pmsi_rules;

import java.util.HashMap;

public class GenericPmsiRule
 implements PmsiRule 
{
  Closure<HashMap> initClosure
  Closure<HashMap> evalClosure
  Closure<HashMap> actionClosure
  
  public GenericPmsiRule() {}
  
  public GenericPmsiRule(Closure<HashMap> initClosure, Closure<HashMap> evalClosure, Closure<HashMap> actionClosure) {
    this.initClosure = initClosure
    this.evalClosure = evalClosure
    this.actionClosure = actionClosure
  }

  @Override
  public void init(HashMap context) {
    if (initClosure != null) initClosure.call(context)
  }

  @Override
  public boolean eval(HashMap context) {
    if (evalClosure == null) return false
    return evalClosure.call(context)
  }

  @Override
  public void action(HashMap context) {
    if (actionClosure != null) actionClosure.call(context)
  }

}
