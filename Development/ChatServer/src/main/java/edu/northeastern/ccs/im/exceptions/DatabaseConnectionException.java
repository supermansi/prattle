/** Copyright (c) 2019 Rohan Gori, Aditi Kacheria, Mansi Jain, Joshua Dick. All rights reserved.*/
package edu.northeastern.ccs.im.exceptions;

/**
 * Class for a database connection exception.
 */
public class DatabaseConnectionException extends RuntimeException {

  public DatabaseConnectionException(String exception){
    super(exception);
  }
}
