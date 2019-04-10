package edu.northeastern.ccs.im.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MessageType;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.services.GroupServices;
import edu.northeastern.ccs.im.services.MessageServices;
import edu.northeastern.ccs.im.services.UserServices;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

    mockStatic(MessageServices.class);
    mockStatic(Prattle.class);
    List<String> wt = new ArrayList<>();
    wt.add("Rohan");
    Whitebox.setInternalState(Prattle.class,"listOfWireTappedUsers",wt);
  }


  @Test
  public void checkValidReply() throws SQLException {
    when(Prattle.updateAndGetChatIDFromUserMap(any(),any())).thenReturn(1);
    when(MessageServices.addMessage(any(),any(),any(),any(),any(Integer.class), any(), any(Integer.class))).thenReturn(true);

    ICommandMessage replyCommand = commandServiceMap.get(MessageType.REPLY);
    Message msg = Message.makeReplyMessage("Demon","/reply abc 1");
    replyCommand.run(clientRunnable,msg);
  }

}