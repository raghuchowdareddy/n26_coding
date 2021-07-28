package com.n26.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

@ResponseStatus(value=HttpStatus.UNPROCESSABLE_ENTITY, reason = "Error while mapping Json to POJO")
public class CustomJsonMappingException extends Exception 
{
   private static final long serialVersionUID = 100L;
   
   public CustomJsonMappingException(String message) {
	  super(message);
   }
   

   public static ResponseEntity<HttpStatus> handleRequestExceptions(Exception e) {
		if(e.getCause().getClass().equals(MismatchedInputException.class)) {
       	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
       }else if(e.getCause().getClass().equals(InvalidFormatException.class)) {
          return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
       }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
	}
}