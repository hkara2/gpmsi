package fr.karadimas.gpmsi.tests;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

import fr.karadimas.gpmsi.DateUtils;

public class DatesTests {

  private void doStartDateTest(int year, int expectedYear, int expectedMonth, int expectedDay) {
    LocalDate expectedStartDate = LocalDate.of(expectedYear, expectedMonth, expectedDay);
    LocalDate startDate = DateUtils.getIsoWeekStartDate(year);
    assertEquals("Iso date calc error", expectedStartDate, startDate);    
  }
  
  private void doEndDateTest(int year, int month, int expectedYear, int expectedMonth, int expectedDay) {
    LocalDate expectedEndDate = LocalDate.of(expectedYear, expectedMonth, expectedDay);
    LocalDate endDate = DateUtils.getIsoWeekEndDate(year, month);
    assertEquals("Iso date calc error", expectedEndDate, endDate);    
  }
  
  /**
   * https://myweb.ecu.edu/mccartyr/isowdcal.html
   */
  @Test
  public void testIsoStartDateFor2024() {
    doStartDateTest(2024, 2024, 1, 1);
  }

  /**
   * https://myweb.ecu.edu/mccartyr/isowdcal.html
   */
  @Test
  public void testIsoStartDateFor2023() {
    doStartDateTest(2023, 2023, 1, 2);
  }

  /**
   * https://myweb.ecu.edu/mccartyr/isowdcal.html
   */
  @Test
  public void testIsoStartDateFor2022() {
    doStartDateTest(2022, 2022, 1, 3);
  }

  /**
   * https://myweb.ecu.edu/mccartyr/isowdcal.html
   */
  @Test
  public void testIsoStartDateFor2021() {
    doStartDateTest(2021, 2021, 1, 4);
  }

  /**
   * https://myweb.ecu.edu/mccartyr/isowdcal.html
   */
  @Test
  public void testIsoStartDateFor2020() {
    doStartDateTest(2020, 2019, 12, 30);
  }

  /**
   * https://myweb.ecu.edu/mccartyr/isowdcal.html
   */
  @Test
  public void testIsoStartDateFor2019() {
    doStartDateTest(2019, 2018, 12, 31);
  }

  /**
   * https://myweb.ecu.edu/mccartyr/isowdcal.html
   */
  @Test
  public void testIsoStartDateFor2015() {
    doStartDateTest(2015, 2014, 12, 29);
  }
  
  /* TESTS basés sur calendrier GENRHA 2024 :
M1 = premier mois de l'année, du lundi 01/01/2024 au dimanche 28/01/2024 (Semaine 1 à 4)
M2 = premier mois de l'année, du lundi 01/01/2024 au dimanche 03/03/2024 (Semaine 1 à 9)
M3 = premier mois de l'année, du lundi 01/01/2024 au dimanche 31/03/2024 (Semaine 1 à 13)
M4 = premier mois de l'année, du lundi 01/01/2024 au dimanche 28/04/2024 (Semaine 1 à 17)
M5 = premier mois de l'année, du lundi 01/01/2024 au dimanche 02/06/2024 (Semaine 1 à 22)
M6 = premier mois de l'année, du lundi 01/01/2024 au dimanche 30/06/2024 (Semaine 1 à 26)
M7 = premier mois de l'année, du lundi 01/01/2024 au dimanche 28/07/2024 (Semaine 1 à 30)
M8 = premier mois de l'année, du lundi 01/01/2024 au dimanche 01/09/2024 (Semaine 1 à 35)
M9 = premier mois de l'année, du lundi 01/01/2024 au dimanche 29/09/2024 (Semaine 1 à 39)
M10 = premier mois de l'année, du lundi 01/01/2024 au dimanche 03/11/2024 (Semaine 1 à 44)
M11 = premier mois de l'année, du lundi 01/01/2024 au dimanche 01/12/2024 (Semaine 1 à 48)
M12 = premier mois de l'année, du lundi 01/01/2024 au dimanche 29/12/2024 (Semaine 1 à 52)
   */
  
  @Test
  public void testIsoEndDateFor202401() {
    doEndDateTest(2024, 1, 2024, 1, 28);
  }
  
  @Test
  public void testIsoEndDateFor202402() {
    doEndDateTest(2024, 2, 2024, 3, 3);
  }
  
  @Test
  public void testIsoEndDateFor202403() {
    doEndDateTest(2024, 3, 2024, 3, 31);
  }
  
  @Test
  public void testIsoEndDateFor202404() {
    doEndDateTest(2024, 4, 2024, 4, 28);
  }
  
  @Test
  public void testIsoEndDateFor202405() {
    doEndDateTest(2024, 5, 2024, 6, 2);
  }
  
  @Test
  public void testIsoEndDateFor202406() {
    doEndDateTest(2024, 6, 2024, 6, 30);
  }
  
  @Test
  public void testIsoEndDateFor202407() {
    doEndDateTest(2024, 7, 2024, 7, 28);
  }
  
  @Test
  public void testIsoEndDateFor202408() {
    doEndDateTest(2024, 8, 2024, 9, 1);
  }
  
  @Test
  public void testIsoEndDateFor202409() {
    doEndDateTest(2024, 9, 2024, 9, 29);
  }
  
  @Test
  public void testIsoEndDateFor202410() {
    doEndDateTest(2024, 10, 2024, 11, 3);
  }
  
  @Test
  public void testIsoEndDateFor202411() {
    doEndDateTest(2024, 11, 2024, 12, 1);
  }
  
  @Test
  public void testIsoEndDateFor202412() {
    doEndDateTest(2024, 12, 2024, 12, 29);
  }
  

}
