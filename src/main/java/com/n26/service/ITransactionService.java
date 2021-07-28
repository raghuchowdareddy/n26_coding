package com.n26.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.n26.model.Statistics;
import com.n26.model.Transaction;

public interface ITransactionService {

	public ResponseEntity<HttpStatus> addTransaction(Transaction transaction);

	public Statistics getStatistics();

	public void deleteAllTransactions();
}
