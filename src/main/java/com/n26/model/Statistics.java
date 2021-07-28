package com.n26.model;

import java.io.Serializable;

public class Statistics implements Serializable{
	private static final long serialVersionUID = 1L;

	
	public Statistics() {
	// TODO Auto-generated constructor stub
    }	
	public Statistics(String sum, String avg, String max, String min, int count) {
		this.sum = sum;
		this.avg = avg;
		this.max = max;
		this.min = min;
		this.count = count;
	}
	
	private String sum;
	private String avg;
	private String max;
	private String min;
	private int count;
	
	public String getSum() {
		return sum;
	}
	public void setSum(String sum) {
		this.sum = sum;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		this.min = min;
	}
	public String getMax() {
		return max;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String getAvg() {
		return avg;
	}
	public void setAvg(String avg) {
		this.avg = avg;
	}
	
	
}
