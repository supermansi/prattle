/** Copyright (c) 2019 Rohan Gori, Aditi Kacheria, Mansi Jain, Joshua Dick. All rights reserved.*/
package edu.northeastern.ccs.im.server;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.northeastern.ccs.im.ChatLogger;
import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MessageType;
import edu.northeastern.ccs.im.model.Message.MsgType;
import edu.northeastern.ccs.im.model.Message.IPType;
import edu.northeastern.ccs.im.model.User;
import edu.northeastern.ccs.im.services.GroupServices;
import edu.northeastern.ccs.im.services.MessageServices;
import edu.northeastern.ccs.im.services.UserServices;

public class CommandService {
  protected static Map<MessageType, ICommandMessage> commandServiceMap = new ConcurrentHashMap<>();

  private static CommandService commandService = null;

  /**
   * Initialises the Command Service map with keys of appropriate handles.
   */
  private CommandService() {
    initialiseCommandServiceMap();
  }

  public static CommandService getInstance() {
    if (commandService == null) {
      commandService =  new CommandService();
      return commandService;
    } else {
      return commandService;
    }
  }

  /**
   * Method to get the command service map.
   *
   * @return the map with all the command services
   */
  protected Map<MessageType, ICommandMessage> getCommandServiceMap() {
    return commandServiceMap;
  }

  /**
   * Method to initialize the command service map.
   */
  private void initialiseCommandServiceMap() {
    commandServiceMap.put(MessageType.PRIVATE, new PrivateMessageCommand());
    commandServiceMap.put(MessageType.GROUP, new GroupMessageCommand());
    commandServiceMap.put(MessageType.CREATE_GROUP, new CreateGroupCommand());
    commandServiceMap.put(MessageType.DELETE_GROUP, new DeleteGroupCommand());
    commandServiceMap.put(MessageType.RETRIEVE_GROUP, new RetrieveGroupMessageCommand());
    commandServiceMap.put(MessageType.RETRIEVE_USER, new RetrieveUserMessageCommand());
    commandServiceMap.put(MessageType.UPDATE_FN, new UpdateFirstNameCommand());
    commandServiceMap.put(MessageType.UPDATE_LN, new UpdateLastNameCommand());
    commandServiceMap.put(MessageType.UPDATE_EM, new UpdateEmailCommand());
    commandServiceMap.put(MessageType.UPDATE_PW, new UpdatePasswordCommand());
    commandServiceMap.put(MessageType.REMOVE_USER, new RemoveUserFromGroupCommand());
    commandServiceMap.put(MessageType.ADD_USER_TO_GRP, new AddUserToGroupCommand());
    commandServiceMap.put(MessageType.DEACTIVATE_USER, new DeactivateUserAccountCommand());
    commandServiceMap.put(MessageType.USER_EXISTS, new DoesUserExistCommand());
    commandServiceMap.put(MessageType.ATTACHMENT, new AttachmentMessageCommand());
    commandServiceMap.put(MessageType.LAST_SEEN, new GetLastSeenCommand());
    commandServiceMap.put(MessageType.SET_GROUP_RESTRICTION, new UpdateGroupRestrictionsCommand());
    commandServiceMap.put(MessageType.LEAVE_GROUP, new LeaveGroupCommand());
    commandServiceMap.put(MessageType.MAKE_ADMIN, new MakeAdminCommand());
    commandServiceMap.put(MessageType.RECALL, new RecallCommand());
    commandServiceMap.put(MessageType.GET_GROUP_USERS, new GetAllUsersInGroupCommand());
    commandServiceMap.put(MessageType.GET_USER_PROFILE, new GetUserProfileCommand());
    commandServiceMap.put(MessageType.DO_NOT_DISTURB, new DNDCommand());
    commandServiceMap.put(MessageType.GET_ALL_GROUP_USER_BELONGS, new GetAllGroupsUserBelongsToCommand());
    commandServiceMap.put(MessageType.GET_MESSAGES_BETWEEN, new GetMessagesBetweenCommand());
    commandServiceMap.put(MessageType.CREATE_THREAD, new CreateThreadCommand());
    commandServiceMap.put(MessageType.POST_ON_THREAD, new PostOnThreadCommand());
    commandServiceMap.put(MessageType.FOLLOW_USER, new FollowUserCommand());
    commandServiceMap.put(MessageType.GET_ALL_THREADS, new GetAllThreadsCommand());
    commandServiceMap.put(MessageType.GET_THREAD_MESSAGES, new GetThreadMessagesCommand());
    commandServiceMap.put(MessageType.UNFOLLOW_USER, new UnfollowUserCommand());
    commandServiceMap.put(MessageType.FORWARD_MESSAGE, new ForwardMessageCommand());
    commandServiceMap.put(MessageType.SECRET_MESSAGE, new SecretMessageCommand());
    commandServiceMap.put(MessageType.REPLY, new ReplyCommand());
    commandServiceMap.put(MessageType.GET_LIST_OF_WIRETAPPED_USERS, new GetListOfWireTappedUserCommand());
    commandServiceMap.put(MessageType.GET_DATA_WIRETAPPED_USER, new GetWiretappedUserDataCommand());
    commandServiceMap.put(MessageType.SET_WIRETAP_MESSAGE, new SetWireTapCommand());
    commandServiceMap.put(MessageType.GET_FOLLOWERS, new GetFollowersCommand());
    commandServiceMap.put(MessageType.GET_FOLLOWING, new GetFollowingCommand());
    commandServiceMap.put(MessageType.SUBSCRIBE_TO_THREAD, new SubscribeToThreadCommand());
    commandServiceMap.put(MessageType.GET_REPLY_CHAIN, new GetReplyChainCommand());
  }

}

