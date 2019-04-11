package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
  public void checkValidReply() throws SQLException {
    when(Prattle.updateAndGetChatIDFromUserMap(any(),any())).thenReturn(1);
    when(MessageServices.addMessage(any(),any(),any(),any(),any(Integer.class), any(), any(Integer.class))).thenReturn(true);

    ICommandMessage replyCommand = commandServiceMap.get(MessageType.REPLY);
    Message msg = Message.makeReplyMessage("Demon","/reply abc 1");
    replyCommand.run(clientRunnable,msg);
  }

  @Test
  public void testDNDcommandTrue() throws SQLException {

    ICommandMessage DNDcommand = commandServiceMap.get(MessageType.DO_NOT_DISTURB);
    Message msg = Message.makeDNDMessage("J", "/DND T");
    DNDcommand.run(clientRunnable,msg);

  }

  @Test
  public void testDNDcommandFalse() throws SQLException {

    ICommandMessage DNDcommand = commandServiceMap.get(MessageType.DO_NOT_DISTURB);
    Message msg = Message.makeDNDMessage("J", "/DND F");
    DNDcommand.run(clientRunnable,msg);

  }

  @Test
  public void testFollowUserCommand() throws SQLException {

    ICommandMessage followUser = commandServiceMap.get(MessageType.FOLLOW_USER);
    Message msg = Message.makeFollowUserMessage("R", "/follow Rohan");
    followUser.run(clientRunnable,msg);
    Message msg2 = Message.makeFollowUserMessage("R", "/follow Rohan");
    followUser.run(clientRunnable,msg2);
  }

  @Test
  public void testForwardMessageCommand() throws SQLException {

    ICommandMessage forwardMessage = commandServiceMap.get(MessageType.FORWARD_MESSAGE);
    Message fwdMsg = Message.makeForwardMessageMessage("J", "/fwd r 2 josh");
    Message fwdMsg2 = Message.makeForwardMessageMessage("J", "/fwd r 2 T");

    when(MessageServices.isSecret(anyString(),anyString(),anyInt())).thenReturn(false);
    when(MessageServices.getMessageForForwarding(anyString(),anyString(),anyInt(),any())).thenReturn("/fwd r 2 josh");
    forwardMessage.run(clientRunnable, fwdMsg);
    forwardMessage.run(clientRunnable,fwdMsg2);
  }

  @Test
  public void testForwardMessageCommandWithSecret() throws SQLException {

    ICommandMessage forwardMessage = commandServiceMap.get(MessageType.FORWARD_MESSAGE);
    Message fwdMsg = Message.makeForwardMessageMessage("J", "/fwd r 2 josh");

    when(MessageServices.isSecret(anyString(),anyString(),anyInt())).thenReturn(true);
    when(MessageServices.getMessageForForwarding(anyString(),anyString(),anyInt(),any())).thenReturn("/fwd r 2 josh");
    forwardMessage.run(clientRunnable, fwdMsg);
    when(MessageServices.getMessageForForwarding(anyString(),anyString(),anyInt(),any())).thenReturn("/reply r 2 josh");
    forwardMessage.run(clientRunnable,fwdMsg);

  }

  @Test
  public void testForwardMessageCommandWithReply() throws SQLException {

    ICommandMessage forwardMessage = commandServiceMap.get(MessageType.FORWARD_MESSAGE);
    Message fwdMsg = Message.makeForwardMessageMessage("J", "/fwd r 2 josh");

    when(MessageServices.isSecret(anyString(),anyString(),anyInt())).thenReturn(false);
    when(MessageServices.getMessageForForwarding(anyString(),anyString(),anyInt(),any())).thenReturn("/reply r 2 josh");
    forwardMessage.run(clientRunnable,fwdMsg);

  }

  @Test
  public void testGetGroupsUserBelongsTo() throws SQLException {

    ICommandMessage getGroups = commandServiceMap.get(MessageType.GET_ALL_GROUP_USER_BELONGS);
    Message ggm = Message.makeGetAllGroupsUserBelongsMessage("Z", "/getGrpsUserBelongsTo");
    getGroups.run(clientRunnable,ggm);
    Message ggm2 = Message.makeGetAllGroupsUserBelongsMessage("Rohan", "/getGrpsUserBelongsTo");
    getGroups.run(clientRunnable,ggm2);
  }

  @Test
  public void testGetAllThreadsCommand() throws SQLException {
    ICommandMessage getThreads = commandServiceMap.get(MessageType.GET_ALL_THREADS);
    Message gat = Message.makeGetAllThreadsMessage("R", "/getAllThreads");
    List<String> threads = new ArrayList<>();
    threads.add("#MSD");
    when(GroupServices.retrieveAllThreads()).thenReturn(threads);
    getThreads.run(clientRunnable,gat);
  }

  @Test
  public void testGetFollowersCommand() throws SQLException {
    ICommandMessage getFollowers = commandServiceMap.get(MessageType.GET_FOLLOWERS);
    Message gaf = Message.makeGetFollowersMessage("R", "/followers");
    List<String> followers = new ArrayList<>();
    followers.add("josh");
    when(UserServices.getFollowers(any())).thenReturn(followers);
    getFollowers.run(clientRunnable,gaf);
  }

  @Test
  public void testGetFollowingCommand() throws SQLException {
    ICommandMessage getFollowing = commandServiceMap.get(MessageType.GET_FOLLOWING);
    Message gfm = Message.makeGetFollowingMessage("R", "/following");
    List<String> following = new ArrayList<>();
    following.add("josh");
    when(UserServices.getFollowing(any())).thenReturn(following);
    getFollowing.run(clientRunnable,gfm);
  }

  @Test
  public void testGetMessagesBetween() throws SQLException {
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
  public void testCreateThreadCommand() throws SQLException {
    ICommandMessage createThread = commandServiceMap.get(MessageType.CREATE_THREAD);
    Message ctd = Message.makeCreateThreadMessage("J", "/createThread #newThread");
    createThread.run(clientRunnable,ctd);
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

  @Test
  public void testGetListWireTappedUsers() throws SQLException {
    ICommandMessage getWiretappedUser = commandServiceMap.get(MessageType.GET_LIST_OF_WIRETAPPED_USERS);
    Message gwu = Message.makeGetListWiretappedUsers("CIA", "/listWTUsers");
    List<String> wtUsers = new ArrayList<>();
    wtUsers.add("Josh");
    when(UserServices.getListOfTappedUsers()).thenReturn(wtUsers);
    getWiretappedUser.run(clientRunnable,gwu);
  }

  @Test
  public void testGetUserProfileCommand() throws SQLException {
    ICommandMessage getUserProfile = commandServiceMap.get(MessageType.GET_USER_PROFILE);
    Message gup = Message.makeGetUserProfileMessage("A", "/getProfile");
    ConcurrentHashMap<User.UserParams, String> userProfile = new ConcurrentHashMap<>();
    userProfile.put(User.UserParams.USERNAME, "tu");
    userProfile.put(User.UserParams.FIRSTNAME, "tu");
    userProfile.put(User.UserParams.LASTNAME, "tu");
    userProfile.put(User.UserParams.EMAIL, "tu");
    when(UserServices.getUserProfile(any())).thenReturn(userProfile);
    getUserProfile.run(clientRunnable,gup);

  }

  @Test
  public void testGetTappedUserDataCommand() throws SQLException {
    ICommandMessage getTappedUserData = commandServiceMap.get(MessageType.GET_DATA_WIRETAPPED_USER);
    Message gwd = Message.makeGetDataOfWiretappedUser("CIA", "/getWTUserData J");
    List<String> data = new ArrayList<>();
    data.add("data of wiretapped user");
    when(MessageServices.getAllDataForCIA(any())).thenReturn(data);
    getTappedUserData.run(clientRunnable, gwd);
  }
}