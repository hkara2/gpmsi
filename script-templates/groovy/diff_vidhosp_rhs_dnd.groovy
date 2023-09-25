/**☺:encoding=UTF-8:
 * Difference entre dossiers VIDHOSP et RHS
 * Appel via drag and drop
 *
 * Arguments :
 * Un fichier qui doit commencer par VIDHOSP_SSR
 * Un fichier qui doit commencer par SSR_RHS
 * Le script sort s'il n'y a pas ces deux fichiers
 * 
 * Le script appelle ensuite diff_vidhosp_rhs.groovy
 *
 * (C) Harry Karadimas 2022, CHSE
 */

in_a = args['in_a']
in_b = args['in_b']
inputs = [in_a, in_b]

in_vh = inputs.find { new File(it).name.startsWith('VIDHOSP_SSR') }
if (in_vh == null) throw new Exception("Pas de fichier fourni qui commence par VIDHOSP_SSR")

in_rhs = inputs.find { new File(it).name.startsWith('SSR_RHS') }
if (in_rhs == null) throw new Exception("Pas de fichier fourni qui commence par SSR_RHS")

inDir = new File(in_rhs).parent
scriptDir = new File(scriptPath).parent //le script que l'on appelle se trouve au meme niveau que celui-ci
//appel en tant que script de diff_vidhosp_rhs.groovy
fr.karadimas.gpmsi.Groovy.main(
  '-script', "${scriptDir}\\diff_vidhosp_rhs.groovy", 
  "-a:input_vh", in_vh, 
  "-a:input_rhs", in_rhs, 
  "-a:output", "${inDir}\\diff_vh_rhs.txt")

