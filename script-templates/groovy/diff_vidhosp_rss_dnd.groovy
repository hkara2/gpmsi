/**☺:encoding=UTF-8:
 * Difference entre dossiers VIDHOSP et RSS
 * Appel via drag and drop
 *
 * Arguments :
 * Un fichier qui doit commencer par VIDHOSP_MCO
 * Un fichier qui doit commencer par MCO_RSS
 * Le script sort s'il n'y a pas ces deux fichiers
 * 
 * Le script appelle ensuite diff_vidhosp_rss.groovy
 *
 * (C) Harry Karadimas 2022, CHSE
 */

in_a = args['in_a']
in_b = args['in_b']
inputs = [in_a, in_b]

in_vh = inputs.find { new File(it).name.startsWith('VIDHOSP_MCO') }
if (in_vh == null) throw new Exception("Pas de fichier fourni qui commence par VIDHOSP_MCO")

in_rss = inputs.find { new File(it).name.startsWith('MCO_RSS') }
if (in_rss == null) throw new Exception("Pas de fichier fourni qui commence par MCO_RSS")

inDir = new File(in_rss).parent
scriptDir = new File(scriptPath).parent
//appel en tant que script de diff_vidhosp_rss.groovy
fr.karadimas.gpmsi.Groovy.main(
  '-script', "${scriptDir}\\diff_vidhosp_rss.groovy", 
  "-a:input_vh", in_vh, 
  "-a:input_rss", in_rss, 
  "-a:output", "${inDir}\\diff_vh_rss.txt")