/**
 * Method to process a private message.
 */
class PrivateMessageCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    String receiverName = cr.getReceiverName(msg.getText());
    int chatId = Prattle.updateAndGetChatIDFromUserMap(msg.getName(), receiverName);
    Message message = Message.makePrivateMessage(msg.getName(), chatId + " " + msg.getText());
    Prattle.sendPrivateMessage(message, receiverName);
    Map<IPType, String> ipMap = CommandServiceUtils.getIPMapAndSendToAgency(msg,receiverName);
    MessageServices.addMessage(MsgType.PVT, msg.getName(), receiverName, msg.getText(), chatId, ipMap);
  }

}

/**
 * Method to process a group message.
 */
class GroupMessageCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    String receiverName = cr.getReceiverName(msg.getText());
    int chatId = Prattle.updateAndGetChatIDFromGroupMap(receiverName);
    Message message = Message.makeGroupMessage(msg.getName(), chatId + " " + msg.getText());

    String sourceIP = Prattle.getIPFromActiveRunnables(msg.getName());
    String receiverIP = null;
    Map<IPType, String> ipMap = new EnumMap(IPType.class);
    ipMap.put(IPType.SENDERIP, sourceIP);
    ipMap.put(IPType.RECEIVERIP, receiverIP);
    boolean doesGroupMemberHasWireTap = false;

    if (Prattle.sendGroupMessage(message, receiverName)) {
      for (String user : Prattle.listOfWireTappedUsers) {
        if (Prattle.groupToUserMapping.get(receiverName).contains(user)) {
          doesGroupMemberHasWireTap = true;
          break;
        }
      }
      if (Prattle.listOfWireTappedUsers.contains(msg.getName()) || doesGroupMemberHasWireTap) {
        Prattle.sendMessageToAgency(msg, sourceIP, receiverIP);
      }
      MessageServices.addMessage(MsgType.GRP, msg.getName(), receiverName, msg.getText(), chatId, ipMap);
    } else {
      cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Either group does not exist or you " +
              "do not have permission to send message to the group");
    }
  }

}

/**
 * Method to process a create group message.
 */
class CreateGroupCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    GroupServices.createGroup(cr.getReceiverName(msg.getText()), msg.getName());
    List<String> usrList = new ArrayList<>();
    usrList.add(msg.getName());
    Prattle.groupToUserMapping.put(cr.getReceiverName(msg.getText()), usrList);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully created group");
  }

}

