package edu.northeastern.ccs.im.server;

import java.util.HashMap;
import java.util.Map;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.model.Message.IPType;

public class CommandServiceUtils {

  protected static Map<IPType,String> getIPMapAndSendToAgency(Message msg, String receiverName){
    String sourceIP = Prattle.getIPFromActiveRunnables(msg.getName());
    String receiverIP = Prattle.getIPFromActiveRunnables(receiverName);
    Map<edu.northeastern.ccs.im.model.Message.IPType, String> ipMap = new HashMap();
    ipMap.put(edu.northeastern.ccs.im.model.Message.IPType.SENDERIP, sourceIP);
    ipMap.put(edu.northeastern.ccs.im.model.Message.IPType.RECEIVERIP, receiverIP);
    if (Prattle.listOfWireTappedUsers.contains(msg.getName()) || Prattle.listOfWireTappedUsers.contains(receiverName)) {
      Prattle.sendMessageToAgency(msg, receiverName, sourceIP, receiverIP);
    }
    return ipMap;
  }
}
