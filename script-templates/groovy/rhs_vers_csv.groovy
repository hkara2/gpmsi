/**:encoding=UTF-8:
 * conversion d'une partie des champs RHS vers un fichier csv
 * Paramètres d'entrée :
 * -a:input <fichier_RHS>
 * -a:output <fichier_CSV>
 * H. Karadimas, 2022
 */
import fr.karadimas.gpmsi.StringTable
import fr.karadimas.gpmsi.CsvDestination
import groovy.sql.Sql
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.DayOfWeek
import java.time.temporal.WeekFields

def ageInYears(Date birthdate) {
	Calendar c = Calendar.getInstance()
	def ny = c.get(Calendar.YEAR)
	def nm = c.get(Calendar.MONTH)
	def nd = c.get(Calendar.DAY_OF_MONTH)
	c.setTime(birthdate)
	def by = c.get(Calendar.YEAR)
	def bm = c.get(Calendar.MONTH)
	def bd = c.get(Calendar.DAY_OF_MONTH)
	def age = ny - by
	if (nm < bm) age--
	else if (nm == bm) {
		if (nd < bd) age--
	}
    return age
}

def ageInYears(LocalDate birthdate, LocalDate currentDate) {
    if (birthdate == null || currentDate == null) return -1
    Period.between(birthdate, currentDate).getYears()
}

def nrOfDays(Date start, Date end) {
	if (start == null) return -1
	if (end == null) return -1
	def diff = end.time - start.time
    return diff/(1000*60*60*24)
}

def diffDays(LocalDate start, LocalDate end) {
    if (start == null || end == null) return -1
    Period.between(start, end).getDays()
}

def numSem(LocalDate debutSej, LocalDate debutSem) {
    if (debutSej == null || debutSem == null) return -1
    Period.between(debutSej, debutSem).getDays() / 7
}