/**
 * Method to process a delete group message.
 */
class DeleteGroupCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    GroupServices.deleteGroup(cr.getReceiverName(msg.getText()), msg.getName()); //to do
    Prattle.groupToUserMapping.remove(cr.getReceiverName(msg.getText()));
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully deleted group");
  }

}

/**
 * Method to process a retrieve group message.
 */
class RetrieveGroupMessageCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    cr.retrieveGroupMessagesForGroup(msg);
  }

}

/**
 * Method to process a retrieve user message.
 */
class RetrieveUserMessageCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    cr.retrieveMessagesForUser(msg);
  }

}

/**
 * Method to process an update first name message.
 */
class UpdateFirstNameCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    UserServices.updateFN(msg.getName(), msg.getText().split(" ")[1]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully updated First name");
  }

}

/**
 * Method to process an update last name message.
 */
class UpdateLastNameCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    UserServices.updateLN(msg.getName(), msg.getText().split(" ")[1]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully updated Last name");
  }

}

/**
 * Method to process an update email message.
 */
class UpdateEmailCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    UserServices.updateEmail(msg.getName(), msg.getText().split(" ")[1]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully updated Email");
  }

}

/**
 * Method to process an update password message.
 */
class UpdatePasswordCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    UserServices.updatePassword(msg.getName(), msg.getText().split(" ")[1]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully updated password");
  }

}

/**
 * Method to process a remove user form group message.
 */
class RemoveUserFromGroupCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    GroupServices.removeUserFromGroup(cr.getReceiverName(msg.getText()), msg.getName(), msg.getText().split(" ")[2]);
    Prattle.groupToUserMapping.get(cr.getReceiverName(msg.getText())).remove(msg.getText().split(" ")[2]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully removed User From group");
    Message message = Message.makeGroupMessage(ServerConstants.SERVER_NAME, "Removed user " + msg.getText().split(" ")[2] + " from Group " + cr.getReceiverName(msg.getText()));
    Prattle.sendGroupMessage(message, cr.getReceiverName(msg.getText()));
  }

}

/**
 * Method to process an add user to group message.
 */
class AddUserToGroupCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    GroupServices.addUserToGroup(cr.getReceiverName(msg.getText()), msg.getName(), msg.getText().split(" ")[2]);
    Prattle.groupToUserMapping.get(cr.getReceiverName(msg.getText())).add(msg.getText().split(" ")[2]);
    Message message = Message.makeGroupMessage(ServerConstants.SERVER_NAME, "Added user " + msg.getText().split(" ")[2] + " to Group " + cr.getReceiverName(msg.getText()));
    Prattle.sendGroupMessage(message, cr.getReceiverName(msg.getText()));
  }

}

/**
 * Method to process a deactivate account message.
 */
class DeactivateUserAccountCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Account successfully deleted.");
    UserServices.deleteUser(msg.getName());
    cr.terminate = true;
  }

}

/**
 * Method to process a does user exist message.
 */
class DoesUserExistCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    if (UserServices.userExists(cr.getReceiverName(msg.getText()))) {
      cr.sendMessageToClient(ServerConstants.SERVER_NAME, "This user exists");
    } else {
      cr.sendMessageToClient(ServerConstants.SERVER_NAME, "This user does not exist");
    }
  }

}

/**
 * Method to process a attachment message.
 */
class AttachmentMessageCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    Message message = Message.makeReadAttachmentMessage(msg.getName(), msg.getText());
    Prattle.sendPrivateMessage(message, cr.getReceiverName(message.getText()));
  }

}

/**
 * Method to process a get last seen message.
 */
class GetLastSeenCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    String receiver = cr.getReceiverName(msg.getText());
    Long lastSeen = UserServices.getLastSeen(receiver);
    Date resultDate = new Date(lastSeen);
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, receiver + " last viewed messages at "
            + sdf.format(resultDate));

  }

}

