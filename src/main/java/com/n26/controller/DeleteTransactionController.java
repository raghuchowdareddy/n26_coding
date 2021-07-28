package com.n26.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.n26.service.TransactionServiceImpl;

@RestController
public class DeleteTransactionController {
	
	private static final Logger logger = LoggerFactory.getLogger(DeleteTransactionController.class);
	
	@Autowired TransactionServiceImpl transactionService;
	
	@RequestMapping(value="/transactions",method=RequestMethod.DELETE)
    public ResponseEntity<String> deleteTransactions(){
		logger.info("End point /transactions being invoked for DELETE operation");
		
		transactionService.deleteAllTransactions();
    	return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		
    }
}
  
