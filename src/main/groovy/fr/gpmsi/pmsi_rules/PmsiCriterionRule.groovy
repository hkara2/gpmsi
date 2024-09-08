package fr.gpmsi.pmsi_rules;

import java.util.HashMap;

/**
 * Règle PMSI qui est juste construite sur un critère
 * @author hkaradimas
 */
public class PmsiCriterionRule
  implements PmsiRule 
{

  PmsiCriterion criterion
  
  public PmsiCriterionRule(PmsiCriterion criterion) {
    this.criterion = criterion
  }

  @Override
  public boolean eval(HashMap context) {
    return criterion.eval(context);
  }

  @Override
  public void init(HashMap context) { }

  @Override
  public void action(HashMap context) { }

}
