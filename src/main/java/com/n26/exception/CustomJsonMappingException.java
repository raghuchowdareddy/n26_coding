package com.n26.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.UNPROCESSABLE_ENTITY, reason = "Error while mapping Json to POJO")
public class CustomJsonMappingException extends Exception 
{
   private static final long serialVersionUID = 100L;
   
   public CustomJsonMappingException(String message) {
	  super(message);
   }
}