package fr.gpmsi.pmsi_rules

/**
 * Interface générale pour une "règle PMSI" minimaliste.
 * La règle est initialisée lorsqu'elle est créée.
 * Elle est ensuite appelée pour une évaluation.
 * si l'evaluation est positive ("evaluate" a retourné "true"), la méthode
 * "action" est appelée.
 * Il y a un contexte en entrée, qui permet de passer des valeurs à la règle,
 * et un contexte en sortie, qui permet à la règle de passer des valeurs en
 * sortie au moteur.
 * <p>
 * Les noms utilisables dans "context" sont, à titre d'exemple :
 * <ul>
 * <li><b>engine</b> : le moteur de regles
 * <li><b>rum</b> : le rum lorsque c'est un rum qui est lu
 * <li><b>out</b> : un PrintWriter qui peut etre utilise pour émettre du texte (par defaut émet vers StdOut)
 * <li><b>collect</b> : une collection pour ramasser des éléments (numéros de dossier, etc.)
 * </ul>
 */
interface PmsiRule {
  void init(Map context);
  boolean eval(Map context, Map outputContext);
  void action(Map context, Map outputContext);
}