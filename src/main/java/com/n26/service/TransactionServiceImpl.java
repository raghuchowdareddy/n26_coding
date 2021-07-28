package com.n26.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.n26.common.Constants;
import com.n26.common.Util;
import com.n26.model.OffsetDateTimeWrapper;
import com.n26.model.Statistics;
import com.n26.model.Transaction;

@Service("transactionService")
public class TransactionServiceImpl implements ITransactionService{
	
	private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
	
	private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneId.of(Constants.UTC));
	
    protected final  Map<OffsetDateTimeWrapper, Transaction>  storeTransactions = new ConcurrentHashMap<OffsetDateTimeWrapper, Transaction>();
   

    @Override
    public ResponseEntity<HttpStatus> addTransaction(Transaction transaction) {
    	ResponseEntity<HttpStatus> httpstatus = null;
    
    	 	httpstatus  = validateData(transaction);
	    	if(Objects.nonNull(httpstatus)) {
	    		return httpstatus;
	    	}
	    	storeTransactions.put(new OffsetDateTimeWrapper(OffsetDateTime.now(ZoneId.of(Constants.UTC))), transaction);
    	    return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    
  
    /**
     * This function is calculate the stats for last 60 seconds transactions
     * Return Statistics with empty or value
     */
	@Override
	public Statistics getStatistics() {
	
		List<Transaction> last60SecondsStatistics = storeTransactions.entrySet().stream()
				 .filter(x->(x.getKey().getDateTime().isBefore(OffsetDateTime.now(ZoneId.of(Constants.UTC)))
						        || (x.getKey().getDateTime()).equals(OffsetDateTime.now(ZoneId.of(Constants.UTC))))
						 && 
						 ((x.getKey().getDateTime()).isAfter(OffsetDateTime.now(ZoneId.of(Constants.UTC)).minusSeconds(60))
						   || (x.getKey().getDateTime()).equals(OffsetDateTime.now(ZoneId.of(Constants.UTC)).minusSeconds(60))
						   ))
		         .map(y->y.getValue()).collect(Collectors.toList());
		
			 Double sum = last60SecondsStatistics.stream().mapToDouble(transaction -> transaction.getAmount().doubleValue()).sum();
		     OptionalDouble average = last60SecondsStatistics.stream().mapToDouble(transaction -> transaction.getAmount().doubleValue()).average();
		     OptionalDouble max = last60SecondsStatistics.stream().mapToDouble(transaction -> transaction.getAmount().doubleValue()).max();
		     OptionalDouble min = last60SecondsStatistics.stream().mapToDouble(transaction -> transaction.getAmount().doubleValue()).min();
		     Integer count = last60SecondsStatistics.size();
	     
	      Statistics statistics = new Statistics();
	      statistics.setSum(sum !=0 ?  Constants.DECIMAL_FORMAT.format(sum) : "0.00");
	      statistics.setAvg(average.isPresent() ? Constants.DECIMAL_FORMAT.format(Util.getHalfRounded(new BigDecimal(average.getAsDouble()))) : "0.00");
	      statistics.setMax(max.isPresent() ? Constants.DECIMAL_FORMAT.format(Util.getHalfRounded(new BigDecimal(max.getAsDouble()))) : "0.00");
	      statistics.setMin(min.isPresent() ? Constants.DECIMAL_FORMAT.format(Util.getHalfRounded(new BigDecimal(min.getAsDouble()))) : "0.00");
	      statistics.setCount(count);
     
     return statistics;
	}
	
	@Override
	public void deleteAllTransactions() {
		if(!storeTransactions.isEmpty())
		{
			storeTransactions.clear();
		}
		
	}
	
	protected ResponseEntity<HttpStatus> validateData(Transaction transaction) {
		ResponseEntity<HttpStatus> httpstatus = null;
		try {
			OffsetDateTime requestTimestamp = ZonedDateTime.parse(transaction.getTimestamp(), TIMESTAMP_FORMATTER).toOffsetDateTime();
			OffsetDateTime now = OffsetDateTime.now(ZoneId.of(Constants.UTC));
		
		if(requestTimestamp.isBefore(now.minusSeconds(60))) {
			logger.error("Transaction timestamp should not be before 60 seconds of current time");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		if(requestTimestamp.isAfter(now)) {
			logger.error("Transaction timestamp should not be future of current time");
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		}
		  
		}catch(NumberFormatException exception) {
			logger.error("Error while numberformate "+exception.getMessage());
			return  ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		}catch (DateTimeParseException exception) {
			logger.error("Error while Date foramating "+exception.getMessage());
			return  ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		}
		return httpstatus;
		
		
	}


}
