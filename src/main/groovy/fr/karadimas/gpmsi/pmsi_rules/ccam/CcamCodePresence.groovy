/**:encoding=UTF-8:
 * Attention encore en cours de mise au point !
 * Il y a certainement des bugs, et l'API va encore beaucoup changer !
 *
 */
package fr.karadimas.gpmsi.pmsi_rules.ccam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import fr.karadimas.gpmsi.pmsi_rules.AoeNode
import fr.karadimas.gpmsi.pmsi_rules.PmsiCriterion;
import fr.karadimas.pmsixml.FszGroup;

/**
 * Selection selon la présence de critères sur les codes CCAM.
 * Pour l'instant on fait une recherche simple sur une liste, qui contient des codes CCAM et/ou
 * des expressions régulieres.
 * L'extension PMSI est un problème ; dans certains outils (DxCare ...) l'acte lorsqu'il
 * n'a pas d'extension PMSI est envoyé avec une extension PMSI '-00'.
 * D'autres outils (Sillage, CDP, ...) ne mettent rien lorsqu'il n'y a pas d'extension PMSI.
 * Il semble que depuis l'introduction de la CCAM descriptive, il y ait eu -00 pour les
 * actes sans extension, puis rien du tout.
 * Pour cettre raison un test est fait en interne et lorsqu'un code avec -00 est rencontré
 * dans le RUM, le -00 est enlevé.
 * Pour le RSA il n'y a pas de problème, CCAM à 7 caractères et extension PMSI sur 2 caractères
 * (donc sans le '-') sont deux champs séparés.
 *
 * Donc pour les expressions régulières.
 *
 * Il y a une syntaxe simplifiée (non encore implémentée) :<br>
 * <ul>
 * <li><code>/a:&lt;expression activité&gt;</code>
 * <li><code>/m:&lt;expression modificateurs&gt;</code>
 * <li><code>/p:&lt;expression phase&gt;</code></code>
 * <li><code>/d:&lt;expression extension documentaire&gt;</code>
 * <li><code>/x:&lt;expression association non prévue&gt;</code>
 * <li><code>/r:&lt;expression remboursement exceptionnel&gt;</code>
 * <li><code>/n:&lt;expression nombre de réalisations de l'acte&gt;</code>
 * </ul>
 * <br>
 * L'expression est :
 * <expression valeur>[, <expression valeur>]
 * L'expression valeur est :
 * valeur [&amp; valeur]
 * Ainsi si on veut rechercher l'acte
 * HHFA016 Appendicectomie, par coelioscopie ou par laparotomie avec préparation par coelioscopie
 * avec à la fois le modificateur P et le modificateur U, on écrit :
 * HHFA016/m:P&amp;U
 *
 * Pour les modificateurs, l'expression concerne la zone d'acte courante.
 *
 * Pour les autres critères, ils concernent le RUM courant, et les actes qui ont le même délai
 * à partir de l'entrée du séjour (lorsque la condition est '&amp;').
 *
 * Exemple : on veut vérifier qu'il y a bien dans le RUM un acte JAFA002 qui est une fois avec une
 * activité 1 et une autre fois avec une activité 4, avec le même délai à partir de l'entrée du séjour :
 * <br>
 *
 *  <code>JAFA002/a:1&4</code>
 *
 *  <br>
 *
 *
 * @author hkaradimas
 */
