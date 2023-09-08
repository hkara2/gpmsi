package fr.karadimas.gpmsi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

/**
 * Objet "destination" pour écrire dans un fichier ou un flux des valeurs qui seront
 * séparées par une virgule ou un point-virgule.
 * En interne, un objet {@link CSVWriter} est utilisé pour écrire au format csv.
 * Pour créer une destination, on utilise un des constructeurs disponibles.
 * On écrit ensuite les valeurs une par une en utilisant la fonction {@link #f(Object)} ou
 * bien {@link #f(String)}.
 * Une fois toutes les valeurs d'une rangée envoyées, on appelle la fonction {@link #endRow()}.
 * Lorsque l'on a fini d'envoyer toutes les rangées, on ferme la destination
 * en appelant {@link #close()}.
 * <br>
 * La librairie <a href="https://javacsv.sourceforge.net/">opencsv</a> utilisée est une ancienne version
 * qui est plus simple pour le peu qui est utilisé ici.
 * Il se peut qu'à l'avenir une nouvelle version sera utilisée.
 * 
 * Si on veut d'autres valeurs pour les séparateurs, utiliser un CSVWriter directement.
 * 
 * @author hkaradimas
 *
 */
public class CsvDestination 
{
  //OutputStream os;
  BufferedWriter bw;
  char fieldSeparator = ';';
	//int fieldNumber = 0;
  String fieldSeparatorStr = String.valueOf(fieldSeparator);
  CSVWriter csvw;
  ArrayList<String> fieldBuffer = new ArrayList<>();
  
	public CsvDestination(OutputStream os, String encoding, char fieldSeparator)
			throws UnsupportedEncodingException
	{
	  this(new OutputStreamWriter(os, encoding), fieldSeparator);
	}
	
	public CsvDestination(OutputStream os, String encoding)
			throws UnsupportedEncodingException
	{
		this(os, encoding, ';');
	}
	
	/**
	 * Destination csv avec le séparateur par défaut ';'
	 * @param w Le writer à utiliser
	 */
    public CsvDestination(Writer w) {
      this(w, ';');
    }
    
    /**
     * Destination csv avec le séparateur donné.
     * Le reste des paramètres est :
     * <ul>
     * <li> quote character : "
     * <li> escape quote character : "
     * <li> line end : \r\n
     * </ul>
     * @param w
     * @param fieldSeparator
     */
	public CsvDestination(Writer w, char fieldSeparator) {
      //this.os = null;
      this.fieldSeparator = fieldSeparator;
      //OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
      bw = new BufferedWriter(w);
      /*
       * @param writer     The writer to an underlying CSV source.
       * @param separator  The delimiter to use for separating entries
       * @param quotechar  The character to use for quoted elements
       * @param escapechar The character to use for escaping quotechars or escapechars
       * @param lineEnd    The line feed terminator to use
       * On met tout par défaut, sauf la fin de ligne que l'on force à \r\n
       */
      csvw = new CSVWriter(
          bw, 
          fieldSeparator, 
          CSVWriter.DEFAULT_QUOTE_CHARACTER, 
          CSVWriter.DEFAULT_ESCAPE_CHARACTER, 
          CSVWriter.RFC4180_LINE_END);
	}
	
    public CsvDestination(File f, String encoding, char fieldSeparator)
        throws UnsupportedEncodingException, FileNotFoundException
{
    this(new FileOutputStream(f), encoding, fieldSeparator);
}

    public CsvDestination(File f, String encoding)
        throws UnsupportedEncodingException, FileNotFoundException
{
    this(new FileOutputStream(f), encoding);
}

	public CsvDestination(File f) 
			throws UnsupportedEncodingException, FileNotFoundException 
	{
		this(f, Charset.defaultCharset().name());
	}
	
	public void f(String fieldValue)
			throws IOException 
	{
		if (fieldValue == null) fieldValue = "";
		fieldBuffer.add(fieldValue);
		//boolean quote = false;
		//if (fieldValue.contains(fieldSeparatorStr)) quote = true;
		//if (fieldNumber > 0) bw.write(fieldSeparator);
		//if (quote) bw.write('"');
		//bw.write(fieldValue);
		//if (quote) bw.write('"');
		//fieldNumber++;
	}
	
	public void f(Object obj) throws IOException {
		f(obj == null ? "" : obj.toString());
	}
	
	/**
	 * Finir la rangée courante, en envoyant la séquence de fin de ligne de la plateforme.
	 * @throws IOException Si erreur E/S
	 */
	public void endRow()
			throws IOException 
	{
		String[] nextLine = fieldBuffer.toArray(new String[0]);
		csvw.writeNext(nextLine, false);
		fieldBuffer.clear();
		//bw.newLine();
		//fieldNumber = 0;
	}
	
	/**
	 * Appelle {@link CSVWriter#writeAll(List)}, avec une liste de tableaux, pour
	 * faire l'écriture en une seule fois.
	 * @param allLines Liste des tableaux
	 */
	public void writeAll(List<String[]> allLines) 
	{
		csvw.writeAll(allLines);
	}
	
	public void flush() throws IOException {
		csvw.flush();
		bw.flush();
		//os.flush();
	}
	
	/**
	 * Ferme la destination en fermant tous les flux internes et le flux de sortie qui a été fourni au départ.
	 * @throws IOException Si erreur E/S
	 */
	public void close() throws IOException {
		csvw.close();
		bw.close();
		//os.close();
	}

  public char getFieldSeparator() {
		return fieldSeparator;
	}

	public void setFieldSeparator(char fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
		this.fieldSeparatorStr = String.valueOf(fieldSeparator);
	}
	
	/**
	 * Donne un accès direct au {@link CSVWriter}. Permet d'utiliser d'autres fonctions
	 * de la librairie opencsv, mais attention car ces fonctions changent entre les versions
	 * (entre 3, 4 et 5 les changements entraînent des ruptures de compatibilité)
	 * <br>
	 * Cf. 
	 * <a href="http://opencsv.sourceforge.net/apidocs/com/opencsv/CSVWriter.html">CSVWriter</a>
	 * pour plus d'informations.
	 * @return l'objet {@link CSVWriter}
	 * 
	 */
	public CSVWriter getUnderlyingCSVWriter() { return csvw; }
	
}
