/**:encoding=UTF-8: (l'encodage DOIT ETRE en UTF-8 !)
 * Interface Graphique (gui) pour rpu_et_bo_vers_xlsx.groovy
 */

import javax.swing.*
import java.awt.*
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTarget
import java.awt.dnd.DnDConstants
import java.awt.datatransfer.DataFlavor

import org.hkmi2.aagbl.AsciiArtGridBagLayout
import groovy.swing.SwingBuilder
import fr.gpmsi.StringUtils
import fr.gpmsi.Groovy

import static fr.gpmsi.StringUtils.isTrimEmpty

aa =
'''
+---+------------------------------+
| a |<              b             >|
+---+-------------------------+----+
|lr |<             tr        >| br |
+---+-------------------------+----+
|lb |<             tb        >| bb |
+---+-------------------------+----+
|lx |<             tx        >| bx |
+---+--------------------+----+----+
|   |< s1               >| ok | ca |
+---+--------------------+----+----+
'''

//abreviations des composants :
//a : Label pour mettre un espage, b : TextField pour mettre le titre
//lr : Label pour RPU, tr : TextField pour RPU, br : Button pour RPU
//lb : Label pour BO, tb : TextField pour BO, bb : Button pour BO
//lx : Label pour eXcel, tx : TextField pour eXcel, bx : Button pour eXcel
//s1 : Label vide pour faire l'espacement, ok : Button pour "OK", ca : Button pour "Cancel"

gbl = new AsciiArtGridBagLayout(aa)
swb = new SwingBuilder()

frm = swb.frame(title: "Conversion RPU + BO vers .xlsx", defaultCloseOperation: JFrame.EXIT_ON_CLOSE) {
    a = label(text: " ")
    b = textField(
        text: "Conversion RPU + informations patient vers .xlsx",
        font: a.font.deriveFont(18.0f).deriveFont(Font.BOLD),
        horizontalAlignment: JTextField.CENTER,
        enabled: false
    )
    ttr = '''<html>
Chemin du fichier<br>
qui contient les RPUs (fichier \'<i>OSCOUR</i>\' au format XML)
</html>
'''
    lr = label(text: "RPUs ", horizontalAlignment: SwingConstants.LEFT, toolTipText: ttr)
    tr = textField(columns: 40, toolTipText: ttr)
    br = button(text: "Choisir", toolTipText: 'Choisir le fichier', actionPerformed: {e->
        def fc = fileChooser(
            dialogTitle: "Choisir le fichier qui contient les RPUs",
            fileSelectionMode: JFileChooser.FILES_ONLY
        )
        if (!isTrimEmpty(tr.text)) fc.currentDirectory = new File(tr.text).parentFile //reprendre répertoire si possible
        else if (!isTrimEmpty(tb.text)) fc.currentDirectory = new File(tb.text).parentFile //sinon reprendre répertoire du fichier BO si possible
        if (fc.showOpenDialog() == JFileChooser.APPROVE_OPTION) {
            tr.text = fc.selectedFile
            tx.text = StringUtils.removeExtension(tr.text) + ".xlsx"
        }
    })
    lb = label(text: "Données patient ")
    tb = textField(columns: 40, toolTipText: '''<html>
Fichier texte avec séparateur <b>tabulation</b>, contenant<br>
les données de séjour extraites de la gestion patient via bo (Business Objects)<br>
ou un autre outil.
</html>''')
    bb = button(text: "Choisir", toolTipText: 'Choisir le fichier', actionPerformed: {e->
        def fc = fileChooser(
            dialogTitle: "Choisir le fichier des données patient et séjour",
            fileSelectionMode: JFileChooser.FILES_ONLY
        )
        if (!isTrimEmpty(tb.text)) fc.currentDirectory = new File(tb.text).parentFile //reprendre répertoire si possible
        else if (!isTrimEmpty(tr.text)) fc.currentDirectory = new File(tr.text).parentFile //sinon reprendre répertoire du fichier RPU si possible
        if (fc.showOpenDialog() == JFileChooser.APPROVE_OPTION) {
            tb.text = fc.selectedFile
        }
    })
    lx = label(text: "Fichier Excel produit ")
    tx = textField(columns: 40, toolTipText: 'Chemin du fichier Excel (format .xlsx) qui sera produit')
    bx = button(text: "Choisir", toolTipText: 'Choisir le chemin du fichier de destination', actionPerformed: {e->
        def fc = fileChooser(
            dialogTitle: "Choisir le chemin du fichier Excel de destination",
            fileSelectionMode: JFileChooser.FILES_ONLY
        )
        if (!isTrimEmpty(tx.text)) fc.currentDirectory = new File(tx.text).parentFile //reprendre répertoire si possible
        if (fc.showOpenDialog() == JFileChooser.APPROVE_OPTION) {
            tx.text = fc.selectedFile
        }
    })
    s1 = label(" ")
    ok = button(text: "OK", toolTipText: 'Exécuter le script', actionPerformed: {e->
        System.out.println("de $tr.text vers $tx.text")
        Thread worker = new Thread() {
          public void run() {
            try {
                s1.text = "Traitement en cours ..."
                ok.enabled = false
                def here = new File(scriptPath).parent
                Groovy.main(
                    "-script", "$here/rpu_et_bo_vers_xlsx.groovy",
                    "-a:input", tr.text,
                    "-a:bo", tb.text,
                    "-a:output", tx.text
                )
            } catch (InterruptedException ex) {
                //interruption ignorée
            }
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                //resultLabel.setText("Ready");
                //good.setEnabled(true);
                s1.text = " "
                JOptionPane.showMessageDialog(null, "Traitement terminé.");
                System.exit(0)
              }
            })
          }
        }
        worker.start(); // démarrage d'un thread de travail séparé.
    })
    ca = button(text: "Annuler", toolTipText: 'Ne rien faire et fermer cette fenêtre', actionPerformed: {e-> System.exit(0)})
}

'a,b,lr,tr,br,lx,tx,bx,lb,tb,bb,s1,ok,ca'.split(',').each {
	gbl.setConstraints(it, binding[it])
}

//Gestion du drag-and-drop
//cf. https://stackoverflow.com/questions/811248/how-can-i-use-drag-and-drop-in-swing-to-get-file-path
frm.setDropTarget(new DropTarget() {
    public synchronized void drop(DropTargetDropEvent evt) {
        evt.acceptDrop(DnDConstants.ACTION_COPY);
        def droppedFiles = evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
        droppedFiles.each { file ->
            if (file.isDirectory()) return;
            //on essaie de deviner si c'est le fichier des RPUs qui est glissé, ou le fichier BO.
            //pour que cela marche il faut que le fichier des RPUs commence par 'rpu'
            if (file.name.toLowerCase().startsWith('rpu')) {
                //c'est le fichier des RPUs
                tr.text = file.canonicalPath
                def bareName = StringUtils.removeExtension(tr.text)
                def destFile = new File(bareName + ".xlsx")
                tx.text = destFile.canonicalPath
            }
            else {
                //ce doit donc être le fichier BO si ce n'est pas celui des RPUs
                tb.text = file.canonicalPath
            }
        }
    }
})//setDropTarget

frm.contentPane.setLayout(gbl)
gbl.addAllComponentsTo(frm.contentPane)
frm.pack()
frm.visible = true

