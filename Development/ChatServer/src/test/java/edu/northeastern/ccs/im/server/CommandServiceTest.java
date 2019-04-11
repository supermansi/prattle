package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.dao.UserDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MessageType;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.services.GroupServices;
import edu.northeastern.ccs.im.services.MessageServices;
import edu.northeastern.ccs.im.services.UserServices;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserServices.class, MessageServices.class, GroupServices.class,Prattle.class})
@PowerMockIgnore("javax.net.ssl.*")

public class CommandServiceTest {

  private static NetworkConnection clientSocket;
  public ClientRunnable clientRunnable;
  NetworkConnection connection;

  CommandService commandService;

  Map<MessageType, ICommandMessage> commandServiceMap;

  /**
   * Set up for testing.
   */
  @Before
  public void init() {
    connection = mock(NetworkConnection.class);
    clientRunnable = new ClientRunnable(connection);
    commandService = CommandService.getInstance();
    commandServiceMap = commandService.getCommandServiceMap();

    clientRunnable.setName("R");
    mockStatic(MessageServices.class);
    mockStatic(Prattle.class);
    mockStatic(UserServices.class);
    mockStatic(GroupServices.class);
    List<String> wt = new ArrayList<>();
    wt.add("Rohan");
    Whitebox.setInternalState(Prattle.class,"listOfWireTappedUsers",wt);
    ConcurrentMap<String, List<String>> tempMap = new ConcurrentHashMap<>();
    tempMap.put("T", wt);
    Whitebox.setInternalState(Prattle.class, "userToFollowerMap", tempMap);
    Whitebox.setInternalState(Prattle.class, "groupToUserMapping", tempMap);
  }


  @Test
  public void checkValidReply() throws SQLException, ParseException {
    when(Prattle.updateAndGetChatIDFromUserMap(any(),any())).thenReturn(1);
    when(MessageServices.addMessage(any(),any(),any(),any(),any(Integer.class), any(), any(Integer.class))).thenReturn(true);

    ICommandMessage replyCommand = commandServiceMap.get(MessageType.REPLY);
    Message msg = Message.makeReplyMessage("Demon","/reply abc 1");
    replyCommand.run(clientRunnable,msg);
  }

  @Test
  public void testDNDcommandTrue() throws SQLException, ParseException {

    ICommandMessage DNDcommand = commandServiceMap.get(MessageType.DO_NOT_DISTURB);
    Message msg = Message.makeDNDMessage("J", "/DND T");
    DNDcommand.run(clientRunnable,msg);

  }

  @Test
  public void testDNDcommandFalse() throws SQLException, ParseException {

    ICommandMessage DNDcommand = commandServiceMap.get(MessageType.DO_NOT_DISTURB);
    Message msg = Message.makeDNDMessage("J", "/DND F");
    DNDcommand.run(clientRunnable,msg);

  }

  @Test
  public void testFollowUserCommand() throws SQLException, ParseException {

    ICommandMessage followUser = commandServiceMap.get(MessageType.FOLLOW_USER);
    Message msg = Message.makeFollowUserMessage("R", "/follow Rohan");
    followUser.run(clientRunnable,msg);
    Message msg2 = Message.makeFollowUserMessage("R", "/follow Rohan");
    followUser.run(clientRunnable,msg2);
  }

  @Test
  public void testForwardMessageCommand() throws SQLException, ParseException {

    ICommandMessage forwardMessage = commandServiceMap.get(MessageType.FOWARD_MESSAGE);
    Message fwdMsg = Message.makeForwardMessageMessage("J", "/fwd r 2 josh");
    Message fwdMsg2 = Message.makeForwardMessageMessage("J", "/fwd r 2 T");

    when(MessageServices.isSecret(anyString(),anyString(),anyInt())).thenReturn(false);
    when(MessageServices.getMessageForForwarding(anyString(),anyString(),anyInt(),any())).thenReturn("/fwd r 2 josh");
    forwardMessage.run(clientRunnable, fwdMsg);
    forwardMessage.run(clientRunnable,fwdMsg2);
  }

  @Test
  public void testForwardMessageCommandWithSecret() throws SQLException, ParseException {

    ICommandMessage forwardMessage = commandServiceMap.get(MessageType.FOWARD_MESSAGE);
    Message fwdMsg = Message.makeForwardMessageMessage("J", "/fwd r 2 josh");

    when(MessageServices.isSecret(anyString(),anyString(),anyInt())).thenReturn(true);
    when(MessageServices.getMessageForForwarding(anyString(),anyString(),anyInt(),any())).thenReturn("/fwd r 2 josh");
    forwardMessage.run(clientRunnable, fwdMsg);
    when(MessageServices.getMessageForForwarding(anyString(),anyString(),anyInt(),any())).thenReturn("/reply r 2 josh");
    forwardMessage.run(clientRunnable,fwdMsg);

  }

