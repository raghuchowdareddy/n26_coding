package com.n26.model;

import java.time.OffsetDateTime;
import java.util.Objects;

import org.apache.commons.lang3.RandomUtils;

public class OffsetDateTimeWrapper {

	private OffsetDateTime dateTime;
	
	public OffsetDateTimeWrapper(OffsetDateTime dateTime) {
		this.dateTime = dateTime;
		
	}
	

	public OffsetDateTime getDateTime() {
		return dateTime;
	}


	public void setDateTime(OffsetDateTime dateTime) {
		this.dateTime = dateTime;
	}


	@Override
	public int hashCode() {
		return RandomUtils.nextInt();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OffsetDateTimeWrapper other = (OffsetDateTimeWrapper) obj;
		return Objects.equals(dateTime, other.dateTime);
	}
	
}
