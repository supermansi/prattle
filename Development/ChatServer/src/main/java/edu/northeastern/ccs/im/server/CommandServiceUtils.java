package edu.northeastern.ccs.im.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.model.Message.IPType;

public class CommandServiceUtils {

  protected static Map<IPType, String> getIPMapAndSendToAgency(Message msg, String receiverName) {
    String sourceIP = Prattle.getIPFromActiveRunnables(msg.getName());
    String receiverIP = Prattle.getIPFromActiveRunnables(receiverName);
    Map<edu.northeastern.ccs.im.model.Message.IPType, String> ipMap = new HashMap();
    ipMap.put(edu.northeastern.ccs.im.model.Message.IPType.SENDERIP, sourceIP);
    ipMap.put(edu.northeastern.ccs.im.model.Message.IPType.RECEIVERIP, receiverIP);
    if (Prattle.listOfWireTappedUsers.contains(msg.getName()) || Prattle.listOfWireTappedUsers.contains(receiverName)) {
      Prattle.sendMessageToAgency(msg, sourceIP, receiverIP);
    }
    return ipMap;
  }

  protected static void getMessageFromStringAndSendToClient(ClientRunnable cr, List<String> messages) {
    for (String conv : messages) {

      String messageWithHiddenType = cr.filterMessageToHideType(conv);
      String[] arr = messageWithHiddenType.split(" ");

      Message sendMessage = Message.makePrivateMessage(arr[1], arr[0] + " " + messageWithHiddenType.substring(arr[0].length() + arr[1].length() + arr[2].length() + arr[3].length() + 4));
      cr.enqueueMessage(sendMessage);
    }
  }
}
