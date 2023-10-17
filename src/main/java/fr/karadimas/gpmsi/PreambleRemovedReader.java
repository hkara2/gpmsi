package fr.karadimas.gpmsi;

import java.io.BufferedReader;
import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Reader qui ne renvoie des données que après qu'une ligne vide a été rencontrée.
 * Utile pour sauter les premières lignes d'un fichier csv, par exemple.
 * @author hkaradimas
 *
 */
public class PreambleRemovedReader
extends FilterReader 
{

  BufferedReader br;
  boolean preambleSkipped = false;
  
  /**
   * Constructeur qui prend un Reader en entrée
   * @param in le Reader
   */
  public PreambleRemovedReader(Reader in) {
    super(in);
    br = new BufferedReader(in);
  }

  private void skipPreamble()
      throws IOException 
  {
    String line = "?";
    while (line != null && line.trim().length() > 0) {
      line = br.readLine();
    }
    preambleSkipped = true;          
  }
  
  @Override
  public int read() throws IOException {
    if (!preambleSkipped) skipPreamble();    
    return br.read();
  }
  
  @Override
  public int read(char[] cbuf)
      throws IOException 
  {
    return read(cbuf, 0, cbuf.length);
  }
  
  @Override
  public int read(char[] cbuf, int off, int len) 
      throws IOException 
  {
    int rc = 0;
    wloop: while (len > 0) {
      int c = read();
      if (c >= 0) {
        rc++; //caractère valide lu
        cbuf[off++] = (char) c;
      }
      else {
        if (rc == 0) rc = -1; //si on a atteint la fin de fichier directement sans rien avoir lu, retourner -1
        break wloop; //fin de fichier atteinte, sortir de la boucle
      }
      len--;
      //S ystem.out.print("["+((char)c)+"]");
    }
    return rc;
  }
 
  @Override
  public long skip(long n) 
      throws IOException 
  {
    long rc = 0;
    while (n > 0) {
      int c = read();
      if (c < 0) break; //fin de fichier atteinte, sortir
      rc++;
      n--;
    }
    return rc;
  }
  
  @Override
  public boolean ready()
      throws IOException 
  {
    if (!preambleSkipped) skipPreamble();
    return br.ready();
  }

  /**
   * Officiellement nous ne supportons pas "mark"
   */
  @Override
  public boolean markSupported() {
    return false;
  }
  
  /**
   * Non supporté, envoie une exception
   * @throws IOException Envoyée systématiquement
   */
  @Override
  public void mark(int readAheadLimit)
      throws IOException 
  {
    throw new IOException("mark not supported");
  }
  
  /**
   * Non supporté, envoie une exception
   * @throws IOException Envoyée systématiquement 
   */
  @Override
  public void reset()
      throws IOException 
  {
    throw new IOException("reset() not supported");
  }

  /**
   * Fermeture
   */
  @Override
  public void close()
      throws IOException 
  {
    br.close();
  }
  
}
