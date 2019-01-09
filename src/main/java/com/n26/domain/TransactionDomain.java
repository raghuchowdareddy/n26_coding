package com.n26.domain;

import java.math.BigDecimal;

public class TransactionDomain{

	private BigDecimal amount;
	private Long timestamp;
	
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

    
   
}