/**☺:encoding=UTF-8:
 * Montrer des informations sur l'environnement d'exécution (nom de la machine,
 * version de java, etc.) dans une fenêtre GUI.
 *
 * Exemples :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\infos_environnement.groovy
 */

import javax.swing.*
import java.awt.*

import org.hkmi2.aagbl.AsciiArtGridBagLayout
import groovy.swing.SwingBuilder
import fr.gpmsi.StringUtils
import fr.gpmsi.Groovy

def textWrap(str, size) {
    sb = new StringBuilder()
    int c = 0
    for (int i = 0; i < str.length(); i++) {
        if (c >= size) { sb.append("\n"); c = 0 }
        sb.append(str.charAt(i))
        c++
    }
    sb.toString()
}

msFont = new Font("Courier New", Font.PLAIN, 10) //Font.MONOSPACED ne marche pas bien

swb = new SwingBuilder();

frm = swb.frame(title: "Informations d'environnement GPMSI",
                size: [800, 600],
                defaultCloseOperation: JFrame.EXIT_ON_CLOSE) {
                    borderLayout(vgap: 5)

                    /*
    println "Variable d'environnement GPMSI_HOME : $gpmsi_home"
    println "Version de Groovy : $groovy_ver"
    println "Version de Java : $java_ver"
    println "Variables prédéfinies :"
    println "  args : $args"
    println "  flags : $flags"
    println "  lg : $lg"
    println "  scriptPath : $scriptPath"
    println "  nl : $nl"
    println "  userHome : $userHome"
    println "  frenchDateFormat : ${frenchDateFormat.toPattern()}"
    println "  pmsiDateFormat : ${pmsiDateFormat.toPattern()}"
    println "  isoDateFormat : ${isoDateFormat.toPattern()}"
                    */
    scrollPane {
        panel(constraints: BorderLayout.CENTER,
                border: compoundBorder([emptyBorder(10), titledBorder('Informations d\'Environnement')])) {
            tableLayout {
                tr {
                    td {
                        label 'Variable d\'environnement GPMSI_BASE :    '
                    }
                    td {
                        textField text:  "${System.getenv('GPMSI_BASE') ?: '<Non défini>'}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Variable d\'environnement GPMSI_HOME :    '
                    }
                    td {
                        textField text: "${System.getenv('GPMSI_HOME') ?: '<Non défini>'}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Version de java :'
                    }
                    td {
                        textField text:  "${System.getProperty('java.version')}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Répertoire de java :'
                    }
                    td {
                        textField text:  "${System.getProperty('java.home')}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Editeur de java :'
                    }
                    td {
                        textField text:  "${System.getProperty('java.vendor')}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Adresse de l\'éditeur de java :'
                    }
                    td {
                        textField text:  "${System.getProperty('java.vendor.url')}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Architecture de l\'O.S. :'
                    }
                    td {
                        textField text:  "${System.getProperty('os.arch')}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Nom de l\'O.S. :'
                    }
                    td {
                        textField text:  "${System.getProperty('os.name')}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Version de l\'O.S. :'
                    }
                    td {
                        textField text:  "${System.getProperty('os.version')}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Architecture processeur :'
                    }
                    td {
                        textField text:  "${System.getenv('PROCESSOR_ARCHITECTURE') ?: '<Non défini>'}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Identifiant processeur :'
                    }
                    td {
                        textField text:  "${System.getenv('PROCESSOR_IDENTIFIER') ?: '<Non défini>'}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Niveau processeur :'
                    }
                    td {
                        textField text:  "${System.getenv('PROCESSOR_LEVEL') ?: '<Non défini>'}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Révision processeur :'
                    }
                    td {
                        textField text:  "${System.getenv('PROCESSOR_REVISION') ?: '<Non défini>'}", editable: false
                    }
                }

                tr {
                    td {
                        label 'Répertoire courant :'
                    }
                    td {
                        textField text:  "${System.getProperty('user.dir')}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Répertoire de l\'utilisateur :'
                    }
                    td {
                        textField text:  "${System.getProperty('user.home')}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Nom de l\'utilisateur :'
                    }
                    td {
                        textField text:  "${System.getProperty('user.name')}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Nom de l\'ordinateur :'
                    }
                    td {
                        textField text:  "${System.getenv('COMPUTERNAME') ?: '<Non défini>'}", editable: false
                    }
                }
                tr {
                    td {
                        label 'Classpath (sur plusieurs lignes de 200 caractères) :'
                    }
                    td {
                        textArea text: textWrap("${System.getProperty('java.class.path')}", 200),
                                 font: msFont,
                                 editable: false
                    }
                }
            }//tableLayout

        }//panel
    }//scrollPane
    //a = label(text: " ")
    panel(constraints: BorderLayout.SOUTH) {
        button('OK', actionPerformed: {e->  frm.dispose()})
    }
} //frm

//frm.pack()
frm.visible = true
