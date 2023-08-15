package fr.karadimas.gpmsi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.input.BOMInputStream;
//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.opencsv.CSVReader;
import com.opencsv.ICSVParser;

import fr.karadimas.gpmsi.poi.PoiHelper;
import fr.karadimas.pmsixml.FieldParseException;
import fr.karadimas.pmsixml.FszNode;
import fr.karadimas.pmsixml.FszReader;
import fr.karadimas.pmsixml.InputString;
import fr.karadimas.pmsixml.MetaFileLoader;
import fr.karadimas.pmsixml.MissingMetafileException;
import fr.karadimas.pmsixml.MonoLevelReader;
import fr.karadimas.pmsixml.RhsReader;
import fr.karadimas.pmsixml.RsaReader;
import fr.karadimas.pmsixml.RsfaceReader;
import fr.karadimas.pmsixml.RssReader;
import groovy.lang.Closure;

/**
 * Etape de script. Les étapes de script résultent de l'exécution des méthodes de {@link GroovyScriptsBase}.
 * A l'intérieur des blocs 'onItem', l'objet 'item' possède des variables, qui dépendent du type de l'étape.
 * Les étapes admises actuellement (avec les variables disponibles) sont :
 * <ul>
 *   <li>RSS : 'RUM' (ou 'rum'), 'line', 'linenr' 
 *   <li>RSA : 'RSA' (ou 'rsa'), 'line', 'linenr' 
 *   <li>RHS : 'RHS' (ou 'rhs'), 'line', 'linenr'
 *   <li>RHA : pas encore implemente
 *   <li>CSV : 'row' ({@link CsvRow}), 'linenr'
 *   <li>SINGLE : pas de variable, execute une fois le corps et sort
 *   <li>LINE : 'line', 'linenr'
 *   <li>VIDHOSP : 'vidhosp', 'line', 'linenr'
 *   <li>RSFACE : 'RSF' (ou 'rsf'), 'line', 'linenr'
 *   <li>MONO : 'MONO' (ou 'mono'), 'line', 'linenr'
 *   <li>XLPOI : 'row' , 'linenr' (EN COURS DE DEV)
 * </ul>
 * En entrée on déclare soit un <code>inputReader</code>, soit un <code>inputFilePath</code>. Le 
 * <code>inputReader</code> a priorité sur le <code>inputFilePath</code>.
 * <p>
 * ScriptStep n'envoie rien par défaut sur la sortie standard, 
 * mais permet de déclarer un
 * outputWriter via la variable <code>outputWriter</code> ou un 
 * outputFilePath via la variable <code>output</code>.
 * Les scripts qui s'exécutent dans
 * un ScriptStep sont encouragés à utiliser ces deux variables pour déclarer
 * le fichier de sortie ou le flux de sortie, et à les utiliser
 * pour ecrire leurs resultats ; cependant il n'y a pas d'obligation à
 * utiliser ces variables.
 * 
 * <p>
 * Pour certaines étapes il est nécessaire de préciser explicitement les
 * métadonnées à utiliser. On peut faire cela en définissant la valeur
 * de <code>metaHint</code> pour aider à trouver la bonne version d'une
 * métadonnées (par exemple l'année pour un les métadonnées RSFACE),
 * ou bien on peut définir directement la valeur <code>metaName</code> pour
 * donner le nom de la métadonnée à trouver.
 * Si le fichier des métadonnées n'est pas
 * un fichier fourni par défaut dans la librairie on peut donner le chemin
 * du répertoire où chercher ce fichier de métadonnées via <code>metaDir</code>.
 * <p>
 * Par exemple pour lire un fichier TRA de 2022 qui est dans 
 * <code>C:\t\mon-tra.txt</code>
 * on mettra <code>input</code> à <code>"C:\t\mon-tra.txt"</code> et
 * <code>metaName</code> à <code>tra2016</code>. gpmsi trouvera tout
 * seul le fichier <code>gpmsi2016.csv</code> qui est dans les ressources de la
 * librairie pmsixml. 
 * 
 * <p>
 * La librairie utilisée pour lire les données csv est <a href="https://opencsv.sourceforge.net/">OpenCsv</a>.
 * S'y référer pour voir tout ce que fait cette librairie qui permet d'analyser fiablement le csv.
 * 
 * 
 * @author hkaradimas
 *
 */
