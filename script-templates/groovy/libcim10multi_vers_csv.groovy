/**☺:encoding=UTF-8:
 *
 * Convertir un fichier "LIBCIM10MULTI.TXT" fournir par l'ATIH avec le kit de
 * nomenclature, vers un fichier .csv (format français) équivalent, avec des en-têtes
 * CDE;C2;C3;C4;LIBC;LIBL
 * utilisées par les scripts de gpmsi.
 * Il faut aussi donner les dates de début et de fin de validité au format aaaammjj.
 *
 * Arguments (tous obligatoires) :
 * -a:input chemin_du_fichier_entree
 * -a:debval date_debut_validite_au_format_aaaammjj
 * -a:finval date_fin_validite_au_format_aaaammjj
 *
 * Le fichier de destination a comme nom "cim10-p<date_debut_validite>-<date_fin_validite>.csv
 * et comme encodage "windows-1252"
 *
 * Exemple :
 * C:\app\gpmsi\v@PROJECT_VERSION@\gpmsi -script C:\app\gpmsi\v@PROJECT_VERSION@\scripts\groovy\libcim10multi_vers_csv.groovy -a:input LIBCIM10MULTI.TXT -a:debval 20240301 -a:finval 20250301
 */
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import fr.gpmsi.StringTable
import fr.gpmsi.CsvDestination

/** format de date ISO BASIC, analyse le format aaaammjj */
df = DateTimeFormatter.BASIC_ISO_DATE

// prendre paramètres d'entrée et vérifier les arguments
input = args.input
if (input == null) throw new Exception("parametre de fichier d'entree manquant")
debval = args.debval
if (debval == null) throw new Exception("parametre de date de debut manquant")
if (!(debval ==~ /^\d\d\d\d\d\d\d\d/)) throw new Exception("la date de debut fournie est incorrecte")
dateDebVal = LocalDate.parse(debval, df) //enverra une exception si le format est incorrect
finval = args.finval
if (finval == null) throw new Exception("parametre de date de debut manquant")
if (!(finval ==~ /^\d\d\d\d\d\d\d\d/)) throw new Exception("la date de fin fournie est incorrecte")
dateFinval = LocalDate.parse(finval, df) //enverra une exception si le format est incorrect

//lire LIBCIM10MULTI.TXT
libcimSt = new StringTable()
String[] enTetes = ['CDE','C2','C3','C4','LIBC','LIBL'] //en-têtes utilisées par les scripts gpmsi
inFile = new File(input)
libcimSt.readFrom(inFile, enTetes, 'windows-1252', '|' as char) //lire le fichier LIBCIM10MULTI.TXT en rajoutant les en-têtes gpmsi

//écrire le resultat en .csv avec encodage windows et séparateur ';'
outFile = new File(inFile.canonicalFile.parent, "cim10_p${debval}-${finval}.csv")
csvDest = new CsvDestination(outFile, 'windows-1252', ';' as char)
libcimSt.writeTo(csvDest)


