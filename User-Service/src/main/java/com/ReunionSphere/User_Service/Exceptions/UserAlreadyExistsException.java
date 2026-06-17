package com.ReunionSphere.User_Service.Exceptions;

public class UserAlreadyExistsException extends RuntimeException {

          public UserAlreadyExistsException(String message) {
          super(message);
     }
}

