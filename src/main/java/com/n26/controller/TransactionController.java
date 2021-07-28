package com.n26.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.n26.exception.CustomJsonMappingException;
import com.n26.model.Transaction;
import com.n26.service.ITransactionService;

@RestController
public class TransactionController {
	
	private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
	
	@Autowired
	private ITransactionService service;

  	@RequestMapping(value = "/transactions", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HttpStatus> createTransaction(@RequestBody Transaction transaction) {
		try {
		ResponseEntity<HttpStatus> addTransactionStatus = service.addTransaction(transaction);
		
		return addTransactionStatus;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
  	@ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HttpStatus> handleException(Exception e) {
        return CustomJsonMappingException.handleRequestExceptions(e);
    }

	
 	    
}
  