public class ScriptStep {
    public static final int ST_RSS = 1;
    public static final int ST_RSA = 2;
    public static final int ST_RHS = 3; //a implementer
    public static final int ST_RHA = 4; //a implementer
    public static final int ST_CSV = 5;
    public static final int ST_SINGLE = 6;
    public static final int ST_LINE = 7; //traitement ligne par ligne
    public static final int ST_VIDHOSP = 8; 
    public static final int ST_RSFACE = 9; //RSFACE. Nécessite l'année comme "metaHint"
    public static final int ST_MONO = 10; //fichier a un seul niveau (mono-niveau) 
    public static final int ST_XLPOI = 11; //fichier excel lu par Apache POI 
    static Logger lg = LogManager.getLogger(ScriptStep.class);
    static String[] emptyRow = {};
    
    String inputFilePath;
    String inputEncoding;
    Reader inputReader; //si non null, a priorite sur inputFilePath
    InputStream inputStream; //si non null, a priorité sur inputFilePath pour les fichiers Excel POI
    String outputFilePath;
    Writer outputWriter; //si non null, a priorite sur outputFilePath
    int stepType;
    String name;
    String metaHint; //info pour aider a trouver la bonne metadonnee (par ex. '2017' pour trouver les fichiers rsface au format 2017)
    String metaName; //fichier meta donnees a utiliser explicitement
    GroovyScriptsBase owner;
    String metasDirPath;
    String[] csvHeaderRow = {}; //rangee d'en-tête lors de la lecture d'un fichier csv et aussi pour xl
    int countOfCsvLinesToSkip = 0; //nombre de lignes du fichier à sauter avant de lire les données csv
    Integer sheetNumber = null; //numero de l'onglet à charger, commence à 0. A priorité sur sheetName. Par défaut c'est l'onglet 0 qui est chargé.
    String sheetName = null; //nom de l'onglet à charger, vide par défaut.
    HashMap<String, Integer> csvColumnIndexesByName = new HashMap<>(); //numéro de colonne pour un nom, utilisé pour csv et xl
    
    Closure<?> onInit;
    Closure<?> onItem;
    Closure<?> onEnd;
    
    Closure<?> childStep;
    
    ScriptStep childScriptStep; //experimental
    ScriptStep parentScriptStep;
    
    RssReader rssRdr;
    RhsReader rhsRdr;
    RsaReader rsaRdr;
    MonoLevelReader mlRdr;
    FszReader vidhospRdr;
    RsfaceReader rsfaceRdr;
    
    MetaFileLoader mfl = new MetaFileLoader();
    
    int linenr;
    
    char csvSeparator = ';'; //default csv separator for France (fixed because PMSI is mainly for France)
    char csvQuoteCharacter = ICSVParser.DEFAULT_QUOTE_CHARACTER;
    char csvEscapeCharacter = ICSVParser.DEFAULT_ESCAPE_CHARACTER;
    
    boolean truncatedInputAccepted = true;

    //-- variables utilisees dans chaque traitement d'un item
    
    BufferedReader br = null;
    CSVReader csvr = null;
    Workbook wb = null; //le classeur dans le cadre d'une étape XLPOI
    Sheet sh = null; //l'onglet dans le cadre d'une étape XLPOI
    String line = null;
    String[] csvRow = null;
    CsvRow csvRowHelper = new CsvRow(this);
    XlRow xlRowHelper = new XlRow(this);
    InputString instr = new InputString();
    HashMap<String, Object> itm = new HashMap<String, Object>();
    //-- fin variables utilisees dans chaque traitement d'un item

    private static SimpleDateFormat frenchDate = new SimpleDateFormat("dd/MM/yyyy");
    
    public ScriptStep(GroovyScriptsBase owner) 
            throws FieldParseException, IOException, MissingMetafileException 
    {
        this.owner = owner;
        //initialisations communes pour éviter les bugs. Inefficace, sera amélioré dans le futur
        rssRdr = new RssReader();
        rsaRdr = new RsaReader();
        rhsRdr = new RhsReader();
        mlRdr = new MonoLevelReader();
        vidhospRdr = new FszReader(mfl, "vidhosp");
        rsfaceRdr = new RsfaceReader();
    }
    
