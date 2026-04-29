package fr.gpmsi.pmsi_rules;

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
  public void init(Map context) {
    if (initClosure != null) initClosure.call(context)
  }

  @Override
  public boolean eval(Map context, Map outputContext) {
    if (evalClosure == null) return false
    return evalClosure.call(context, outputContext)
  }

  public boolean eval(Map context) { eval(context, null) }

  @Override
  public void action(Map context, Map outputContext) {
    if (actionClosure != null) actionClosure.call(context, outputContext)
  }

  public void action(Map context) { action(context, null) }

}
