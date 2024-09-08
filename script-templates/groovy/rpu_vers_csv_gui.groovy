/**:encoding=UTF-8:
 * Interface Graphique (gui) pour rpu_vers_csv.groovy
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

aa =
'''
+---+------------------------------+
| a |<              b             >|
+---+-------------------------+----+
| lr|<             tr        >| br |
+---+-------------------------+----+
| lc|<             tc        >| bc |
+---+--------------------+----+----+
|   |< s1               >| ok | ca |
+---+--------------------+----+----+
'''
gbl = new AsciiArtGridBagLayout(aa)
swb = new SwingBuilder()

frm = swb.frame(title: "Conversion RPU vers .csv", defaultCloseOperation: JFrame.EXIT_ON_CLOSE) {
    a = label(text: " ")
    b = textField(
        text: "Conversion RPU vers .csv", 
        font: a.font.deriveFont(18.0f).deriveFont(Font.BOLD), 
        horizontalAlignment: JTextField.CENTER, 
        enabled: false
    )
    lr = label(text: "RPUs")
    tr = textField(columns: 40)
    br = button(text: "Choisir", actionPerformed: {e-> 
        def fc = fileChooser(
            dialogTitle: "Choisir le fichier qui contient les RPUs", 
            fileSelectionMode: JFileChooser.FILES_ONLY
        )
        if (fc.showOpenDialog() == JFileChooser.APPROVE_OPTION) {
            tr.text = fc.selectedFile
            tc.text = StringUtils.removeExtension(tr.text) + ".csv"
        }
    })
    lc = label(text: ".csv")
    tc = textField(columns: 40)
    bc = button(text: "Choisir", actionPerformed: {e-> 
        def fc = fileChooser(
            dialogTitle: "Choisir le fichier de destination", 
            fileSelectionMode: JFileChooser.FILES_ONLY
        )
        if (fc.showOpenDialog() == JFileChooser.APPROVE_OPTION) {
            tc.text = fc.selectedFile
        }
    })
    s1 = label(" ")
    ok = button(text: "OK", actionPerformed: {e-> 
        System.out.println("de $tr.text vers $tc.text")
        def here = new File(scriptPath).parent
        Groovy.main(
            "-script", "$here/rpu_vers_csv.groovy", 
            "-a:input", tr.text, 
            "-a:output", tc.text
        )
        System.exit(0)
    })
    ca = button(text: "Annuler", actionPerformed: {e-> System.exit(0)})
}

'a,b,lr,tr,br,lc,tc,bc,s1,ok,ca'.split(',').each {
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
            tr.text = file.canonicalPath
            def bareName = StringUtils.removeExtension(tr.text)
            def destFile = new File(bareName + ".csv")
            tc.text = destFile.canonicalPath
        }
    }
})//setDropTarget

frm.contentPane.setLayout(gbl)
gbl.addAllComponentsTo(frm.contentPane)
frm.pack()
frm.visible = true

