package fr.karadimas.gpmsi.hapi.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uhn.hl7v2.DefaultHapiContext
import ca.uhn.hl7v2.HapiContext
import ca.uhn.hl7v2.model.Message
import ca.uhn.hl7v2.model.Structure
import ca.uhn.hl7v2.parser.Parser
import ca.uhn.hl7v2.parser.PipeParser

public class HapiTests1 {

  @Test
  public void testPipeParser1() {
    HapiContext ctx = new DefaultHapiContext()
    Parser pars = ctx.getGenericParser()
    Message msg = pars.parse("MSH|^~\\&|||||20230214145150.73+0100||ADT^A04^ADT_A01|7201|T|2.5\r\n");
    def names = msg.getNames();
    println 'Names:'
    names.each { name -> println "'$name'" }
    Structure structMSH = msg.get('MSH')
    println "MSH structure : $structMSH"
    def msgStruct = msg.printStructure()
    println "Message structure : $msgStruct"
  }

}
