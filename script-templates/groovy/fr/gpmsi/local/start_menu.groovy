/**☺:encoding=UTF-8:
 * Affiche le fichier html qui est passé en paramètre,
 * et pour chaque lien l'ouvre avec le awt.Desktop
 * Cela permet de gérer une collection de raccourcis Windows en utilisant une
 * page html locale, ce qu'on pouvait faire avec Internet Explorer (mais qui
 * n'est plus possible avec Edge)
 * Les liens locaux partent depuis le répertoire ~/.gpmsi
 * Les liens absolus sont de la forme file:///C:/repertoire
 * Le fichier ou répertoire cible doit exister.
 * Arguments :
 * -a:input CHEMIN_FICHIER_ENTREEE  (obligatoire)
 * -a:taille LARGEURxHAUTEUR        (optionnel, exemple : 800x600)
 *
 * Exemple :
 * c:\app\gpmsi\v@PROJECT_VERSION@\gpmsi.bat -script c:\app\gpmsi\v@PROJECT_VERSION@\groovy\fr\gpmsi\local\start_menu.groovy -a:input %USERPROFILE%\.gpmsi\start_menu.html
 */
package fr.gpmsi.local
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.html.HTMLDocument
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener
import javax.swing.*
import java.awt.*

import org.apache.commons.io.FileUtils //distribue avec gpmsi

class StartMenuFrame
  extends JFrame
  implements HyperlinkListener 
{
    JLabel statusLabel = new JLabel(" ")

    StartMenuFrame(String menuFilePath)
      throws IOException 
    {
        super("start_menu ~/.gpmsi")
        setIconImage(new ImageIcon(getClass().getResource("/fr/gpmsi/gpmsi-logo-transparent-48.png")).getImage())
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
        File startMenuFile = new File(menuFilePath)
        JEditorPane jep = new JEditorPane(startMenuFile.toURI().toURL())
        jep.setContentType("text/html")
        jep.setEditable(false)
        jep.addHyperlinkListener(this)
        JPanel panel = new JPanel()
        add(panel)
        panel.setLayout(new BorderLayout())
        panel.add(new JScrollPane(jep), BorderLayout.CENTER)
        panel.setOpaque(true)
        panel.add(statusLabel, BorderLayout.SOUTH)
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
      URL u = e.getURL()
      if (u == null) {
          statusLabel.setText("Lien invalide")
      }
      else {
          File f = FileUtils.toFile(u)
          if (f.exists()) {
              switch (e.getEventType()) {
                  case HyperlinkEvent.EventType.ACTIVATED:
                      statusLabel.setText("Ouverture de $f")
                      Desktop desktop = Desktop.getDesktop();
                      desktop.open(f);
                  break;
                  case HyperlinkEvent.EventType.ENTERED:
                      statusLabel.setText("$f")
                  break;
                  case HyperlinkEvent.EventType.EXITED:
                      statusLabel.setText(" ")
                  break;
              }//switch
          }
          else {
              statusLabel.setText("Fichier n'existe pas")
          }//if (f.exists...
      }//if (u == null ...
    }
}//class

w = 0
h = 0
if (args.taille != null) {
    def szStr = args.taille
    def sepPos = szStr.toUpperCase().indexOf('X')
    if (sepPos > 0) {
        w = Integer.valueOf(szStr.substring(0, sepPos))
        h = Integer.valueOf(szStr.substring(sepPos+1))
    }
}
frm = new StartMenuFrame(args.input)
if (w > 0 && h > 0) {
    frm.setSize(w, h)
}
else frm.pack()
frm.setVisible(true)