    public void input(String path) { inputFilePath = path; }
    
    public void input(String path, String encoding) {
        inputFilePath = path;
        inputEncoding = encoding;
    }
    
    public void inputEncoding(String enc) { inputEncoding = enc; }
    
    public void output(String path) { outputFilePath = path; }
    
    public void name(String str) { name = str; }
    
    public void onInit(Closure<?> dcl) {
        onInit = dcl.rehydrate(this, owner, this);
    }
    
    public void onItem(Closure<?> dcl) {
        onItem = dcl.rehydrate(this, owner, this);
    }
    
    public void onEnd(Closure<?> dcl) {
        onEnd = dcl.rehydrate(this, owner, this);
    }
    
    /**
     * Experimental : chainage d'une etape 'rss' (resume standardise de sortie) apres cette etape
     * @param dcl la déclaration de closure
     * @return Le ScriptStep cree
     * @throws MissingMetafileException si une métadonnée de fichier n'a pas été trouvée
     * @throws IOException si erreur entree sortie
     * @throws FieldParseException si une erreur a lieu lors de l'analyse d'un champ
     */
    public ScriptStep childStepRss(Closure<?> dcl) 
        throws FieldParseException, IOException, MissingMetafileException 
    {
        childScriptStep = connect(owner.task(dcl, ST_RSS));
        return childScriptStep;
    }
    
    /**
     * Experimental : chainage d'une etape 'rsa' (resume de sortie anonymise) apres cette etape
     * @param dcl la déclaration de closure
     * @return Le ScriptStep cree
     * @throws MissingMetafileException  si une métadonnée de fichier n'a pas été trouvée
     * @throws IOException  si erreur entree sortie
     * @throws FieldParseException si une erreur a lieu lors de l'analyse d'un champ
     */
    public ScriptStep childStepRsa(Closure<?> dcl) 
        throws FieldParseException, IOException, MissingMetafileException 
    {
        childScriptStep = connect(owner.task(dcl, ST_RSA));
        return childScriptStep;
    }
    
    /**
     * Experimental : chainage d'une etape 'rhs' (resume hebdomadaire standardise) apres cette etape
     * @param dcl la déclaration de closure
     * @return Le ScriptStep cree
     * @throws MissingMetafileException si une métadonnée de fichier n'a pas été trouvée 
     * @throws IOException  si erreur entree sortie
     * @throws FieldParseException si une erreur a lieu lors de l'analyse d'un champ
     */
    public ScriptStep childStepRhs(Closure<?> dcl) 
        throws FieldParseException, IOException, MissingMetafileException 
    {
        childScriptStep = connect(owner.task(dcl, ST_RHS));
        return childScriptStep;
    }
    
    /**
     * Experimental : chainage d'une etape 'rha' (resume hebdomadaire anonymise) apres cette etape
     * @param dcl la déclaration de closure
     * @return Le ScriptStep cree
     * @throws MissingMetafileException  si une métadonnée de fichier n'a pas été trouvée
     * @throws IOException  si erreur entree sortie
     * @throws FieldParseException si une erreur a lieu lors de l'analyse d'un champ
     */
    public ScriptStep childStepRha(Closure<?> dcl) 
        throws FieldParseException, IOException, MissingMetafileException 
    {
        childScriptStep = connect(owner.task(dcl, ST_RHA));
        return childScriptStep;
    }
    
    /**
     * Experimental : chainage d'une etape 'csv' (comma separated values) apres cette etape
     * @param dcl la déclaration de closure
     * @return Le ScriptStep cree
     * @throws MissingMetafileException  si une métadonnée de fichier n'a pas été trouvée
     * @throws IOException si erreur entree sortie 
     * @throws FieldParseException si une erreur a lieu lors de l'analyse d'un champ
     */
    public ScriptStep childStepCsv(Closure<?> dcl) 
        throws FieldParseException, IOException, MissingMetafileException 
    {
        childScriptStep = connect(owner.task(dcl, ST_CSV));
        return childScriptStep;
    }
    
