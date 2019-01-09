package com.n26.common;

import java.math.BigDecimal;

public class Util {

	public static BigDecimal getHalfRounded(BigDecimal value) {
		BigDecimal scaledBigDecimal = value.setScale(2, BigDecimal.ROUND_HALF_UP);
		return scaledBigDecimal;
	}
}
