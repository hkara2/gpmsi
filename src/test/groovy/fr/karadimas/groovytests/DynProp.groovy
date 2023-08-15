package fr.karadimas.groovytests

/**
 * Dynamic properties, backed by a map.
 * See http://mrhaki.blogspot.fr/2010/01/groovy-goodness-override-getproperty.html
 */
class DynProp
extends Expando
{

  def myMap = [FOO: 2, BAR: 4, BAZ: 6] //these values are accessed as if they were properties

  static {
    DynProp.metaClass.getProperty = { String propName ->
      def meta = DynProp.metaClass.getMetaProperty(propName)
      if (meta) {
        return meta.getProperty(delegate) //look for traditional prop
      } 
      else if (myMap[propName] == null) {
        return "Not found : $propName"
      }
      else {
        return myMap[propName]
      }
    }
  }
    
  def RANT = 20 //traditional property, will be accessible also
    
}
