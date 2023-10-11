/**☺:encoding=UTF-8:
 * Réparer les RHS pour lesquels la sortie s'est faite un lundi, et le SI n'a
 * rien envoyé.
 * Exemple :
 * cd C:\Local\e-pmsi\fichiers-rhs-ssr\2023\M08\RHS+VH
 * c:\app\gpmsi\v1.3\gpmsi.bat -script C:\hkgh\gpmsi\script-templates\groovy\rhs_reparer_lundis.groovy -a:input SSR_RHSG_date_SSR_date_RHS_reunion.txt
 * Utilisé avec succès le 09/10/2023
 * TODO : changer le calcul des semaines pour que cela marche aussi avec les
 *    séjours à cheval sur 2 années.
 */
import java.time.*
import java.time.temporal.IsoFields
import fr.karadimas.pmsixml.RhsWriter
import fr.karadimas.gpmsi.DateUtils
import fr.karadimas.gpmsi.StringUtils

def renameForOut(inputName) {
    def ext = StringUtils.getExtension(inputName)
    def bareName = StringUtils.removeExtension(inputName, ext)
    return bareName + "-lun-repares" + ext
}

LocalDate now = DateUtils.toLocalDate(new Date())

rhsw = new RhsWriter()

nadlOk = []

//1 - reperer nadl + numeros de semaine pour lesquels on n'a rien trouvé.
//    marquer le numéro de semaine précédent

rhs {
    input args.input
    onInit {}
    onItem {item->
        def rhs = item.rhs
        def ddsej = DateUtils.toLocalDate(rhs.DDSEJ.toDate())
        def dfsej = rhs.DFSEJ.toDate() ? DateUtils.toLocalDate(rhs.DFSEJ.toDate()) : null
        def nadl = rhs.txtNADL
        def nsem = (rhs.NSEM.toInt()/10000) as int
        def dersem = dfsej?.get ( IsoFields.WEEK_OF_WEEK_BASED_YEAR )
        //println "$rhs;$ddsej;$dfsej;$nadl;$nsem;$dersem"
        if (dersem == nsem) nadlOk << nadl //si on a bien la dernière semaine, le séjour est OK
    }
    onEnd {}
}

//2 - on refait un tour et on répare si besoin.
//    reparcourir les RHS. Si on tombe sur un nadl + numéro de semaine marqué,
//    générer le RHS suivant, qui a N° de semaine + 1 et tous les jours à 0.
//    on enlève aussi tous les actes CCAM et CSARR
rhs {
    input args.input
    output renameForOut(args.input)
    onInit {
        outf = new FileOutputStream(outputFilePath)
    }
    onItem {item->
        def rhs = item.rhs
        def ddsej = DateUtils.toLocalDate(rhs.DDSEJ.toDate())
        def dfsej = rhs.DFSEJ.toDate() ? DateUtils.toLocalDate(rhs.DFSEJ.toDate()) : null
        def nadl = rhs.txtNADL
        def nsem = (rhs.NSEM.toInt()/10000) as int
        def dersem = dfsej?.get ( IsoFields.WEEK_OF_WEEK_BASED_YEAR )
        sb = new StringBuffer()
        rhsw.writeRhs(rhs, sb)
        //println "$rhs;$ddsej;$dfsej;$nadl;$nsem;$dersem"
        outf << sb << "\r\n"
        //si on est sur l'avant-dernière semaine, et que la dernière semaine est manquante,
        //modifier ce rhs et l'envoyer à nouveau avec les nouvelles infos
        if (dersem != null && nsem == dersem - 1 && !nadlOk.contains(nadl)) {
            rhs.NSEM.value = ("000000" + (rhs.NSEM.toInt() + 10000))[-6..-1]
            rhs.JHWE.value = "00000"
            rhs.JWE.value = "00"
            rhs.getChild('ACS').removeChildren() //enlever tous les actes CSARR
            rhs.getChild('ACC').removeChildren() //enlever tous les actes CCAM
            sb = new StringBuffer()
            rhsw.writeRhs(rhs, sb)
            outf << sb << "\r\n"
        }
    }
    onEnd {
        outf.close()
    }
}

