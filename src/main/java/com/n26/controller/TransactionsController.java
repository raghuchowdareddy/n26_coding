package com.n26.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.n26.domain.TransactionDomain;
import com.n26.exception.CustomJsonMappingException;
import com.n26.model.Transaction;
import com.n26.service.TransactionService;

@RestController
public class TransactionsController {
	
	private static final Logger logger = LoggerFactory.getLogger(TransactionsController.class);
	
	@Autowired TransactionService transactionService;
	
	@RequestMapping(value="/transactions",method=RequestMethod.DELETE)
    public ResponseEntity<String> deleteTransactions(){
		logger.trace("End point /transactions being invoked for DELETE operation");
		
		transactionService.deleteAllTransactions();
    	return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		
    }
    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    public ResponseEntity<String> transaction(@RequestBody Transaction transaction) throws CustomJsonMappingException{
    	logger.trace("End point /transactions being invoked for POST operation");
    	
    	TransactionDomain domain = transactionService.validateAndBuildDomain(transaction);
    	return transactionService.computeTransaction(domain);
    }
    
	
    
}
  
