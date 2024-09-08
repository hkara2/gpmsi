/**:encoding=UTF-8:
 * Transformer un message HL7 traditionnel codé en MLLP (Minimum Lower Layer Protocol, qui utilise traditionnellement des délimiteurs '|', '^', '~', etc.)
 * en message HL7 codé en XML. 
 * Exemple :
 * c:\app\gpmsi\v1.1\gpmsi -run fr.gpmsi.hapi.MllpToXml -a:input toto.hl7 -a:output toto_hl7.xml
 */
package fr.gpmsi.hapi;

import ca.uhn.hl7v2.DefaultHapiContext
import ca.uhn.hl7v2.HapiContext
import ca.uhn.hl7v2.model.Message
import ca.uhn.hl7v2.parser.CustomModelClassFactory
import ca.uhn.hl7v2.parser.DefaultXMLParser
import ca.uhn.hl7v2.parser.GenericParser
import ca.uhn.hl7v2.parser.ModelClassFactory
import ca.uhn.hl7v2.parser.XMLParser
import groovy.lang.Binding;
import groovy.lang.Script;

public class MllpToXml extends Script {

  //public MllpToXml() {
  //}

  //public MllpToXml(Binding binding) {
  //  super(binding);
  //}

  @Override
  public Object run() {
    String input = args.input 
    //println "input : $input"
    String output = args.output
    inputText = new File(input).getText() //a noter que cette variable est dans le binding, on ne l'a pas déclarée
    HapiContext hc = new DefaultHapiContext()
    ModelClassFactory mcf = new CustomModelClassFactory("fr.gpmsi.hapi") //avec le package pour trouver notre v25.segment.ZBE
    hc.setModelClassFactory(mcf)
    GenericParser gp = hc.getGenericParser()
    Message msg = gp.parse(inputText)
    //transformation en XML, cf.
    //https://saravanansubramanian.com/hl72xhapiparsemessage/
    XMLParser xp = new DefaultXMLParser()
    String xmlMsg = xp.encode(msg)
    //on rajoute des commentaires de description de tag
    TagDescriptionAdder tda = new TagDescriptionAdder(Ihe2_10TagDescriptions.getDescriptionsByTag())
    xmlMsg = tda.addDescriptionComments(xmlMsg)
    TimestampCommenter tsc = new TimestampCommenter()
    xmlMsg = tsc.commentTimestamps(xmlMsg)
    if (output == null) {
      println()
      println xmlMsg
    }
    else {
      File outf = new File(output)
      outf.write(xmlMsg, 'UTF-8')
    }
    return 0;
  }

}
