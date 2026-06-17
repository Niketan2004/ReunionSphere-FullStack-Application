package com.ReunionSphere.User_Service.Exceptions;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class LocationNotFoundException extends RuntimeException {

     public LocationNotFoundException(String message) {

          super(message);
     }
}