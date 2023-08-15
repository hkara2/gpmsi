/**:encoding=UTF-8:
 * A partir de deux arguments, l'un contenant un fichier csv avec une colonne NRSA,
 * de numéros de RSA, l'autre étant le fichier TRA,
 * Appeler via drag and drop le script nrsa_attacher_ndoss.groovy
 *
 * Arguments :
 * Un fichier qui doit finir par .tra.txt
 * L'autre fichier est considéré comme étant le fichier avec la colonne NRSA
 * Le script sort s'il n'y a pas ces deux fichiers
 *
 * Le script appelle ensuite tra_attacher_nadl.groovy
 *
 * (C) Harry Karadimas 2023, CHSE
 */
import fr.karadimas.gpmsi.FileUtils

in_a = args['in_a']
in_b = args['in_b']
inputs = [in_a, in_b]

in_tra = inputs.find { new File(it).name.toUpperCase().endsWith('.TRA.TXT') }
if (in_tra == null) throw new Exception("Pas de fichier fourni qui se termine par .tra.txt")

//prendre l'autre fichier pour avoir les numéros de RSA au format csv
in_csv = inputs.find { it != in_tra }
in_csv_bare = new File(FileUtils.stripSuffix(in_csv)).name

inDir = new File(in_csv).parent ?: '.'
scriptDir = new File(scriptPath).parent //le script que l'on appelle se trouve au meme niveau que celui-ci

//appel en tant que script de diff_vidhosp_rhs.groovy
fr.karadimas.gpmsi.Groovy.main(
  '-script', "${scriptDir}\\nrsa_attacher_ndoss.groovy",
  "-a:input_csv", in_csv,
  "-a:input_tra", in_tra,
  "-a:output", "${inDir}\\${in_csv_bare}_nadl.csv")