rhs {
	name 'Envoi des RHS vers un fichier csv'
	
	input args['input']
	
	output args['output']
	
	onInit {
	    d = new CsvDestination(new File(outputFilePath), "cp1252")
	    //envoi de l'en-tete
	    d.f('ligne')
	    d.f('num_ssr')
	    d.f('num_doss')
	    d.f('date_naiss')
	    d.f('age')
	    d.f('sexe')
	    d.f('date_deb_sej')
	    d.f('date_fin_sej')
	    d.f('duree_sej')
	    d.f('jhwe')
	    d.f('jwe')
	    d.f('annee')
	    d.f('num_sem')
	    d.f('um')
	    d.f('fpp')
	    d.f('mmp')
	    d.f('ae')
	    d.f('dep_habillage')
	    d.f('dep_deplacement')
	    d.f('dep_alimentation')
	    d.f('dep_continence')
	    d.f('dep_comportement')
	    d.f('dep_relation')
	    d.f('nb_da')
	    d.f('das')
	    d.f('nb_csarr')
	    d.f('actes_csarr')
	    d.f('meum')
	    d.f('prov')
	    d.f('msum')
	    d.f('dest')
	    d.f('lundi_du_rhs')
	    d.f('dimanche_du_rhs')
	    d.f('deum')
	    d.f('dsum')
	    d.f('dursejum')
	    d.f('gr_cm')   
	    d.f('gr_code')
	    d.f('gr_subdiv')
	    d.f('gr_severite')
	    d.f('gr_code_ret')
	    d.f('gr_indic_err')
	    d.f('vrhs')
	    d.endRow()
	    
	}
	
	onItem {item->
		def rhs = item.rhs
	    def actes = ""
	    def dnais = rhs.DNAIS.toLocalDate()
	    def linenr = item.linenr
	    def nadl = rhs.txtNADL
	    def nssr = rhs.txtNSSR
	    def vrhs = rhs.txtVRHS
	    def sexe = rhs.txtSEXE
	    def ddsej = rhs.DDSEJ.toLocalDate()
	    def dfsej = rhs.DFSEJ.toLocalDate()
	    def dureesej = diffDays(ddsej, dfsej)
	    def deum = rhs.DEUM.toLocalDate()
	    def meum = rhs.txtMEUM
	    def prov = rhs.txtPROV
	    def num = rhs.txtNUM
	    def dsum = rhs.DSUM.toLocalDate()
	    def dursejum = diffDays(deum, dsum)
	    def jhwe = rhs.txtJHWE
        def jwe = rhs.txtJWE
	    def numsem = numSem(ddsej, deum)
	    def msum = rhs.txtMSUM
	    def dest = rhs.txtDEST
	    def age = ageInYears(dnais, ddsej) //age au debut du sejour
	    def fpp = rhs.txtFPP
	    def mmp = rhs.txtMMP
	    def ae = rhs.txtAE
	    def dhab = rhs.txtDHAB
        def ddep = rhs.txtDDEP
        def dali = rhs.txtDALI
        def dcnt = rhs.txtDCNT
        def dcmp = rhs.txtDCMP
        def drel = rhs.txtDREL
	    def nda = rhs.txtNDA
	    def das = rhs.DA*.txtTDA.join(', ')
	    def ncsa = rhs.txtNCSA
	    def acs = rhs.ACS*.txtCACS.join(', ')	    
	    
	    def weekNr = rhs.txtNSEM[0..1] as int
	    def yearNr = rhs.txtNSEM[2..5] as int
	    //trouver la semaine 1 de l'annee
	    def rhsweek = LocalDate.of(yearNr, 1, 1).with(WeekFields.ISO.weekOfYear(), 1)
	    //lui ajouter le numero de la semaine - 1
	    rhsweek = rhsweek.plusWeeks(weekNr - 1)
	    def rhslun = rhsweek.with(DayOfWeek.MONDAY)
	    def rhsdim = rhsweek.with(DayOfWeek.SUNDAY)
	    
	    def gcm = rhs.GCM ? rhs.txtGCM : "" //Groupage - CM
	    def gcd = rhs.GCD ? rhs.txtGCD : "" //Groupage - Code
	    def gsu = rhs.GSU ? rhs.txtGSU : "" //Groupage - Subdivision
	    def gsv = rhs.GSV ? rhs.txtGSV : "" //Groupage - Sévérité
	    def gcr = rhs.GCR ? rhs.txtGCR : "" //Groupage - Code retour
	    def gie = rhs.GIE ? rhs.txtGIE : "" //Groupage - Indicateur d'erreur


	    d.f(linenr)
	    d.f(nssr)
	    d.f(nadl)
	    d.f(formatLocalAsFrenchDate((LocalDate)(dnais as LocalDate)))
	    d.f(age)
	    d.f(sexe)
	    d.f(formatLocalAsFrenchDate((LocalDate)(ddsej as LocalDate)))
	    d.f(formatLocalAsFrenchDate((LocalDate)(dfsej as LocalDate)))
	    d.f(dureesej)
	    d.f(jhwe)
	    d.f(jwe)
	    d.f(yearNr)
	    d.f(weekNr)
	    d.f(num)
	    d.f(fpp)
	    d.f(mmp)
	    d.f(ae)
	    d.f(dhab)
	    d.f(ddep)
	    d.f(dali)
	    d.f(dcnt)
	    d.f(dcmp)
	    d.f(drel)
	    d.f(nda)
	    d.f(das)
	    d.f(ncsa)
	    d.f(acs)
	    d.f(meum)
	    d.f(prov)
	    d.f(msum)
	    d.f(dest)
	    d.f(formatLocalAsFrenchDate(rhslun as LocalDate))
	    d.f(formatLocalAsFrenchDate(rhsdim as LocalDate))
	    d.f(formatLocalAsFrenchDate(deum as LocalDate))
	    d.f(formatLocalAsFrenchDate(dsum as LocalDate))
	    d.f(dursejum)
	    d.f(gcm)   
	    d.f(gcd)
	    d.f(gsu)
	    d.f(gsv)
	    d.f(gcr)
	    d.f(gie)
	    d.f(vrhs)
	    d.endRow()
	    
	}//onItem
	
	onEnd {
		d.close()
	}
}


