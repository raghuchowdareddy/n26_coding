package com.n26;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.n26.domain.TransactionDomain;
import com.n26.exception.CustomJsonMappingException;
import com.n26.model.Statistics;
import com.n26.model.Transaction;
import com.n26.service.TransactionService;

public class TransactionServiceTest {
	
	@Test
	public void test_computeTransaction_with_status_code_201() {
		TransactionService service = new TransactionService();
		
		long offset = -30000;
        Instant timestamp = Instant.now().plusMillis(offset);
        TransactionDomain transactionDomain = new TransactionDomain();
        transactionDomain.setAmount(new BigDecimal(100));
        transactionDomain.setTimestamp(timestamp.toEpochMilli());
		ResponseEntity<String> computeTransaction = service.computeTransaction(transactionDomain);
		Assert.assertEquals(HttpStatus.CREATED, computeTransaction.getStatusCode());
	}
	
	@Test
	public void test_computeTransaction_with_status_code_204() {
		TransactionService service = new TransactionService();
		
		long offset = -61000;
        Instant timestamp = Instant.now().plusMillis(offset);
        TransactionDomain transactionDomain = new TransactionDomain();
        transactionDomain.setAmount(new BigDecimal(100));
        transactionDomain.setTimestamp(timestamp.toEpochMilli());
		ResponseEntity<String> computeTransaction = service.computeTransaction(transactionDomain);
		Assert.assertEquals(HttpStatus.NO_CONTENT, computeTransaction.getStatusCode());
	}
	
	@Test
	public void test_computeTransaction_with_status_code_422() {
		TransactionService service = new TransactionService();
		
		long offset = 61000;
        Instant timestamp = Instant.now().plusMillis(offset);
        TransactionDomain transactionDomain = new TransactionDomain();
        transactionDomain.setAmount(new BigDecimal(100));
        transactionDomain.setTimestamp(timestamp.toEpochMilli());
		ResponseEntity<String> computeTransaction = service.computeTransaction(transactionDomain);
		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, computeTransaction.getStatusCode());
	}

	@Test
	public void test_getStatistics_with_not_null() {
		TransactionService service = new TransactionService();
		Statistics stats = service.getStatistics();
		Assert.assertNotNull(stats);
	}
	

	@Test
	public void test_getStatistics_with_empty_stats() {
		TransactionService service = new TransactionService();
		Statistics stats = service.getStatistics();
		Assert.assertEquals("0.00", stats.getAvg());
		Assert.assertEquals(0, stats.getCount());
		Assert.assertEquals("0.00", stats.getSum());
		Assert.assertEquals("0.00", stats.getMax());
		Assert.assertEquals("0.00", stats.getMin());
	}
	@Test
	public void test_getStatistics_with_one_stats_data() {
		TransactionService service = new TransactionService();
		
		long offset = 61000;
        Instant timestamp = Instant.now().plusMillis(offset);
        TransactionDomain transactionDomain = new TransactionDomain();
        transactionDomain.setAmount(new BigDecimal(100));
        transactionDomain.setTimestamp(timestamp.toEpochMilli());
        
        service.add(transactionDomain);
        
		Statistics stats = service.getStatistics();
		Assert.assertEquals("100.00", stats.getAvg());
		Assert.assertEquals(1, stats.getCount());
		Assert.assertEquals("100.00", stats.getSum());
		Assert.assertEquals("100.00", stats.getMax());
		Assert.assertEquals("100.00", stats.getMin());
	}
	
	@Test
	public void test_getStatistics_for_transactions_added() {
		TransactionService service = new TransactionService();
		
		builTransDomainTestData(service);
        
		Statistics stats = service.getStatistics();
		Assert.assertEquals("150.00", stats.getAvg());
		Assert.assertEquals(99, stats.getCount());
		Assert.assertEquals("14850.00", stats.getSum());
		Assert.assertEquals("199.00", stats.getMax());
		Assert.assertEquals("101.00", stats.getMin());
	}
	
	@Test
	public void test_getStatistics_for_transactions_added_within_60sec(){
		TransactionService service = new TransactionService();
		
		builTransactionListByComputeTransactionMethod(service);
		
		//explicitly trying to add one more transaction which is more than 60 sec
		long offset = 61000;
	     Instant timestamp = Instant.now().plusMillis(offset);
		  TransactionDomain transactionDomain = new TransactionDomain();
         transactionDomain.setAmount(new BigDecimal(100));
         transactionDomain.setTimestamp(timestamp.toEpochMilli());
         service.computeTransaction(transactionDomain);
         
       //explicitly trying to add one more transaction which is less than 60 sec
 		 offset = -61000;
 	     timestamp = Instant.now().plusMillis(offset);
 		  transactionDomain = new TransactionDomain();
          transactionDomain.setAmount(new BigDecimal(100));
          transactionDomain.setTimestamp(timestamp.toEpochMilli());
          service.computeTransaction(transactionDomain);
          
         
		Statistics stats = service.getStatistics();
		Assert.assertEquals("150.00", stats.getAvg());
		Assert.assertEquals(99, stats.getCount());
		Assert.assertEquals("14850.00", stats.getSum());
		Assert.assertEquals("199.00", stats.getMax());
		Assert.assertEquals("101.00", stats.getMin());
	}
	
	@Test
	public void test_deleteOldTransactions() throws InterruptedException {
       TransactionService service = new TransactionService();
	    
       builTransactionListByComputeTransactionMethod(service);
       Thread.currentThread().sleep(38000);
       service.deleteOldTransactions();
       Assert.assertEquals(99, service.transactionsList.size());
	}
	
	@Test(expected = NullPointerException.class)
	public void test_validateAndBuildDomain_invalidData_null() throws CustomJsonMappingException {
		TransactionService service = new  TransactionService();
		service.validateAndBuildDomain(null);
	}
	@Test(expected = CustomJsonMappingException.class)
	public void test_validateAndBuildDomain_invalidData_to_amount() throws CustomJsonMappingException {
		TransactionService service = new  TransactionService();
		Transaction trans = new Transaction();
		trans.setAmount("amount");
		service.validateAndBuildDomain(trans);
	}
	
	@Test(expected = CustomJsonMappingException.class)
	public void test_validateAndBuildDomain_invalidData_timestamp() throws CustomJsonMappingException {
		TransactionService service = new  TransactionService();
		Transaction trans = new Transaction();
		trans.setAmount("123.99");
		trans.setTimestamp("some");
		service.validateAndBuildDomain(trans);
	}
	
	
	private TransactionService builTransactionListByComputeTransactionMethod(TransactionService service){
		IntStream.range(1, 100).forEach(i -> 
		{
			 long offset = -30000+(i+10000); // some random but within 60 seconds
		     Instant timestamp = Instant.now().plusMillis(offset);
			  TransactionDomain transactionDomain = new TransactionDomain();
	          transactionDomain.setAmount(new BigDecimal(100+i));
	          transactionDomain.setTimestamp(timestamp.toEpochMilli());
	          service.computeTransaction(transactionDomain);
		}
	  ); 
		return service;
	}
		
	private TransactionService builTransDomainTestData(TransactionService service){
		IntStream.range(1, 100).forEach(i -> 
		{
			 long offset = -30000+(i*10000); // some random but within 60 seconds
		     Instant timestamp = Instant.now().plusMillis(offset);
			  TransactionDomain transactionDomain = new TransactionDomain();
	          transactionDomain.setAmount(new BigDecimal(100+i));
	          transactionDomain.setTimestamp(timestamp.toEpochMilli());
	          service.add(transactionDomain);
		}
	  ); 
		return service;
		
	}
}
