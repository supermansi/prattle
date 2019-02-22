package edu.northeastern.ccs.im;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageTypeTest {
  private String hello;
  private String quit;
  private String broadCast;

  @Before
  public void setUp() {
    hello = "HLO";
    quit = "BYE";
    broadCast = "BCT";
  }

  @Test
  public void testHello() {
    assertEquals(hello,MessageType.HELLO.toString());
  }

  @Test
  public void testQuit() {
    assertEquals(quit,MessageType.QUIT.toString());
  }

  @Test
  public void testBroadCast() {
    assertEquals(broadCast,MessageType.BROADCAST.toString());
  }
}