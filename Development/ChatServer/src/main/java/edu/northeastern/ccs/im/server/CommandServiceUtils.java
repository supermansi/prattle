/** Copyright (c) 2019 Rohan Gori, Aditi Kacheria, Mansi Jain, Joshua Dick. All rights reserved.*/
package edu.northeastern.ccs.im.server;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.model.Message.IPType;

/**
 * This class represents the command service utilities.
 */

public class CommandServiceUtils {

  private CommandServiceUtils(){
    //Static class is not supposed to be instantiated
  }

  /**
   * Method to get the map of ip addresses to users and send info to agency.
   *
   * @param msg the message to search for
   * @param receiverName the receiver name
   * @return the mapping of ip addresses to users
   */
  protected static Map<IPType, String> getIPMapAndSendToAgency(Message msg, String receiverName) {
    String sourceIP = Prattle.getIPFromActiveRunnables(msg.getName());
    String receiverIP = Prattle.getIPFromActiveRunnables(receiverName);
    Map<edu.northeastern.ccs.im.model.Message.IPType, String> ipMap = new EnumMap(IPType.class);
    ipMap.put(edu.northeastern.ccs.im.model.Message.IPType.SENDERIP, sourceIP);
    ipMap.put(edu.northeastern.ccs.im.model.Message.IPType.RECEIVERIP, receiverIP);
    if (Prattle.listOfWireTappedUsers.contains(msg.getName()) || Prattle.listOfWireTappedUsers.contains(receiverName)) {
      Prattle.sendMessageToAgency(msg, sourceIP, receiverIP);
    }
    return ipMap;
  }

  /**
   * Method to get a message from a string and send to another user.
   *
   * @param cr the client runnable
   * @param messages the list of messages to send
   */
  protected static void getMessageFromStringAndSendToClient(ClientRunnable cr, List<String> messages) {
    for (String conv : messages) {

      String messageWithHiddenType = cr.filterMessageToHideType(conv);
      String[] arr = messageWithHiddenType.split(" ");

      Message sendMessage = Message.makePrivateMessage(arr[1], arr[0] + " " + messageWithHiddenType.substring(arr[0].length() + arr[1].length() + arr[2].length() + arr[3].length() + 4));
      cr.enqueueMessage(sendMessage);
    }
  }
}
