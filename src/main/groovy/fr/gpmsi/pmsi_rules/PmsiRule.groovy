package fr.gpmsi.pmsi_rules

/**
 * Interface générale pour une "règle PMSI" minimaliste.
 * La règle est initialisée lorsqu'elle est créée.
 * Elle est ensuite appelée pour une évaluation.
 * si l'evaluation est positive ("evaluate" a retourné "true"), la méthode
 * "action" est appelée.
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
  void init(HashMap context);
  boolean eval(HashMap context);
  void action(HashMap context);
}