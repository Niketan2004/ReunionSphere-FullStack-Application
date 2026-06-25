package com.ReunionSphere.authentication_service.Exceptions;

public class UserAlreadyExistsException extends RuntimeException {
     public UserAlreadyExistsException(String message) {
          super(message);
     }

}
