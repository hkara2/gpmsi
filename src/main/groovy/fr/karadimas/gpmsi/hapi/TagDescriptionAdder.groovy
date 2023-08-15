package fr.karadimas.gpmsi.hapi;


/**
 * Pour chaque tag XML trouvé dans la carte, ajouter le commentaire derrière.
 * Si le commentaire commence par '/', c'est qu'il faut l'ajouter après le tag
 * fermant, pas le tag ouvrant.
 * Utile pour ajouter de la description dans les tags HL7
 * @author hkaradimas
 *
 */
class TagDescriptionAdder {
  Map<String, String> descriptionsByTag;
  
  TagDescriptionAdder(Map<String, String> descriptionsByTag) {
    this.descriptionsByTag = descriptionsByTag
  }
  
  String addDescriptionComments(String xml) {
    descriptionsByTag.each {k, v->
      String tag = "<$k>"
      String comment = "<!--$v-->"
      if (v.startsWith("/")) {
        tag = "</$k>"
        comment = '<!--' + v[1..-1] + '-->'
      }
      xml = xml.replace(tag, tag+' '+comment)
    }
    return xml
  }
  
}