/**
 * Method to process an update group restriction message.
 */
class UpdateGroupRestrictionsCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    String[] split = msg.getText().split(" ");

    GroupServices.changeGroupRestrictions(split[1], msg.getName(), split[2]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Group restriction set successfully");
  }

}

/**
 * Method to process a leave group message.
 */
class LeaveGroupCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    GroupServices.leaveGroup(msg.getName(), cr.getReceiverName(msg.getText()));
    Prattle.groupToUserMapping.get(cr.getReceiverName(msg.getText())).remove(msg.getName());
    Message message = Message.makeGroupMessage(ServerConstants.SERVER_NAME, "Removed user " + msg.getName() + " from Group " + cr.getReceiverName(msg.getText()));
    Prattle.sendGroupMessage(message, cr.getReceiverName(msg.getText()));
  }

}

/**
 * Method to process a make admin message.
 */
class MakeAdminCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    String[] split = msg.getText().split(" ");
    GroupServices.makeAdmin(split[1], msg.getName(), split[2]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Admin added successfully");
  }

}

/**
 * Method to process a recall message.
 */
class RecallCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    if (!Prattle.isUserOnline(cr.getReceiverName(msg.getText())) && MessageServices.recallMessage(msg.getName(), cr.getReceiverName(msg.getText()))) {
      cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Recall Successful");
    } else {
      cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Recall Failed");
    }


  }

}

/**
 * Method to process a get all users in group message
 */
class GetAllUsersInGroupCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    List<String> users = Prattle.groupToUserMapping.getOrDefault(cr.getReceiverName(msg.getText()), new ArrayList<>());
    StringBuilder sb = new StringBuilder();
    for (String s : users) {
      sb.append(s + " ");
    }
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, sb.toString());
  }
}

/**
 * Method to process a get all groups user belongs to message.
 */
class GetAllGroupsUserBelongsToCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    StringBuilder groups = new StringBuilder();
    for (String group : Prattle.groupToUserMapping.keySet()) {
      if (Prattle.groupToUserMapping.get(group).contains(message.getName())) {
        groups.append(group + "\n");
      }
    }
    if (groups.length() == 0) {
      cr.sendMessageToClient(ServerConstants.SERVER_NAME, "You belong to no groups");
    } else {
      cr.sendMessageToClient(ServerConstants.SERVER_NAME, "You belong to the groups: " + groups.toString().trim());
    }
  }
}

/**
 * Method to process a set dnd message.
 */
class DNDCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    boolean status = message.getText().split(" ")[1].equalsIgnoreCase("T");
    cr.setDNDStatus(status);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "DND Status Changed to +" + status + "+ successfully");
    if (status) {
      UserServices.updateLastSeen(cr.getName(), System.currentTimeMillis());
    } else {
      cr.pushNotificationsToClient(message);
    }
  }
}

/**
 * Method to process a get user profile message.
 */
class GetUserProfileCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    StringBuilder sb = new StringBuilder();
    Map<User.UserParams, String> userProfile = UserServices.getUserProfile(message.getName());
    sb.append("Username: " + userProfile.get(User.UserParams.USERNAME) + "\n");
    sb.append("First Name: " + userProfile.get(User.UserParams.FIRSTNAME) + "\n");
    sb.append("Last Name: " + userProfile.get(User.UserParams.LASTNAME) + "\n");
    sb.append("Email: " + userProfile.get(User.UserParams.EMAIL) + "\n");

    cr.sendMessageToClient(ServerConstants.SERVER_NAME, sb.toString());
  }
}

/**
 * Method to process a get messages between message.
 */
class GetMessagesBetweenCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    String[] split = message.getText().split(" ");

    String pattern = "MM/dd/yyyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Date start = new Date();
    Date end = new Date();
    try {
      start = simpleDateFormat.parse(split[2]);
      end = simpleDateFormat.parse(split[3]);
    } catch (ParseException e) {
      ChatLogger.error( e.getMessage());
    }


    List<String> messages = MessageServices.getMessagesBetween(message.getName(), split[1], start.getTime()+"", end.getTime()+"");
    CommandServiceUtils.getMessageFromStringAndSendToClient(cr,messages);
  }
}

