package com.n26.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.n26.model.Statistics;
import com.n26.model.Transaction;

@RunWith(JUnit4.class)
public class TransactionServiceImplTest {
	
	@Test
	public void test_transaction_adding_to_map() {
		TransactionServiceImpl service = new TransactionServiceImpl();
		
	    Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(100));
        transaction.setTimestamp(OffsetDateTime.now(ZoneId.of("UTC")).toString());
		ResponseEntity<HttpStatus> computeTransaction = service.addTransaction(transaction);
		Assert.assertEquals(HttpStatus.CREATED, computeTransaction.getStatusCode());
		Assert.assertEquals(1, service.storeTransactions.size());
	}
	
	@Test
	public void test_transaction_with_status_code_204_by_date_older_than_60sec() {
        TransactionServiceImpl service = new TransactionServiceImpl();
		
	    Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(100));
        transaction.setTimestamp(OffsetDateTime.now(ZoneId.of("UTC")).minusSeconds(61).toString());
        
		ResponseEntity<HttpStatus> computeTransaction = service.validateData(transaction);
		Assert.assertEquals(HttpStatus.NO_CONTENT, computeTransaction.getStatusCode());
		Assert.assertEquals(0, service.storeTransactions.size());
		
		
	}
	
	@Test
	public void test_transaction_with_status_code_422_by_date_in_future() {
       TransactionServiceImpl service = new TransactionServiceImpl();
		
	    Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(100));
        transaction.setTimestamp(OffsetDateTime.now(ZoneId.of("UTC")).plusSeconds(60).toString());
		ResponseEntity<HttpStatus> computeTransaction = service.addTransaction(transaction);
		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, computeTransaction.getStatusCode());
		Assert.assertEquals(0, service.storeTransactions.size());
		
	}

	@Test
	public void test_getStatistics_with_not_null() {
		TransactionServiceImpl service = new TransactionServiceImpl();
		Statistics stats = service.getStatistics();
		Assert.assertNotNull(stats);
	}
	

	@Test
	public void test_getStatistics_with_empty_stats() {
		TransactionServiceImpl service = new TransactionServiceImpl();
		Statistics stats = service.getStatistics();
		Assert.assertEquals("0.00", stats.getAvg());
		Assert.assertEquals(0, stats.getCount());
		Assert.assertEquals("0.00", stats.getSum());
		Assert.assertEquals("0.00", stats.getMax());
		Assert.assertEquals("0.00", stats.getMin());
	}
	@Test
	public void test_getStatistics_with_one_stats_data() {
		TransactionServiceImpl service = new TransactionServiceImpl();
		
	    Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(100));
        transaction.setTimestamp(OffsetDateTime.now(ZoneId.of("UTC")).toString());
        
        service.addTransaction(transaction);
        
		Statistics stats = service.getStatistics();
		Assert.assertEquals("100.00", stats.getAvg());
		Assert.assertEquals(1, stats.getCount());
		Assert.assertEquals("100.00", stats.getSum());
		Assert.assertEquals("100.00", stats.getMax());
		Assert.assertEquals("100.00", stats.getMin());
	}
	
	@Test
	public void test_getStatistics_for_transactions_added() {
		TransactionServiceImpl service = new TransactionServiceImpl();
		
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
		TransactionServiceImpl service = new TransactionServiceImpl();
		
		builTransactionListByComputeTransactionMethod(service);
		
		//explicitly trying to add one more transaction which is more than 60 sec
		 Transaction transaction = new Transaction();
         transaction.setAmount(new BigDecimal(100));
         transaction.setTimestamp(OffsetDateTime.now(ZoneId.of("UTC")).plusSeconds(61).toString());
         service.validateData(transaction);
         
       //explicitly trying to add one more transaction which is less than 60 sec
 		  transaction = new Transaction();
          transaction.setAmount(new BigDecimal(100));
          transaction.setTimestamp(OffsetDateTime.now(ZoneId.of("UTC")).minusSeconds(61).toString());
          service.validateData(transaction);
          
         
		Statistics stats = service.getStatistics();
		Assert.assertEquals("150.00", stats.getAvg());
		Assert.assertEquals(99, stats.getCount());
		Assert.assertEquals("14850.00", stats.getSum());
		Assert.assertEquals("199.00", stats.getMax());
		Assert.assertEquals("101.00", stats.getMin());
	}
	
	@Test
	public void test_deleteOldTransactions() throws InterruptedException {
       TransactionServiceImpl service = new TransactionServiceImpl();
	    
       builTransactionListByComputeTransactionMethod(service);
       Assert.assertEquals(99, service.storeTransactions.size());
       service.deleteAllTransactions();
       Assert.assertEquals(0, service.storeTransactions.size());
	}
	
	@Test(expected = NullPointerException.class)
	public void test_validateData_invalidData_null(){
		TransactionServiceImpl service = new  TransactionServiceImpl();
		service.validateData(null);
	}
	@Test(expected = NullPointerException.class)
	public void test_validateData_invalidAmount() {
		TransactionServiceImpl service = new  TransactionServiceImpl();
		Transaction trans = new Transaction();
		trans.setAmount(null);
		service.validateData(trans);
	}
	@Test(expected = NumberFormatException.class)
	public void test_validateData_invalidData_Date() {
		TransactionServiceImpl service = new  TransactionServiceImpl();
		Transaction trans = new Transaction();
		trans.setAmount(new BigDecimal("amount"));
		service.validateData(trans);
	}
	@Test
	public void test_validateAndBuildDomain_invalidDate_formate() {
		TransactionServiceImpl service = new  TransactionServiceImpl();
		Transaction trans = new Transaction();
		trans.setTimestamp("2021/07-26");
		trans.setAmount(new BigDecimal(56.1234));
		ResponseEntity<HttpStatus> validateData = service.validateData(trans);
		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, validateData.getStatusCode());
	}
	
	
	private TransactionServiceImpl builTransactionListByComputeTransactionMethod(TransactionServiceImpl service){
		IntStream.range(1, 100).forEach(i -> 
		{
			  Transaction transaction = new Transaction();
	          transaction.setAmount(new BigDecimal(100+i));
	          transaction.setTimestamp(OffsetDateTime.now(ZoneId.of("UTC")).toString());
	          service.addTransaction(transaction);
		}
	  ); 
		return service;
	}
		
	private TransactionServiceImpl builTransDomainTestData(TransactionServiceImpl service){
		IntStream.range(1, 100).forEach(i -> 
		{
			  Transaction transaction = new Transaction();
	          transaction.setAmount(new BigDecimal(100+i));
	          transaction.setTimestamp(OffsetDateTime.now(ZoneId.of("UTC")).toString());
	          service.addTransaction(transaction);
		}
	  ); 
		return service;
		
	}
}
