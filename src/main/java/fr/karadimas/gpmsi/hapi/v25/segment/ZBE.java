package fr.karadimas.gpmsi.hapi.v25.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v25.datatype.CWE;
import ca.uhn.hl7v2.model.v25.datatype.EI;
import ca.uhn.hl7v2.model.v25.datatype.ID;
import ca.uhn.hl7v2.model.v25.datatype.TS;
import ca.uhn.hl7v2.model.v25.datatype.XON;
import ca.uhn.hl7v2.parser.ModelClassFactory;

/**
 * Segment "custom" pour le ZBE utilisé en France.
 * Code issu du code exemple de HAPI.
 * @author hkaradimas
 *
 */
public class ZBE extends AbstractSegment {

  @SuppressWarnings("unused")
  private static final long SerialVersionUID = 1;
  
  public ZBE(Group parent, ModelClassFactory factory) {
    super(parent, factory);
    // By convention, an init() method is created which adds
    // the specific fields to this segment class
    init(factory);
  }

  private void init(ModelClassFactory factory) {
      try {
          /*
           * For each field in the custom segment, the add() method is
           * called once. In this example, new Object[]{ getMessage() }, "Pet Name(s)", there are two fields in
           * the ZPI segment.
           * 
           * See here for information on the arguments to this method:
           * http://hl7api.sourceforge.net/base/apidocs/ca/uhn/hl7v2/model/AbstractSegment.html#add%28java.lang.Class,%20boolean,%20int,%20int,%20java.lang.Object[],%20java.lang.String%29
           */
          //SEQ | LEN | DT | Usage | Card. | HL7 TBL# | ELEMENT NAME | IHE FR
          //1 427 EI R [0..*] Movement ID
          add(EI.class, true, 0, 99, new Object[]{ getMessage() }, "Identifiant du mouvement (EI)");
          //2 26 TS R [1..1] Start of Movement Date/Time
          add(TS.class, true, 1, 26, new Object[]{ getMessage() }, "Start of Movement Date/Time");
          //3 26 TS X [0..1] End of Movement Date/Time
          add(TS.class, true, 1, 26, new Object[]{ getMessage() }, "End of Movement Date/Time");
          //4 6 ID R [1..1] Action on the Movement
          add(ID.class, true, 1, 6, new Object[]{ getMessage() }, "Action on the Movement");
          //5 1 ID R [1..1] Indicator 'Historical movement'
          add(ID.class, true, 1, 1, new Object[]{ getMessage() }, "Indicator 'Historical movement'");
          //6 3 ID C [0..1] Original trigger event code
          add(ID.class, true, 1, 3, new Object[]{ getMessage() }, "Original trigger event code");
          //7 250 XON C [0..1] Ward of medical responsibility in the period starting with this movement *
          add(XON.class, true, 1, 250, new Object[]{ getMessage() }, "Ward of medical responsibility in the period starting with this movement");
          //8 250 XON C [0..1] Ward of care responsibility in the period starting with this movement *
          add(XON.class, true, 1, 250, new Object[]{ getMessage() }, "Ward of care responsibility in the period starting with this movement");
          //9 3 CWE R [1..1] IHE ZBE-9 Nature of this movement *
          add(CWE.class, true, 1, 3, new Object[]{ getMessage() }, "IHE ZBE-9 Nature of this movement");
          
          //add(ST.class, true, 0, 100, new Object[]{ getMessage() }, "Pet Name(s)");
          //add(NM.class, false, 1, 4, new Object[]{ getMessage() }, "Shoe Size");
      } catch (HL7Exception e) {
          log.error("Erreur lors de la creation du ZBE.", e);
      }
      
   }
      
  /**
   * This method must be overridden. The easiest way is just to return null.
   */
  @Override
  protected Type createNewTypeWithoutReflection(int field) {
    return null;
  }
   
  /**
   * Create an accessor for each field
   */
  public CWE[] getNatureOfMovement() throws HL7Exception {
    return getTypedField(9, new CWE[0]);
  }
   
  /**
   * Create an accessor for each field
   */
  public EI[] getMovementIdentifier() throws HL7Exception {
    return getTypedField(0, new EI[0]);
  }
  
}
