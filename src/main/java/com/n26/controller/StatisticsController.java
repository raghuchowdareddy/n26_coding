package com.n26.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.n26.model.Statistics;
import com.n26.service.TransactionService;

@RestController
public class StatisticsController {
	
	private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);
	
    @Autowired
    TransactionService transactionService;

    @RequestMapping(value = "/statistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Statistics statistics(){
    	logger.info("End point /statistics being invoked");
        Statistics statistics =  transactionService.getStatistics();
        return statistics;
    }
}

