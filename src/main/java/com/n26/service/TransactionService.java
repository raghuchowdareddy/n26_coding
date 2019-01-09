package com.n26.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.n26.common.Constants;
import com.n26.common.Util;
import com.n26.domain.TransactionDomain;
import com.n26.exception.CustomJsonMappingException;
import com.n26.model.Statistics;
import com.n26.model.Transaction;

@Service
public class TransactionService {
	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
	
    public List<TransactionDomain> transactionsList = new ArrayList<TransactionDomain>();
    Object lock = new Object();
    
    public TransactionDomain add(TransactionDomain transaction){
        transactionsList.add(transaction);
        return transaction;
    }

    public  ResponseEntity<String>  computeTransaction(final TransactionDomain transDomain) {
    	
    	ResponseEntity<String> response = null;
    	if(transDomain == null)
    		 return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    	
    	synchronized (lock) {
			
		  	long nowtimeWithMillis = Instant.now().toEpochMilli(); 
		  	if(!StringUtils.isEmpty(transDomain.getTimestamp())){
		  		
	 			if(transDomain.getTimestamp() < (nowtimeWithMillis  - Constants.ONE_MINUTE_IN_MILLIS)){
	 				response =  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	 				logger.info("Transaction is less than a 60 seconds. HTTP STATUS :204");
		        }
				else if(
						Constants.ONE_MINUTE_IN_MILLIS > (transDomain.getTimestamp() - nowtimeWithMillis) 
						&& (transDomain.getTimestamp() - nowtimeWithMillis) < Constants.ONE_MINUTE_IN_MILLIS)//if the transaction is within 60 seconds
					{
						add(transDomain);
						response =  ResponseEntity.status(HttpStatus.CREATED).build();
						logger.info("Transaction is added successfully . HTTP STATUS :201");
					}
				else{  
					response = ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
					logger.info("Transaction is greater than a 60 seconds. HTTP STATUS :422");
		        }
		  	}
		}
    	return response;
    }
	
	
    public Statistics getStatistics(){
    	
    	logger.info("Capturing statistics for 60 seconds transactions");
    	final Statistics statistics = new Statistics();
    	synchronized (lock) {
    	   
    		deleteOldTransactions();
        
    		Double sum = transactionsList.stream().mapToDouble(transaction -> transaction.getAmount().doubleValue()).sum();
	        OptionalDouble average = transactionsList.stream().mapToDouble(transaction -> transaction.getAmount().doubleValue()).average();
	        OptionalDouble max = transactionsList.stream().mapToDouble(transaction -> transaction.getAmount().doubleValue()).max();
	        OptionalDouble min = transactionsList.stream().mapToDouble(transaction -> transaction.getAmount().doubleValue()).min();
	        Integer count = transactionsList.size();
	        statistics.setSum(sum !=0 ?  Constants.DECIMAL_FORMAT.format(sum) : "0.00");
	        statistics.setAvg(average.isPresent() ? Constants.DECIMAL_FORMAT.format(Util.getHalfRounded(new BigDecimal(average.getAsDouble()))) : "0.00");
	        statistics.setMax(max.isPresent() ? Constants.DECIMAL_FORMAT.format(Util.getHalfRounded(new BigDecimal(max.getAsDouble()))) : "0.00");
	        statistics.setMin(min.isPresent() ? Constants.DECIMAL_FORMAT.format(Util.getHalfRounded(new BigDecimal(min.getAsDouble()))) : "0.00");
	        statistics.setCount(count);
	        
	        logger.info("Statistics computed successfully!.");
    	}
        return  statistics;
    }

    public void deleteAllTransactions() {
    	transactionsList.clear();
    	logger.info("Deleted All transaction from the list");
    }
    public void deleteOldTransactions(){
    	removeMatching(transactionsList.iterator(), isOlderThanOneMinute());
    }

    public static <E> void removeMatching(final Iterator<E> it, final Predicate<E> predicate) {
        while (it.hasNext()) {
            final E e = it.next();
            if (predicate.test(e)) {
                it.remove();
            }
        }
    }

    private static Predicate<TransactionDomain> isOlderThanOneMinute() {
//        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
//        long epochInMillis = utc.toEpochSecond() * 1000;
        long epochInMillis = Instant.now().toEpochMilli(); 
        return transaction ->
                transaction.getTimestamp() < epochInMillis - Constants.ONE_MINUTE_IN_MILLIS;
    }
    
    public  TransactionDomain validateAndBuildDomain(Transaction transaction)  throws CustomJsonMappingException{
    	if(transaction == null) {
    		throw new NullPointerException();
    	}
		TransactionDomain domain = new TransactionDomain();
		try {
			domain.setAmount(Util.getHalfRounded(new BigDecimal(transaction.getAmount())));
    		domain.setTimestamp(Constants.SIMPLE_DATE_FORMAT.parse(transaction.getTimestamp()).toInstant().toEpochMilli());
		} catch ( NumberFormatException | ParseException e) {
			throw new CustomJsonMappingException("Invalid data passed either in amount time stamp!..");
		}
		return domain;
	}


}
