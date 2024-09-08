/**:encoding=UTF-8:
 * Imprimer la représentation phonétique de l'argument d'entree par l'algorithme PhoneX
 *
 * Exemple :
 * C:\app\gpmsi\v@PROJECT_VERSION@\scripts\bat\phonex.bat -a:text toto
 */
import fr.gpmsi.Phonex

def text = args['text'];

def phonex = Phonex.toPhonex(text)

println "text   : " + text
println "phonex : " + phonex 