    /**
     * Experimental : chainage d'une etape 'single' (etape unique isolee) apres cette etape
     * @param dcl la déclaration de closure
     * @return Le ScriptStep cree
     * @throws MissingMetafileException  si une métadonnée de fichier n'a pas été trouvée
     * @throws IOException si erreur entree sortie 
     * @throws FieldParseException si une erreur a lieu lors de l'analyse d'un champ
     */
    public ScriptStep childStepSingle(Closure<?> dcl) 
        throws FieldParseException, IOException, MissingMetafileException 
    {
        childScriptStep = connect(owner.task(dcl, ST_SINGLE));
        return childScriptStep;
    }
    
    /**
     * Experimental : chainage d'une etape 'line' (traitement ligne par ligne) apres cette etape
     * @param dcl la déclaration de closure
     * @return Le ScriptStep cree
     * @throws MissingMetafileException  si une métadonnée de fichier n'a pas été trouvée
     * @throws IOException si erreur entree sortie 
     * @throws FieldParseException si une erreur a lieu lors de l'analyse d'un champ
     */
    public ScriptStep childStepLine(Closure<?> dcl) 
        throws FieldParseException, IOException, MissingMetafileException 
    {
        childScriptStep = connect(owner.task(dcl, ST_LINE));
        return childScriptStep;
    }
    
    /**
     * Experimental : chainage d'une etape 'vidhosp' (variables identifiantes d hospitalisation) apres cette etape
     * @param dcl la déclaration de closure
     * @return Le ScriptStep cree
     * @throws MissingMetafileException  si une métadonnée de fichier n'a pas été trouvée
     * @throws IOException si erreur entree sortie 
     * @throws FieldParseException si une erreur a lieu lors de l'analyse d'un champ
     */
    public ScriptStep childStepVidhosp(Closure<?> dcl) 
        throws FieldParseException, IOException, MissingMetafileException 
    {
        childScriptStep = connect(owner.task(dcl, ST_VIDHOSP));
        return childScriptStep;
    }
    
    /**
     * Experimental : chainage d'une etape 'rsface' (resume standard de facturation d activites et consultations externes) apres cette etape
     * @param dcl la déclaration de closure
     * @return Le ScriptStep cree
     * @throws MissingMetafileException  si une métadonnée de fichier n'a pas été trouvée
     * @throws IOException si erreur entree sortie 
     * @throws FieldParseException si une erreur a lieu lors de l'analyse d'un champ
     */
    public ScriptStep childStepRsface(Closure<?> dcl) 
        throws FieldParseException, IOException, MissingMetafileException 
    {
        childScriptStep = connect(owner.task(dcl, ST_RSFACE));
        return childScriptStep;
    }
    
    /**
     * Experimental : chainage d'une etape 'mono' (pmsi generique mono niveau) apres cette etape
     * @param dcl la déclaration de closure
     * @return Le ScriptStep cree
     * @throws MissingMetafileException  si une métadonnée de fichier n'a pas été trouvée
     * @throws IOException si erreur entree sortie 
     * @throws FieldParseException si une erreur a lieu lors de l'analyse d'un champ
     */
    public ScriptStep childStepMono(Closure<?> dcl) 
        throws FieldParseException, IOException, MissingMetafileException 
    {
        childScriptStep = connect(owner.task(dcl, ST_MONO));
        return childScriptStep;
    }
    
    /**
     * Connecte l'enfant au parent, et si la sortie est un PipedWriter,
     * la connecte a l'entree de l'enfant.
     * Met l'enfant dans la variable childScriptStep.
     * @param child le ScriptStep enfant
     * @return le scriptStep qui a été passé en paramètre et adapté
     * @throws IOException si erreur entree sortie
     */
    private ScriptStep connect(ScriptStep child)
        throws IOException 
    {
      childScriptStep = child;
      childScriptStep.parentScriptStep = this; //connecter l'enfant au parent
      if (outputWriter instanceof PipedWriter) {
        PipedWriter pow = (PipedWriter) outputWriter;
        PipedReader pir = new PipedReader(pow);
        childScriptStep.setInputReader(pir);
      }
      return child;
    }
    
