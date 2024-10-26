/**:encoding=UTF-8:
 * A partir d'un fichier 'in' de Druide, qui est en JSON, refaire un fichier de RSS
 * Le nom du fichier en entrée est dans le parametre "-a:input"
 * Le nom du fichier de sortie est le nom du fichier d'entrée avec ajouté "_reconstitution.rss"
 *
 * Ex : c:\app\gpmsi\gpex -script "C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\fr\gpmsi\druides\refaire_fichier_rss.groovy" -a:input panoramix\910019447.2023.3.SEJOURS.SEJOURS.56f12aae-708b-4f0c-bd7a-f03495e7bff3.in
 * 
 */
package fr.gpmsi.druides
import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()

input = args['input']

entrees = jsonSlurper.parse(new File(input))

fichierRss = new FileOutputStream(input + '_reconstitution.rss')

FSEP = System.properties['line.separator']

entrees.dossiers.rums.each {
    it.each {
        it.each {
            fichierRss << it << FSEP 
        }
    }
}

fichierRss.close()
