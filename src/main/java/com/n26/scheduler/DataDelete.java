package com.n26.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.n26.service.TransactionService;

@Component
public class DataDelete {

	private static final Logger logger = LoggerFactory.getLogger(DataDelete.class);
	
	@Autowired
    TransactionService transactionService;

    @Scheduled(fixedRate = 1000)
    public void cleanOldData() {
    	logger.info("Auto delete operation triggered");
        transactionService.deleteOldTransactions();
    }
}