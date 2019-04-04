package edu.northeastern.ccs.im.server;

import java.sql.SQLException;

import edu.northeastern.ccs.im.Message;

public interface ICommandMessage {

  public void run(ClientRunnable cr, Message message) throws SQLException;

}
