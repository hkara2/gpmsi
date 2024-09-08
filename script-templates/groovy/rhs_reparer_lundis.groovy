/**☺:encoding=UTF-8:
 * Réparer les RHS pour lesquels la sortie s'est faite un lundi, et le SI n'a
 * rien envoyé.
 * Le jour de la sortie, le patient n'est pas présent à minuit, et il faut donc
 * mettre 0 pour la présence de ce jour là.
 * Certains DPI (dont DxCare...) si toutes les présences sont à 0 ne jugent pas
 * utile de produire un RHS, or il en faut un, qui correspond au jour de la
 * sortie qui est enregistrée au minimum dans le dernier RHS.
 * Il faut donner la periode de fin d'export sous forme AAAAMM, par ex. pour
 * février 2024 : -a:periode 202402
 *
 * Exemple d'utilisation :
 * cd C:\Local\e-pmsi\fichiers-rhs-ssr\2023\M08\RHS+VH
 * c:\app\gpmsi\v1.3\gpmsi.bat -script C:\hkgh\gpmsi\script-templates\groovy\rhs_reparer_lundis.groovy -a:input SSR_RHSG_date_SSR_date_RHS_reunion.txt -a:periode 202402
 * Utilisé avec succès le 09/10/2023
 * TODO : changer le calcul des semaines pour que cela marche aussi avec les
 *    séjours à cheval sur 2 années.
 * TODO : si la sortie se fait un lundi qui est hors de la période d'export, la
 *    semaine rajoutée sera "HORS PERIODE". Il faudrait rajouter le mois
 *    d'export considéré pour calculer la fin de la période d'export. Ex : on
 *    exporte 2024M1, la période va du lun 01/01 au dim 28/01 inclus (sem 1 à 4)
 *    Du coup si le patient sort le lun 29/01 ce script va rajouter un RHS qui
 *    se trouvera en semaine 5 d'où un rejet par GENRHA car "HORS PERIODE". Pour
 *    l'instant il faut reperer ces rejets à la main et enlever les lignes
 *    incriminées.
 */
import java.time.*
import java.time.temporal.IsoFields
import fr.karadimas.pmsixml.RhsWriter
import fr.gpmsi.DateUtils
import fr.gpmsi.StringUtils

def renameForOut(inputName) {
    def ext = StringUtils.getExtension(inputName)
    def bareName = StringUtils.removeExtension(inputName, ext)
    return bareName + "-lun-repares" + ext
}

LocalDate now = DateUtils.toLocalDate(new Date())

rhsw = new RhsWriter()

periode = args.periode
anneePeriode = periode[0..3] as int
moisPeriode = periode[4..5] as int
finPeriode = DateUtils.getIsoWeekEndDate(anneePeriode, moisPeriode)
semaineFinPeriode = finPeriode.get( IsoFields.WEEK_OF_WEEK_BASED_YEAR )
//print "semaineFinPeriode $semaineFinPeriode"

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
        //et que la dernière semaine n'est pas après la fin de la période
        //modifier ce rhs et l'envoyer à nouveau avec les nouvelles infos pour la dernière semaine
        if (dersem != null && nsem == dersem - 1 && dersem <= semaineFinPeriode && !nadlOk.contains(nadl)) {
            println "nsem: $nsem , dersem: $dersem"
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