public class CcamCodePresence
    implements PmsiCriterion
{
  final static Logger lg = LogManager.getLogger(CcamCodePresence.class)

  public static Pattern ccamPattern =  Pattern.compile(/[A-Z][A-Z][A-Z][A-Z]\d\d\d(-\d\d)?/) //pattern pour un code CCAM (avec extention PMSI) autorisé

  /**
   * Classe interne pour stocker les critères de sélection d'un code avec les expressions qui vont avec
   */
  class SelCriteria {
    String code
    Pattern pattern
    AoeNode exprActivites
    AoeNode exprModificateurs
  }

  List<String> includedCodes = new ArrayList<>();
  List<Pattern> includedPatterns = new ArrayList<>();

  //sélectionner si au moins une activité CCAM est présente dans cette liste
  List<String> includedActivities = new ArrayList<>();
  //exclure si au moins une activité CCAM est présente dans cette liste (a priorité sur includedActivities)
  List<String> excludedActivities = new ArrayList<>();

  //sélectionner si au moins un modificateur est présent dans cette liste
  List<String> includedModifiers = new ArrayList<>();
  //exclure si au moins un modificateur est présent dans cette liste (a priorité sur includedModifiers)
  List<String> excludedModifiers = new ArrayList<>();


  /**
   * Constructeur simple avec juste un ensemble de codes à rechercher.
   * Les codes sont soit des codes CCAM normaux si ils sont acceptés par le #ccamPattern,
   * soit une expression régulière sinon.
   * Si le code est un code à 7 caractères, il sera comparé tel quel car les
   * codes lus dans les RSS et RSA ont les espaces de fin enlevés avant comparaison.
   * Donc si par exemple on veut tous les codes qui commencent par JGNE171, on a 2 solutions :
   * <ul>
   * <li>['JGNE171.*']
   * <li>['JGNE171', 'JGNE171-01', 'JGNE171-02', 'JGNE171-03']
   * </ul>
   * @param codesToInclude
   */
  public CcamCodePresence(Set<String> codesToInclude) {
    codesToInclude.each { untrimmedCode ->
      String code = untrimmedCode.trim()
      if (ccamPattern.matcher(code).matches()) {
        includedCodes.add(code)
      }
      else includedPatterns.add(Pattern.compile(code))
    }
  }

  /**
   * Constructeur qui prend une liste.
   * Appelle ensuite le constructeur qui prend un ensemble (Set) en transformant la "List" en "Set".
   * @param codesToInclude
   */
  public CcamCodePresence(List<String> codesToInclude) {
    this(codesToInclude as Set)
  }

  /*
   * Date de réalisation
   * Code CCAM
   * Phase
   * Activité
   * Extension documentaire
   * Modificateurs
   * Remboursement exceptionnel
   * Association non prévue
   * Nombre de réalisations de l'acte n° nZA pendant le séjour
   * Effectue la recherche selon ce qu'il y a dans le contexte.
   * Si il y a 'rum' dans le contexte, recherche dans le rum.
   * Si il y a 'rsa' dans le contexte, recherche dans tous les rums du rsa.
   */
  @Override
  public boolean eval(HashMap context) {

    FszGroup rum = context['rum']
    FszGroup rsa = context['rsa']

    Set ccamCodes = [] as Set

    if (rsa != null) {
      //Dans les RSA le code CCAM a 7 caractères et l'extension PMSI sur 2 caractères !
      rsa.RU.ZA.flatten().each { za ->
        def ccam = za.txtCCCAM
        //println "ccam:$ccam"
        def xtpmsi = za.txtXTPMSI
        def codeToAdd
        if (xtpmsi == '') codeToAdd = ccam
        else codeToAdd = ccam + '-' + xtpmsi
        //si le code finit par -00 on enlève le -00 qui n'est pas pertinent
        if (codeToAdd.endsWith('-00')) codeToAdd = codeToAdd[0..-4]
        lg.debug("ccam '$codeToAdd'")
        ccamCodes.add(codeToAdd)
      }
      //si rsa présent, il a priorité
      //ccamCodes.addAll(rsa.RU.ZA.txtCCCA)
    }
    else if (rum != null) {
      //println "rum:$rum"
      rum.ZA.txtCCCA.each { code ->
        if (code.endsWith('-00')) code = code[0..-4] //enlever les 3 derniers caractères -00 qui ne sont pas pertinents
        lg.debug("ccam '$code'")
        ccamCodes.add(code)
      }
      //println "ccamCodes:$ccamCodes"
    }
    else {
      lg.warn("Ni rum ni rsa trouves.")
      return false;
    }
    //voir d'abord si on trouve dans les codes CCAM des codes de la liste simple.
    if (!ccamCodes.intersect(includedCodes).isEmpty()) return true
    //ok, si on est arrivé ici, c'est que aucun code n'est dans les codes inclus.
    //voir si un des codes concorde avec une des expressions régulières
    boolean anyMatch = includedPatterns.any { pattern ->
      //println "pattern:$pattern,class:${pattern.class}"
      ccamCodes.any { code -> pattern.matcher(code).matches() }
    }
    return anyMatch
  }

}


/*
 * N.B. voici la definition (avec les noms pmsixml) de la zone actes dans un RSS :
 * ZA;Date de réalisation                                      ;DR   ;8;*;*;F;N;N;NA/NA        ;JJMMAAAA Signalement si non renseigné                    ;;
 * ZA;Code CCAM                                                ;CCCA ;7;*;*;O;A;A;NA/NA        ;                                                         ;;
 * ZA;Extension PMSI                                           ;XPMSI;3;*;*;F;A;A;NA/NA        ;ATTENTION, l’extension est obligatoire lorsqu’elle Existe;;
 * ZA;Phase                                                    ;PHAS ;1;*;*;O;A;A;NA/NA        ;                                                         ;;
 * ZA;Activité                                                 ;ACTI ;1;*;*;O;A;A;NA/NA        ;                                                         ;;
 * ZA;Extension documentaire                                   ;XDOCU;1;*;*;F;A;A;NA/NA        ;                                                         ;;
 * ZA;Modificateurs                                            ;MODIF;4;*;*;F;A;A;Gauche/Espace;                                                         ;;
 * ZA;Remboursement exceptionnel                               ;REXC ;1;*;*;F;A;A;NA/NA        ;                                                         ;;
 * ZA;Association non prévue                                   ;ASSNP;1;*;*;F;A;A;NA/NA        ;                                                         ;;
 * ZA;Nombre de réalisations de l'acte n° nZA pendant le séjour;NREAL;2;*;*;O;A;A;Droite/Zéro  ;                                                         ;;
 *
 * L'extension PMSI est obligatoire lorsqu'elle est présente.
 * De ce fait, si on cherche un code qui n'a pas d'extension tel que DEQP003
 * ça va fonctionner sur une recherche CCCA+XPMSI à 'DEQP003   '.
 * Par contre
 * Exemple : le code
 * FCFC001    (Curage lymphonodal [ganglionnaire] iliaque, par cœlioscopie ou par rétropéritonéoscopie)
 * évolue en deux codes avec extension :
 * FCFC001-30 (Curage lymphonodal [ganglionnaire] iliaque, par cœlioscopie ou par rétropéritonéoscopie, sans assistance par robot)
 * FCFC001-40 (Curage lymphonodal [ganglionnaire] iliaque, par cœlioscopie ou par rétropéritonéoscopie, avec assistance par robot)
 * De ce fait si on recherche tous les codes de curage lymphonodal, avant il
 * fallait faire une recherche sur FCFC001, après la mise en oeuvre de la nouvelle
 * nomenclature il faut rechercher FCFC001-30 et FCFC001-40.
 * L'utilisation d'une recherche par expression régulières simplifie cela, il
 * suffit de chercher FCFC001.*
 *


 */