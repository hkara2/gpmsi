package fr.gpmsi.tests;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Tests d'analyse des nombres
 */
public class NumberParseTest {

  /**
   * Constructeur simple
   */
  public NumberParseTest() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Méthode main pour appel en tant qu'application
   * @param args Arguments (ignorés ici)
   * @throws ParseException Si erreur d'analyse
   */
  public static void main(String[] args) throws ParseException {
    double d1 = Double.valueOf("2.3E3");
    System.out.println("d1:"+d1);
    NumberFormat fnf = NumberFormat.getInstance();
    Number d2 = fnf.parse("2,3E3");
    System.out.println("d2:"+d2+" (class "+d2.getClass().getName()+")");
    Number d3 = fnf.parse("2,3E-1");
    System.out.println("d3:"+d3+" (class "+d3.getClass().getName()+")");
    Number d4 = fnf.parse("2,3E100");
    System.out.println("d4:"+d4+" (class "+d4.getClass().getName()+")");
    Number d5 = fnf.parse("2,3+E100");//<- ne marche pas, il ne lit que 2.3 !
    System.out.println("d5:"+d5+" (class "+d5.getClass().getName()+")");
  }

}