  @Test
  public void testForwardMessageCommandWithReply() throws SQLException, ParseException {

    ICommandMessage forwardMessage = commandServiceMap.get(MessageType.FOWARD_MESSAGE);
    Message fwdMsg = Message.makeForwardMessageMessage("J", "/fwd r 2 josh");

    when(MessageServices.isSecret(anyString(),anyString(),anyInt())).thenReturn(false);
    when(MessageServices.getMessageForForwarding(anyString(),anyString(),anyInt(),any())).thenReturn("/reply r 2 josh");
    forwardMessage.run(clientRunnable,fwdMsg);

  }

  @Test
  public void testGetGroupsUserBelongsTo() throws SQLException, ParseException {

    ICommandMessage getGroups = commandServiceMap.get(MessageType.GET_ALL_GROUP_USER_BELONGS);
    Message ggm = Message.makeGetAllGroupsUserBelongsMessage("Z", "/getGrpsUserBelongsTo");
    getGroups.run(clientRunnable,ggm);
    Message ggm2 = Message.makeGetAllGroupsUserBelongsMessage("Rohan", "/getGrpsUserBelongsTo");
    getGroups.run(clientRunnable,ggm2);
  }

  @Test
  public void testGetAllThreadsCommand() throws SQLException, ParseException {
    ICommandMessage getThreads = commandServiceMap.get(MessageType.GET_ALL_THREADS);
    Message gat = Message.makeGetAllThreadsMessage("R", "/getAllThreads");
    List<String> threads = new ArrayList<>();
    threads.add("#MSD");
    when(GroupServices.retrieveAllThreads()).thenReturn(threads);
    getThreads.run(clientRunnable,gat);
  }

  @Test
  public void testGetFollowersCommand() throws SQLException, ParseException {
    ICommandMessage getFollowers = commandServiceMap.get(MessageType.GET_FOLLOWERS);
    Message gaf = Message.makeGetFollowersMessage("R", "/followers");
    List<String> followers = new ArrayList<>();
    followers.add("josh");
    when(UserServices.getFollowers(any())).thenReturn(followers);
    getFollowers.run(clientRunnable,gaf);
  }

  @Test
  public void testGetFollowingCommand() throws SQLException, ParseException {
    ICommandMessage getFollowing = commandServiceMap.get(MessageType.GET_FOLLOWING);
    Message gfm = Message.makeGetFollowingMessage("R", "/following");
    List<String> following = new ArrayList<>();
    following.add("josh");
    when(UserServices.getFollowing(any())).thenReturn(following);
    getFollowing.run(clientRunnable,gfm);
  }

  @Test
  public void testGetMessagesBetween() throws SQLException, ParseException {
    ICommandMessage getMessagesBetween = commandServiceMap.get(MessageType.GET_MESSAGES_BETWEEN);
    Message gmb = Message.makeGetMessagesBetweenMessage("M", "/getMessages r 04/08/2019 04/10/2019");
    List<String> messages = new ArrayList<>();
    messages.add("/pvt r hey");
    when(MessageServices.getMessagesBetween(any(),any(),any(),any())).thenReturn(messages);
    getMessagesBetween.run(clientRunnable,gmb);
  }

  @Test
  public void testGetThreadMessages() throws SQLException {
    ICommandMessage getMessagesBetween = commandServiceMap.get(MessageType.GET_THREAD_MESSAGES);
    Message gmb = Message.makeGetThreadMessagesMessage("A", "/getThread #MSD");
    List<String> messages = new ArrayList<>();
    messages.add("/pvt r hey");
    when(MessageServices.retrieveGroupMessages(anyString())).thenReturn(messages);
    getMessagesBetween.run(clientRunnable,gmb);
  }

  @Test
  public void testPostOnThreadCommand() throws SQLException {
    ICommandMessage postOnThread = commandServiceMap.get(MessageType.POST_ON_THREAD);
    Message pot = Message.makePostOnThreadMessage("T", "/postToThread #MSD hey");
    postOnThread.run(clientRunnable,pot);
  }

  @Test
  public void testUnfollowCommand() throws SQLException {
    ICommandMessage unfollow = commandServiceMap.get(MessageType.UNFOLLOW_USER);
    Message ufu = Message.makeUnfollowUserMessage("T", "/unfollow T");
    unfollow.run(clientRunnable,ufu);
    Message ufu2 = Message.makeUnfollowUserMessage("T", "/unfollow XYZ");
    unfollow.run(clientRunnable,ufu2);
  }

  @Test
  public void testSetWireTapCommand() throws SQLException {
    ICommandMessage setWiretap = commandServiceMap.get(MessageType.SET_WIRETAP_MESSAGE);
    Message swt = Message.makeSetWiretapMessage("R", "/tapUser T");
    setWiretap.run(clientRunnable,swt);
    clientRunnable.setName("CIA");
    Message swt2 = Message.makeSetWiretapMessage("CIA", "/tapUser T T");
    setWiretap.run(clientRunnable,swt2);
    Message swt3 = Message.makeSetWiretapMessage("CIA", "/tapUser T F");
    setWiretap.run(clientRunnable,swt3);
  }
}