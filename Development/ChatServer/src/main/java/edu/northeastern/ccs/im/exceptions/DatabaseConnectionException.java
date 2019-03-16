package edu.northeastern.ccs.im.exceptions;

public class DatabaseConnectionException extends RuntimeException {

  public DatabaseConnectionException(String exception){
    super(exception);
  }
}
