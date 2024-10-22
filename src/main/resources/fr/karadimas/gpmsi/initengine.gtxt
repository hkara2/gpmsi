/*:encoding=UTF-8:*/
package fr.gpmsi
import fr.gpmsi.pmsixml.FszGroup
import fr.gpmsi.StringTableRow
import fr.gpmsi.CsvRow
import fr.gpmsi.DbfRow

//
// Scripts Groovy utilisés lors de l'initialisation de l'environnement de script.
// Déclare les "métaclasses" qui permettent la syntaxe d'accès particulière
// aux membres 
//

/**
 * Spécialisation de l'accès à une propriété pour FszGroup, de manière à accéder 
 * facilement au contenu (rum.txtFOO), aux enfants (rum.ZA), etc.
 */
FszGroup.metaClass.getProperty = { String propName ->
      def pmeta = FszGroup.metaClass.getMetaProperty(propName)
      if (pmeta) {
        return pmeta.getProperty(delegate) //look for traditional prop
      }
      if (propName.startsWith('txt')) {
        propName = propName[3..-1]
        def ch = delegate.getChild(propName)
        if (ch == null) {
          lg.error("Propriete dynamique non trouvee '${propName}'")
          return ""
        }
        return ch.getValueAsText() //et non plus juste getValue()
      }
      def ch = delegate.getChild(propName)
      if (ch == null) return null
      if (ch.field) return ch //c'est un champ, on le renvoie directement
      //c'est un groupe ; regarder si c'est un conteneur
      if (ch.container) {
	      //renvoyer la liste de tous les enfants qui ont le même nom que la propriété
	      def clst = ch.getChildren()
	      if (clst == null) return null
	      def sel = clst.findAll { it.meta.stdName == propName }
	      return sel
      }
      //c'est un groupe simple, pas un conteneur. Le renvoyer normalement
      return ch
    }

/**
 * Spécialisation de l'accès à une propriété pour StringTableRow, de manière
 * à avoir accès à une valeur via le nom de colonne
 */
StringTableRow.metaClass.getProperty = {String propName ->
    def meta = StringTableRow.metaClass.getMetaProperty(propName)
    if (meta) {
        meta.getProperty(delegate)
    } else {
        delegate.getValue(propName) 
    }
} //StringTableRow.metaClass.getProperty

/**
 * Spécialisation de l'accès à une propriété en écriture pour StringTableRow, de manière
 * à attribuer une valeur via le nom de colonne
 */
StringTableRow.metaClass.setProperty = {String propName, String value ->
    def meta = StringTableRow.metaClass.getMetaProperty(propName)
    if (meta) {
        meta.setProperty(delegate, value)
    } else {
        delegate.setValue(propName, value) 
    }
} //StringTableRow.metaClass.setProperty

/**
 * Spécialisation de l'accès à une propriété pour CsvRow, de manière
 * à avoir accès à une valeur via le nom de colonne
 */
CsvRow.metaClass.getProperty = {String propName ->
    def meta = CsvRow.metaClass.getMetaProperty(propName)
    if (meta) {
        meta.getProperty(delegate)
    } else {
        delegate.getValue(propName) 
    }
} //CsvRow.metaClass.getProperty

/**
 * Spécialisation de l'accès en écriture à une propriété pour CsvRow, de manière
 * à stocker une valeur via le nom de colonne
 */
CsvRow.metaClass.setProperty = {String propName, String value ->
    def meta = CsvRow.metaClass.getMetaProperty(propName)
    if (meta) {
        meta.setProperty(delegate, value)
    } else {
        delegate.setValue(propName, value) 
    }
} //CsvRow.metaClass.setProperty

/**
 * Spécialisation de l'accès à une propriété pour XlRow, de manière
 * à avoir accès à une valeur (String) via le nom de colonne
 */
XlRow.metaClass.getProperty = {String propName ->
    def meta = XlRow.metaClass.getMetaProperty(propName)
    if (meta) {
        meta.getProperty(delegate)
    } else {
        delegate.getStringValue(propName)
    }
} //XlRow.metaClass.getProperty

/**
 * Spécialisation de l'accès à un objet contenu dans un champ pour DbfRow, de manière
 * à avoir accès à une valeur (Object) via le nom de colonne
 */
DbfRow.metaClass.getProperty = {String propName ->
    def meta = DbfRow.metaClass.getMetaProperty(propName)
    if (meta) {
        meta.getProperty(delegate)
    } else {
        delegate.getValue(propName)
    }
} //DbfRow.metaClass.getProperty

