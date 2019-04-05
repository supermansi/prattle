package edu.northeastern.ccs.im.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.northeastern.ccs.im.model.MessageToUserMap;

public class MessageToUserMapTest {
	
	MessageToUserMap messageUserMap = new MessageToUserMap();
	
	@Test
	public void testID() {
		messageUserMap.setId(1);
		assertEquals(1, messageUserMap.getId());
	}
	
	@Test
	public void testMessageID() {
		messageUserMap.setMsgID(123);
		assertEquals(123, messageUserMap.getMsgID());
	}
	
	@Test
	public void testReceiverID() {
		messageUserMap.setReceiverID(234);
		assertEquals(234, messageUserMap.getReceiverID());
	}

	@Test
	public void testReceiverIP (){
		messageUserMap.setReceiverIP("00000000");
		assertEquals("00000000", messageUserMap.getReceiverIP());
	}

}
