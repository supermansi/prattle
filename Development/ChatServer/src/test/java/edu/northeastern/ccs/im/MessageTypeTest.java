package edu.northeastern.ccs.im;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for the MessageType enum.
 */
public class MessageTypeTest {
  private String hello;
  private String quit;
  private String broadCast;

  /**
   * Set up for tests.
   */
  @Before
  public void setUp() {
    hello = "HLO";
    quit = "BYE";
    broadCast = "BCT";
  }

  /**
   * Test to return MessageType.HELLO.
   */
  @Test
  public void testHello() {
    assertEquals(hello,MessageType.HELLO.toString());
  }

  /**
   * Test to return MessageType.QUIT.
   */
  @Test
  public void testQuit() {
    assertEquals(quit,MessageType.QUIT.toString());
  }

  /**
   * Test to return MessageType.BROADCAST.
   */
  @Test
  public void testBroadCast() {
    assertEquals(broadCast,MessageType.BROADCAST.toString());
  }
}