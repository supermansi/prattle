package edu.northeastern.ccs.im.services;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MessageServiceTest {
		
	@Test
	public void testSend() {
		assertTrue(MessageServices.addMessage("PVT", "r", "admin", "hi"));
	}

}
