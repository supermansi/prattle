package edu.northeastern.ccs.im.server;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the methods in the ClientTimer class.
 */
public class ClientTimerTest {
  ClientTimer clientTimer;

  /**
   * Set up for testing.
   */
  @Before
  public void setUp() {
    clientTimer = new ClientTimer();
  }

  /**
   * Test for the isBehind method.
   */
  @Test
  public void testIsBehindFalse() {
    assertEquals(false,clientTimer.isBehind());
  }

  /**
   * Test for the updateAfterActivity method.
   */
  @Test
  public void testUpdateAfterActivity() {
    clientTimer.updateAfterActivity();
    assertEquals(false,clientTimer.isBehind());
  }

  /**
   * Test for the updateAfterInitialization method.
   */
  @Test
  public void testUpdateAfterInitialization() {
    clientTimer.updateAfterInitialization();
    assertEquals(false,clientTimer.isBehind());
  }
}
