package edu.northeastern.ccs.im.server;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.northeastern.ccs.im.server.ClientTimer;

public class ClientTimerTest {
  ClientTimer clientTimer;

  @Before
  public void setUp() {
    clientTimer = new ClientTimer();
  }

  @Test
  public void testIsBehindFalse() {
    assertEquals(false,clientTimer.isBehind());
  }

  @Test
  public void testUpdateAfterActivity() {
    clientTimer.updateAfterActivity();
    assertEquals(false,clientTimer.isBehind());
  }

  @Test
  public void testUpdateAfterInitialization() {
    clientTimer.updateAfterInitialization();
    assertEquals(false,clientTimer.isBehind());
  }
}
