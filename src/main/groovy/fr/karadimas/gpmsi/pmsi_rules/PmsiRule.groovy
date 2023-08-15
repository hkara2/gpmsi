package fr.karadimas.gpmsi.pmsi_rules

/**
 * Interface generale pour une "regle PMSI" extremement simpliste.
 * La regle est initialisee lorsqu'elle est creee.
 * Elle est ensuite appelee pour une evaluation.
 * si l'evaluation est positive ("evaluate" a retourne "true"), la methode
 * "action" est appelee.
 * Les noms utilisables dans "context" sont :
 * engine : le moteur de regles
 * rum : le rum lorsque c'est un rum qui est lu
 * out : un PrintWriter qui peut etre utilise pour emettre du texte (par defaut emet vers StdOut)
 * collect : une collection pour ramasser des �l�ments (num�ros de dossier, etc.)
 */
interface PmsiRule {
  void init(HashMap context);
  boolean eval(HashMap context);
  void action(HashMap context);
}