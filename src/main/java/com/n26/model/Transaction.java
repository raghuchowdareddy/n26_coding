package com.n26.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction implements Serializable{

	private static final long serialVersionUID = -9087185157453215444L;
	
	@JsonProperty 
	private String amount;
	
	@JsonProperty
    private String timestamp;
	
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

   

   
}