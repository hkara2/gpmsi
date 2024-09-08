/**:encoding=UTF-8:
 * Fenetre pour appel de rss_vers_csv.groovy avec des boutons pour rechercher
 * les fichiers.
 *
 * Supporte le "drag and drop" pour definir le fichier d'entree des RSS
 *
 * Appelle le script scripts\rss_vers_csv.groovy qui se trouve a
 * GPMSI_HOME
 *
 * Harry KARADIMAS 2022
 */

import groovy.swing.SwingBuilder

import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.*
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTarget
import java.awt.dnd.DnDConstants
import java.awt.datatransfer.DataFlavor
import org.hkmi2.aagbl.AsciiArtGridBagLayout

import fr.gpmsi.StringUtils

def swb = new SwingBuilder()

//Le dessin en ascii art de la disposition des composants
def aa =
'''
+------------------------------------------+
|            L1                            |
+------+-----------------------------+-----+
|  L2  |<        T2                 >| B2  |
+------+-----------------------------+-----+
|  L3  |<        T3                 >| B3  |
+------+-----------------------------+-----+
|                     S1                   |
+----------------------------+-------+-----+
|             S2             |  BC   | BO  |
+----------------------------+-------+-----+
'''

//Ici on crée le AsciiArtGridBagLayout avec les contraintes qui sont dans le dessin en Ascii Art
def gbl = new AsciiArtGridBagLayout(aa)

//Définition du panel principal, mais en mettant chaque composant créé dans une variable de script (donc accessible dans tout le script)
def mainPanel = {
   cPnl = swb.panel(layout : gbl) {
      L1 = label(
        text: 'Transformation fichier RSS vers .csv',
        horizontalAlignment : JLabel.CENTER
      )
      L2 = label(
        text: 'Chemin du fichier des RSS',
        horizontalAlignment : JLabel.RIGHT
      )
      T2 = textField()
      B2 = button(
        text: 'Choisir',
        actionPerformed: {
            jfc = fileChooser(dialogTitle: 'Choisir le fichier des RSS')
            //jfc.setFileFilter(new FileNameExtensionFilter("Fichier csv", "csv"));
            if (T2.text.length() > 0) {
                File f = new File(T2.text)
                if (f.exists()) jfc.setCurrentDirectory(f.parentFile)
            }
            if (jfc.showOpenDialog() == JFileChooser.APPROVE_OPTION) {
                //println jfc.selectedFile
                T2.text = jfc.selectedFile.path
            }
        }
      )
      L3 = label(
        text: 'Chemin du fichier .csv a créer',
        horizontalAlignment : JLabel.RIGHT
      )
      T3 = textField()
      B3 = button(
        text: 'Choisir',
        actionPerformed: {
            jfc = fileChooser(dialogTitle: 'Choisir le fichier .csv de destination')
            if (T3.text.length() > 0) {
                File f = new File(T3.text)
                if (f.exists()) jfc.setCurrentDirectory(f.parentFile)
            }
            if (jfc.showOpenDialog() == JFileChooser.APPROVE_OPTION) {
                T3.text = jfc.selectedFile.path
            }
        }
      )
      S1 = label(text: ' ')
      S2 = label(text: ' ')
      BC = button(
          text: 'Annuler',
          actionPerformed: { System.exit(0) }
      )
      BO = button(
          text: 'OK',
          actionPerformed: {
              def cheminSrc = T2.text
              def cheminDest = T3.text
              def pmsixml_home = System.getenv('GPMSI_HOME')
              def gpmsi = pmsixml_home + '\\gpmsi.bat'
              def script = pmsixml_home + '\\scripts\\groovy\\rss_vers_csv.groovy'
              //Construction de la commande
              def cmd = "\"$gpmsi Groovy -script $script -a:input \"$cheminSrc\" -a:output \"$cheminDest\" \""
              ProcessBuilder pb =
                  new ProcessBuilder("cmd", "/c", gpmsi, "-script", script, "-a:input", cheminSrc, "-a:output", cheminDest);
              println "Lancement du process : ${pb.command.join(' ')}"
              def proc = pb.start()
              def reader = new BufferedReader( new InputStreamReader(proc.getInputStream()) ) 
              while ((line = reader.readLine()) != null) { println(line) }
              int result = proc.waitFor()
              def errMsg = (result == 0) ? "" : "avec l'erreur $result"
              JOptionPane.showMessageDialog(null, "Execution terminée $errMsg");
              System.exit(0)
          }
      )

      //on applique maintenant les contraintes à chaque composant
      def components = [L1:L1,L2:L2,T2:T2,B2:B2,L3:L3,T3:T3,B3:B3,S1:S1,S2:S2,BC:BC,BO:BO]
      components.each {name, component-> gbl.setConstraints(name, component) }
      gbl.setWeightx('L2,L3,B2,B3', 0) //correction du poids de ces 4 composants pour qu'ils ne prennent pas de place superflue
   }//cPnl = swb.panel

   //Gestion du drag-and-drop
    //cf. https://stackoverflow.com/questions/811248/how-can-i-use-drag-and-drop-in-swing-to-get-file-path
    cPnl.setDropTarget(new DropTarget() {
        public synchronized void drop(DropTargetDropEvent evt) {
            evt.acceptDrop(DnDConstants.ACTION_COPY);
            def droppedFiles = evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            droppedFiles.each { file ->
                if (file.isDirectory()) return;
                T2.text = file.canonicalPath
                def bareName = StringUtils.removeExtension(T2.text)
                def destFile = new File(bareName + ".csv")
                T3.text = destFile.canonicalPath
            }
        }
    })//setDropTarget
}//def mainPanel


def myframe = swb.frame(
        title : 'Transformation .csv -> fichcomp',
        location : [100, 100],
        size : [800, 300],
        defaultCloseOperation : WindowConstants.EXIT_ON_CLOSE
) {
  mainPanel()
}

myframe.setVisible(true)