    public String getInputFilePath() {
        return inputFilePath;
    }
    
    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }
    
    public int getStepType() {
        return stepType;
    }
    
    public void setStepType(int stepType) {
        this.stepType = stepType;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Closure<?> getOnInit() {
        return onInit;
    }
    
    public void setOnInit(Closure<?> onInit) {
        this.onInit = onInit;
    }
    
    public Closure<?> getOnItem() {
        return onItem;
    }
    
    public void setOnItem(Closure<?> onItem) {
        this.onItem = onItem;
    }
    
    public Closure<?> getOnEnd() {
        return onEnd;
    }
    
    public void setOnEnd(Closure<?> onEnd) {
        this.onEnd = onEnd;
    }
    
    /**
     * Initialisation.
     * Appelle le code utilisateur d'initialisation, et ensuite
     * si besoin cree le <code>inputReader</code> (ou le <code>inputStream</code> pour xlpoi) s'il n'est pas defini.
     * @throws IOException si erreur d'entrée/sortie
     */
    public void callOnInit() 
        throws IOException 
    {
        //System.err.println("inputReader : " + inputReader);
        linenr = 1; // linenr commence à 1 (et pas à 0), de manière à retrouver
        // plus facilement les erreurs dans un éditeur de texte
        instr.acceptTruncated = truncatedInputAccepted;
        if (onInit != null) onInit.call(); //appeler la closure onInit pour préparations supplémentaires avant l'initialisation proprement dite
        //on distingue maintenant l'initialisation pour des fichiers binaires (Excel) et l'initialisation pour de fichiers orientés ligne/texte qui était le mode traditionnel pour gpmsi
        if (stepType == ST_XLPOI) {
          if (inputStream == null) {
            if (inputFilePath == null) throw new FileNotFoundException("Erreur lors de l'ouverture de xlpoi : ni input, ni inputStream trouve.");
            inputStream = new FileInputStream(inputFilePath);
          }
          wb = WorkbookFactory.create(inputStream);
          if (sheetNumber != null) sh = wb.getSheetAt(sheetNumber);
          else if (sheetName != null) sh = wb.getSheet(sheetName);
          else sh = wb.getSheetAt(0);
          //la première ligne doit être a priori une ligne d'en-têtes. Essayer de la lire
          Row firstRow = sh.getRow(linenr-1);
          int ncols = firstRow.getLastCellNum()+1;
          csvHeaderRow = new String[ncols];
          for (int i = 0; i < ncols; i++) {
            if (firstRow == null) csvHeaderRow[i] = "";
            else csvHeaderRow[i] = PoiHelper.getCellValueAsString(firstRow.getCell(i));
          }
        }
        else {
          if (inputReader == null && inputFilePath != null) {
            if (inputEncoding == null) inputReader = new FileReader(inputFilePath); //pas d'encodage specifie -> utiliser encodage par defaut.
            else {
              FileInputStream fis = new FileInputStream(inputFilePath);
              BOMInputStream bomis = new BOMInputStream(fis); //221027 hk ajout de BOMInputStream car java ne detecte pas de BOM automatiquement (alors que c'est possible pour UTF-8) et du coup un "?" est lu en 1er caractere là où il y a un BOM.
              inputReader = new InputStreamReader(bomis, inputEncoding);
            }
          }
          if (inputReader != null) br = new BufferedReader(inputReader);
          if (stepType == ST_CSV) {
            if (br == null) throw new FileNotFoundException(inputFilePath);
            csvr = new CSVReader(br, csvSeparator, csvQuoteCharacter, csvEscapeCharacter, countOfCsvLinesToSkip);
            setCsvHeaderRow(csvr.readNext());
            csvRow = csvHeaderRow; //la première ligne ce sont les en-têtes
          }
          else if (stepType == ST_SINGLE) {
            line = "";
          }
          else {
            // initialisation commune a tous les traitements orientes ligne
            if (br == null) throw new FileNotFoundException("Pas de donnees trouvee pour cette etape");
            line = br.readLine();
          }          
        }// else ( if (stepType == ST_XLPOI) )
        if (childScriptStep != null) childScriptStep.callOnInit(); //experimental
    }
    
    public void callOnEnd() {
        if (childScriptStep != null) childScriptStep.callOnEnd(); //experimental
        if (onEnd != null) onEnd.call();
    }
    
  /**
   * Ici "onItem" n'est pas appelé pour le "childStep".
   * C'est au code utilisateur de l'appeler selon les besoins.
   * @throws FieldParseException si erreur lors de l'analyse d'un champ
   * @throws MissingMetafileException si une métadonnée de définition n'est pas trouvée
   */
  public void processItems()
      throws FieldParseException, MissingMetafileException 
  {
    if (onItem == null) return;

    try {
      while (line != null || csvRow != null) {
        processNextItem();
      }
    }
    catch (FileNotFoundException e) {
      System.err.println("Fichier non trouve : " + inputFilePath);
      return;
    }
    catch (IOException e) {
      System.err.println("Erreur d'entree-sortie " + e);
      return;
    }
    catch (Exception e) {
      System.err.println("Erreur (ligne " + linenr + ") : " + e);
      throw e;
    }
    finally {
      try {
        if (inputReader != null) inputReader.close();
      }
      catch (IOException ignored) {
      }
      try {
        if (inputStream != null) inputStream.close();
      }
      catch (IOException ignored) {
      }
      try {
        if (br != null) br.close();
      }
      catch (IOException ignored) {
      }
    }
  }// processItems

  public void processNextItem()
      throws FieldParseException, MissingMetafileException, IOException 
  {
    switch (stepType) {
    case ST_RSS:
      if (line.trim().length() > 0) {
        FszNode nd = rssRdr.readOne(line, linenr);
        // StringBuffer sb = new StringBuffer();
        // nd.getMeta().dump(sb);
        // lg.debug(sb.toString());
        itm.put("RUM", nd);
        itm.put("rum", nd);
        itm.put("line", line);
        itm.put("linenr", linenr);
        onItem.call(itm);
      }
      line = br.readLine();
      linenr++;
      break;
    case ST_RSA:
      if (line.trim().length() > 0) {
        FszNode nd = rsaRdr.readRSA(line, linenr);
        itm.put("rsa", nd);
        itm.put("RSA", nd);
        itm.put("line", line);
        itm.put("linenr", linenr);
        onItem.call(itm);
      }
      line = br.readLine();
      linenr++;
      break;
    case ST_RHS:
      if (line.trim().length() > 0) {
        FszNode nd = rhsRdr.readOne(line, linenr, false);
        // StringBuffer sb = new StringBuffer();
        // nd.getMeta().dump(sb);
        // lg.debug(sb.toString());
        itm.put("RHS", nd);
        itm.put("rhs", nd);
        itm.put("line", line);
        itm.put("linenr", linenr);
        onItem.call(itm);
      }
      line = br.readLine();
      linenr++;
      break;
    case ST_RHA:
      throw new RuntimeException("ST_RHA : cas non implémenté !");
    case ST_CSV:
      csvRowHelper.setRow(csvRow);
      itm.put("row", csvRowHelper);
      itm.put("linenr", linenr);
      onItem.call(itm);
      csvRow = csvr.readNext();
      linenr++;
      break;
    case ST_SINGLE:
      onItem.call(itm);
      line = null; // just call once and get out of loop
      break;
    case ST_LINE:
      itm.put("line", line);
      itm.put("linenr", linenr);
      onItem.call(itm);
      line = br.readLine();
      linenr++;
      break;
    case ST_VIDHOSP:
      if (line.trim().length() > 0) {
        instr.nextLine(line);
        FszNode nd = vidhospRdr.readOne(instr);
        itm.put("vidhosp", nd);
        itm.put("line", line);
        itm.put("linenr", linenr);
        onItem.call(itm);
      }
      line = br.readLine();
      linenr++;
      break;
    case ST_MONO:
      if (line.trim().length() > 0) {
        FszNode nd = mlRdr.readMonoLevel(line, linenr);
        // StringBuffer sb = new StringBuffer();
        // nd.getMeta().dump(sb);
        // lg.debug(sb.toString());
        itm.put("MONO", nd);
        itm.put("mono", nd);
        itm.put("line", line);
        itm.put("linenr", linenr);
        onItem.call(itm);
      }
      line = br.readLine();
      linenr++;
      break;
    case ST_RSFACE:
      if (line.trim().length() > 0) {
        FszNode nd = rsfaceRdr.readRSFACE(line, linenr);
        itm.put("rsf", nd);
        itm.put("RSF", nd);
        itm.put("line", line);
        itm.put("linenr", linenr);
        onItem.call(itm);
      }
      line = br.readLine();
      linenr++;
      break;
    case ST_XLPOI:
      itm.put("row", xlRowHelper);
      itm.put("linenr", linenr);
      onItem.call(itm);
      linenr++;
      break;
    default:
      lg.error("Unknown stepType " + stepType + ", doing nothing");
      line = null;
    }//switch    
    if (childStep != null) {
      childScriptStep.processNextItem();
    }
  }
  
    public char getCsvSeparator() {
        return csvSeparator;
    }

    /**
     * Définir le séparateur qui sera utilisé pour construire le CSVReader (défaut : ';')
     * @param csvSeparator Le séparateur
     */
    public void setCsvSeparator(char csvSeparator) {
        this.csvSeparator = csvSeparator;
    }

    public char getCsvEscapeCharacter() {
      return csvEscapeCharacter;
    }
    
    /**
     * Définir le caractère qui servira pour les séquences d'échappement (défaut : '\\', pour désactiver,
     * utiliser '\0'.
     * @param csvEscapeCharacter Le caractère à utiliser
     */
    public void setCsvEscapeCharacter(char csvEscapeCharacter) {
      this.csvEscapeCharacter = csvEscapeCharacter;
    }
    
    public char getCsvQuoteCharacter() {
      return csvQuoteCharacter;
    }
    
    /**
     * Définir le caractère qui sera utilisé pour délimiter le texte. Défaut : '"'
     * @param csvQuoteCharacter Le caractère à utiliser
     */
    public void setCsvQuoteCharacter(char csvQuoteCharacter) {
      this.csvQuoteCharacter = csvQuoteCharacter;
    }
    
    /**
     * Identique à {@link #setCsvSeparator(char)} mais plus clair lorsqu'on utilise groovy
     * @param csvSeparator Le séparateur csv (typiquement ";", ou "\t", ou ",")
     */
    public void csvSeparator(char csvSeparator) { setCsvSeparator(csvSeparator); }
    
    public void csvEscapeCharacter(char csvEscapeCharacter) { setCsvEscapeCharacter(csvEscapeCharacter); }
    
    public void csvQuoteCharacter(char csvQuoteCharacter) { setCsvQuoteCharacter(csvQuoteCharacter); }
    
    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }
    
    /**
     * Formater au format français (dd/MM/yyyy). 
     * @param d La date à formater. si null retourne une chaîne vide.
     * @return Une String de d au format français.
     */
    public String formatAsFrenchDate(Date d) {
        if (d == null) return "";
        return frenchDate.format(d); 
    }

    /**
     * Formater au format français (dd/MM/yyyy)
     * @param ld La date (de type LocalDate) à formater. si null retourne une chaîne vide.
     * @return Une String de ld au format français
     * @apiNote Obligé de renommer de formatAsFrenchDate en formatLocalAsFrenchDate car
     *          malgré mes efforts lorsque ld est null j'ai une erreur <code>Ambiguous method overloading</code>
     *          même en faisant des casts en LocalDate. 
     */
    public String formatLocalAsFrenchDate(LocalDate ld) {
      return DateUtils.formatAsFrenchDate(ld);
    }
    
  public String getMetaName() {
    return metaName;
  }

  /** nom a utiliser pour pour syntaxe DSL
   * 
   * @param metaName Le nom de métadonnée pour trouver le fichier
   */
  public void metaName(String metaName) {
    setMetaName(metaName);
  }
  
  public void setMetaName(String metaName) {
    this.metaName = metaName;
    mlRdr.setMetaName(metaName);
    //rssRdr metaName is implicit
    //rsaRdr metaName is implicit
    //rhsRdr metaName is implicit
    //vidhospRdr metaName is implicit      
  }

  public String getMetaHint() { return metaHint; }
  
  /** nom a utiliser pour syntaxe DSL
   * 
   * @param metaHint L'indice pour aider à retrouver les métadonnées
   */
  public void metaHint(String metaHint) {
      setMetaHint(metaHint);
  }
  
  public void setMetaHint(String metaHint) {
      this.metaHint = metaHint;
      rsfaceRdr.setYearOfFormat(metaHint);
  }
  
  public String getMetasDir() {
    return metasDirPath;
  }

  public void setMetasDir(String metasDirPath) {
    this.metasDirPath = metasDirPath;
    if (metasDirPath != null) {
      File metasDir = new File(metasDirPath);
      mlRdr.setMetasDir(metasDir);
      rssRdr.setMetasDir(metasDir);
      rsaRdr.setMetasDir(metasDir);
      rhsRdr.setMetasDir(metasDir);
      mlRdr.setMetasDir(metasDir);
      vidhospRdr.getMetaLoader().setMetaFilesDir(metasDir);   
      rsfaceRdr.setMetasDir(metasDir);
    }
  }
    
  /** nom a utiliser pour pour syntaxe DSL
   * 
   * @param metasDirPath Le chemin pour rechercher les fichiers de métadonnées
   */
  public void metasDir(String metasDirPath) { setMetasDir(metasDirPath); }

  public boolean isTruncatedInputAccepted() {
    return truncatedInputAccepted;
  }

  public void setTruncatedInputAccepted(boolean truncatedInputAccepted) {
    this.truncatedInputAccepted = truncatedInputAccepted;
  }
  
  public String[] getCsvHeaderRow() { return csvHeaderRow; }
  
  public void setCsvHeaderRow(String[] newRow) {
    if (newRow == null) throw new NullPointerException("newRow ne peut pas être null");
    csvHeaderRow = newRow;
    csvColumnIndexesByName.clear();
    for (int i = 0; i < csvHeaderRow.length; i++) {
      if (lg.isDebugEnabled()) lg.debug("col["+i+"]:'"+csvHeaderRow[i]+"'");
      csvColumnIndexesByName.put(csvHeaderRow[i], i);
    }
  }
  
  /**
   * Renvoyer l'index de la colonne csv (ou xl) qui a le nom donné ou -1 si il n'y a pas de colonne avec ce nom. 
   * @param name Le nom de la colonne
   * @return le numéro de colonne (commence à 0), -1 si non trouvé
   */
  public int getCsvColumnIndex(String name) {
    if (!csvColumnIndexesByName.containsKey(name)) return -1;
    else return csvColumnIndexesByName.get(name); 
  }
  
  /**
   * Nom plus court pour {@link #getCsvColumnIndex(String)}
   * @param name Le nom de la colonne
   * @return le numéro de colonne (commence à 0), -1 si non trouvé
   */
  public int ci(String name) { return getCsvColumnIndex(name); }

  public Reader getInputReader() {
    return inputReader;
  }

  public void setInputReader(Reader inputReader) {
    this.inputReader = inputReader;
  }

  /**
   * forme courte pour pouvoir écrire en Groovy quelque chose comme :
   * <pre>inputReader new StringReader()</pre>
   * @param inputReader Le {@link Reader} à utiliser 
   */
  public void inputReader(Reader inputReader) {
    this.inputReader = inputReader;
  }

  public Writer getOutputWriter() {
    return outputWriter;
  }

  public void setOutputWriter(Writer outputWriter) {
    this.outputWriter = outputWriter;
  }

  /** 
   * forme courte pour pouvoir écrire en Groovy quelque chose comme 
   * <pre>outputWriter new StringWriter()</pre>
   * @param outputWriter Le {@link Writer} à utiliser
   */
  public void outputWriter(Writer outputWriter) {
    this.outputWriter = outputWriter;
  }
  
  public ScriptStep getChildScriptStep() {
    return childScriptStep;
  }

  public ScriptStep getParentScriptStep() {
    return parentScriptStep;
  }

  public int getCountOfCsvLinesToSkip() {
    return countOfCsvLinesToSkip;
  }

  public void setCountOfCsvLinesToSkip(int countOfCsvLinesToSkip) {
    this.countOfCsvLinesToSkip = countOfCsvLinesToSkip;
  }

  /**
   * forme courte pour pouvoir écrire en Groovy quelque chose comme
   * <pre>countOfCsvLinesToSkip 5</pre>
   * @param countOfCsvLinesToSkip Le nombre de lignes à sauter avant de lire les données csv proprement dites
   */
  public void countOfCsvLinesToSkip(int countOfCsvLinesToSkip) {
    this.countOfCsvLinesToSkip = countOfCsvLinesToSkip;
  }

}
