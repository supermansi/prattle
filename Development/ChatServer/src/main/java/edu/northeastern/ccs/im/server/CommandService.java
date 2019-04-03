package edu.northeastern.ccs.im.server;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MessageType;
import edu.northeastern.ccs.im.model.Message.MsgType;
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
      return new CommandService();
    } else {
      return commandService;
    }
  }

  protected Map<MessageType, ICommandMessage> getCommandServiceMap() {
    return commandServiceMap;
  }

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
  }

}

class PrivateMessageCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    String receiverId = cr.getReceiverName(msg.getText());
    Prattle.sendPrivateMessage(msg, receiverId);
    MessageServices.addMessage(MsgType.PVT, msg.getName(), receiverId, msg.getText());
  }

}

class GroupMessageCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    String receiverId = cr.getReceiverName(msg.getText());
    if (Prattle.sendGroupMessage(msg, receiverId)) {
      MessageServices.addMessage(MsgType.GRP, msg.getName(), receiverId, msg.getText());
    } else {
      cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Either group does not exist or you " +
              "do not have permission to send message to the group");
    }
  }

}

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

class DeleteGroupCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    GroupServices.deleteGroup(cr.getReceiverName(msg.getText()), msg.getName()); //to do
    Prattle.groupToUserMapping.remove(cr.getReceiverName(msg.getText()));
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully deleated group");
  }

}

class RetrieveGroupMessageCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    cr.retrieveGroupMessagesForGroup(msg);
  }

}

class RetrieveUserMessageCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    cr.retrieveMessagesForUser(msg);
  }

}

class UpdateFirstNameCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    UserServices.updateFN(msg.getName(), msg.getText().split(" ")[1]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully updated First name");
  }

}

class UpdateLastNameCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    UserServices.updateLN(msg.getName(), msg.getText().split(" ")[1]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully updated Last name");
  }

}

class UpdateEmailCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    UserServices.updateEmail(msg.getName(), msg.getText().split(" ")[1]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully updated Email");
  }

}

class UpdatePasswordCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    UserServices.updatePassword(msg.getName(), msg.getText().split(" ")[1]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully updated password");
  }

}

class RemoveUserFromGroupCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    GroupServices.removeUserFromGroup(cr.getReceiverName(msg.getText()), msg.getName(), msg.getText().split(" ")[2]);
    Prattle.groupToUserMapping.get(cr.getReceiverName(msg.getText())).remove(msg.getText().split(" ")[2]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully removed User From group");
  }

}

class AddUserToGroupCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    GroupServices.addUserToGroup(cr.getReceiverName(msg.getText()), msg.getName(), msg.getText().split(" ")[2]);
    Prattle.groupToUserMapping.get(cr.getReceiverName(msg.getText())).add(msg.getText().split(" ")[2]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully Added User to group");
    Message message = Message.makeGroupMessage(msg.getName(), "Added user" + msg.getText().split(" ")[2] + "to Group" + cr.getReceiverName(msg.getText()));
    Prattle.sendGroupMessage(message,cr.getReceiverName(msg.getText()));
  }

}

class DeactivateUserAccountCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Account successfully deleted.");
    UserServices.deleteUser(msg.getName());
    cr.terminate = true;
  }

}

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

class AttachmentMessageCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    Message message = Message.makeReadAttachmentMessage(msg.getName(), msg.getText());
    Prattle.sendPrivateMessage(message, cr.getReceiverName(message.getText()));
  }

}

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


class UpdateGroupRestrictionsCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    String[] split = msg.getText().split(" ");

    GroupServices.changeGroupRestrictions(split[1], msg.getName(), split[2]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Group restriction set successfully");
  }

}

class LeaveGroupCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    GroupServices.leaveGroup(msg.getName(), cr.getReceiverName(msg.getText()));
    Prattle.groupToUserMapping.get(cr.getReceiverName(msg.getText())).remove(msg.getName());
  }

}

class MakeAdminCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message msg) throws SQLException {
    String[] split = msg.getText().split(" ");
    GroupServices.makeAdmin(split[1], msg.getName(), split[2]);
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "Admin added successfully");
  }

}

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

class GetAllGroupsUserBelongsToCommand implements ICommandMessage {
  StringBuilder groups = new StringBuilder();

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
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

class DNDCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    boolean status = message.getText().split(" ")[1].equalsIgnoreCase("T");
    cr.sendMessageToClient(ServerConstants.SERVER_NAME, "DND Status Changed to +" + status + "+ successfully");
    if (status) {
      UserServices.updateLastSeen(cr.getName(), System.currentTimeMillis());
    } else {
      cr.pushNotificationsToClient(message);
    }
  }
}

class GetUserProfileCommand implements ICommandMessage {

  @Override
  public void run(ClientRunnable cr, Message message) throws SQLException {
    //todo add service call for sending user profile message to client
  }
}
