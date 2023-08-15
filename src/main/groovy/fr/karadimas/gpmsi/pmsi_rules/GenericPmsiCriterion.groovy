package fr.karadimas.gpmsi.pmsi_rules;

import java.util.HashMap;

import groovy.lang.Closure;

public class GenericPmsiCriterion
    implements PmsiCriterion 
{
  Closure closureToEval;

  public GenericPmsiCriterion(Closure closureToEval) {
    this.closureToEval = closureToEval;
  }

  @Override
  public boolean eval(HashMap context) {
    return closureToEval.call(context);
  }

}
