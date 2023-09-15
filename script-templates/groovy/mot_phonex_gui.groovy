/**☺:encoding=UTF-8: (l'encodage DOIT ETRE en UTF-8 !)
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
import fr.karadimas.gpmsi.StringUtils
import fr.karadimas.gpmsi.Groovy

import static fr.karadimas.gpmsi.StringUtils.isTrimEmpty

import fr.karadimas.gpmsi.Phonex

aa =
'''
+---+------------------------------+
| a |<              b             >|
+---+------------------------------+
|lm |<             tm             >|
+---+------------------------------+
|                  bp              |
+---+------------------------------+
|lp |<             tp             >|
+---+------------------------------+
'''

//abreviations des composants :
//a : Label pour mettre un espage, b : TextField pour mettre le titre
//lm : Label pour mot, tm : TextField pour mot
//bp : Button pour Phonex
//tp : TextField pour Phonex (il vaut mieux un TextField car on peut copier le résultat)

gbl = new AsciiArtGridBagLayout(aa)
swb = new SwingBuilder();

frm = swb.frame(title: "Conversion d'un mot en représentation phonétique Phonex", defaultCloseOperation: JFrame.EXIT_ON_CLOSE) {
    a = label(text: " ")
    b = textField(
        text: "Conversion mot en Phonex",
        font: a.font.deriveFont(18.0f).deriveFont(Font.BOLD),
        horizontalAlignment: JTextField.CENTER,
        enabled: false
    )
    ttr = '''<html>
Mot à transformer en Phonex
</html>
'''
    lm = label(text: "Mot ", horizontalAlignment: SwingConstants.LEFT, toolTipText: ttr)
    tm = textField(columns: 40, toolTipText: ttr)
    bp = button(
        text: "Phonex",
        toolTipText: 'Transformer en Phonex (https://sqlpro.developpez.com/cours/soundex/)',
        actionPerformed: {e->  tp.text = Phonex.toPhonex(tm.text) } //executé si on appuie sur le bouton
    )
    lp = label(text: "Phonex ")
    tp = textField(columns: 40)
}
frm.rootPane.defaultButton = bp //astuce qui appuie sur le bouton bp lorsqu'on appuie sur la touche entrée

//mettre les contraines calculées à chaque composant
'a,b,lm,tm,bp,lp,tp'.split(',').each {
	gbl.setConstraints(it, binding[it])
}

frm.contentPane.setLayout(gbl)
gbl.addAllComponentsTo(frm.contentPane)
frm.pack()
frm.visible = true