/**
 * Method to process a create thread message.
 */
class CreateThreadCommand implements ICommandMessage {
  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    GroupServices.createThread(message.getName(), message.getText().split(" ")[1]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Thread created successfully.");
  }
}

/**
 * Method to process a post on thread message.
 */
class PostOnThreadCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    MessageServices.postMessageToThread(MsgType.TRD, message.getName(), cr.getReceiverName(message.getText()), message.getText());
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Message posted to " + cr.getReceiverName(message.getText()));
    List<String> followers = Prattle.userToFollowerMap.get(message.getName());
    String notify = message.getName() + " posted on the thread " + cr.getReceiverName(message.getText());
    for (String s : followers) {
      Message mess = Message.makePrivateMessage(ServerConstants.SERVER_NAME, notify);
      Prattle.sendPrivateMessage(mess, s);
    }
  }
}

/**
 * Method to process a follow user message.
 */
class FollowUserCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    String following = message.getText().split(" ")[1];
    UserServices.followUser(message.getName(), following);
    if (Prattle.userToFollowerMap.containsKey(following)) {
      List<String> followers = Prattle.userToFollowerMap.get(following);
      followers.add(message.getName());
      Prattle.userToFollowerMap.put(following, followers);
    } else {
      List<String> followers = new ArrayList<>();
      followers.add(message.getName());
      Prattle.userToFollowerMap.put(following, followers);
    }
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "You are now following " + following);
  }
}

/**
 * Method to process a get all threads message.
 */
class GetAllThreadsCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    List<String> threads = GroupServices.retrieveAllThreads();
    StringBuilder sb = new StringBuilder();
    for (String s : threads) {
      sb.append(s + "\n");
    }
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, sb.toString());
  }

}

/**
 * Method to process a get thread messages message.
 */
class GetThreadMessagesCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {

    List<String> messages = MessageServices.retrieveGroupMessages(message.getText().split(" ")[1]);
    StringBuilder sb = new StringBuilder();
    for (String s : messages) {
      sb.append(s + "\n");
    }
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, sb.toString());
  }

}

/**
 * Method to process an unfollow user message.
 */
class UnfollowUserCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    String unfollowing = message.getText().split(" ")[1];
    UserServices.unFollowUser(message.getName(), unfollowing);
    if (Prattle.userToFollowerMap.containsKey(unfollowing)) {
      List<String> followers = Prattle.userToFollowerMap.get(unfollowing);
      followers.remove(message.getName());
    }
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "You are no longer following " + unfollowing);
  }
}

/**
 * Method to process a forward message message.
 */
class ForwardMessageCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    String receiverName = msg.getText().split(" ")[3];
    int chatID = Integer.parseInt(msg.getText().split(" ")[2]); // /fwd r 2 josh
    if(!MessageServices.isSecret(msg.getName(),cr.getReceiverName(msg.getText()),chatID)){
      String fwdMsg;
      if(Prattle.groupToUserMapping.containsKey(receiverName)){
        fwdMsg = MessageServices.getMessageForForwarding(msg.getName(),cr.getReceiverName(msg.getText()),chatID,MsgType.GRP);
      }else{
        fwdMsg = MessageServices.getMessageForForwarding(msg.getName(),cr.getReceiverName(msg.getText()),chatID,MsgType.PVT);
      }
      if(fwdMsg.contains("/reply")){
        new PrivateMessageCommand().run(cr,Message.makePrivateMessage(msg.getName(),"/fwd "+receiverName+" "+fwdMsg.split(" ")[3]));
      }else{
        new PrivateMessageCommand().run(cr,Message.makePrivateMessage(msg.getName(),"/fwd "+receiverName+" "+fwdMsg.split(" ")[2]));
      }
    }else{
      cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Cant forward a secret message");
    }
  }
}

/**
 * Method to process a secret message.
 */
class SecretMessageCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    String receiverName = cr.getReceiverName(msg.getText());
    int chatId = Prattle.updateAndGetChatIDFromUserMap(msg.getName(), receiverName);
    Message message = Message.makeSecretMessageMessage(msg.getName(), chatId + " " + msg.getText());
    Prattle.sendPrivateMessage(message, receiverName);
    Map<IPType, String> ipMap = CommandServiceUtils.getIPMapAndSendToAgency(msg,receiverName);
    MessageServices.addMessage(MsgType.PVT, msg.getName(), receiverName, msg.getText(), chatId, ipMap,true);
  }

}

/**
 * Method to process a reply message.
 */
class ReplyCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    String receiverName = cr.getReceiverName(msg.getText());
    int chatId = Prattle.updateAndGetChatIDFromUserMap(msg.getName(), receiverName);
    Message message = Message.makeReplyMessage(msg.getName(), chatId + " " + msg.getText());
    Prattle.sendPrivateMessage(message, receiverName);
    Map<IPType, String> ipMap = CommandServiceUtils.getIPMapAndSendToAgency(msg,receiverName);
    MessageServices.addMessage(MsgType.PVT, msg.getName(), receiverName, msg.getText(), chatId, ipMap,Integer.parseInt(msg.getText().split(" ")[2]));
  }

}

/**
 * Method to process a get list of wiretapped users message.
 */
class GetListOfWireTappedUserCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    String data = cr.getMessagesInFormat(UserServices.getListOfTappedUsers());
    Prattle.sendMessageToAgency(Message.makePrivateMessage(ServerConstants.SERVER_NAME,data));
  }
}

/**
 * Method to process a get wiretapped user data message.
 */
class GetWiretappedUserDataCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    String data = cr.getMessagesInFormat(MessageServices.getAllDataForCIA(cr.getReceiverName(message.getText())));
    Prattle.sendMessageToAgency(Message.makePrivateMessage(ServerConstants.SERVER_NAME,data));
  }
}

/**
 * Method to process a set wiretap on user message.
 */
class SetWireTapCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    if (cr.getName().equalsIgnoreCase("CIA")) {
      boolean tapStatus = message.getText().split(" ")[2].equalsIgnoreCase("T");
      String receiverName = message.getText().split(" ")[1];
      UserServices.setWireTapStatus(receiverName,tapStatus);
      if(tapStatus){
        Prattle.listOfWireTappedUsers.add(receiverName);
      }
      else{
        Prattle.listOfWireTappedUsers.remove(receiverName);
      }
      cr.sendMessageToClient(ServerConstants.SERVER_NAME, "WireTap Status updated successfully");
    } else {
      cr.sendMessageToClient(ServerConstants.SERVER_NAME, "You are not allowed to use this command !!");
    }
  }
}

/**
 * Method to process a get followers message.
 */
class GetFollowersCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    List<String> followers = UserServices.getFollowers(message.getName());
    StringBuilder sb = new StringBuilder();
    for (String s: followers) {
      sb.append(s + "\n");
    }

    cr.sendMessageToClient(ServerConstants.SERVER_NAME, sb.toString());
  }
}

/**
 * Method to process a get following message.
 */
class GetFollowingCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    List<String> followers = UserServices.getFollowing(message.getName());
    StringBuilder sb = new StringBuilder();
    for (String s: followers) {
      sb.append(s + "\n");
    }

    cr.sendMessageToClient(ServerConstants.SERVER_NAME, sb.toString());
  }
}

/**
 * Method to process a subscribe to thread message.
 */
class SubscribeToThreadCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    GroupServices.subscribeToThread(message.getText().split(" ")[1], message.getName());
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully subscribed to thread");
  }
}

/**
 * Method to process a get reply chain message.
 */
class GetReplyChainCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    List<String> messages = MessageServices.getReplyThread(message.getName(),cr.getReceiverName(message.getText()),Integer.parseInt(message.getText().split(" ")[2]));
    CommandServiceUtils.getMessageFromStringAndSendToClient(cr, messages);
  }

}



