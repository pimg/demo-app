package io.yts.client.demoapp.model.creditreport;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Value
@Builder
public class CreditReport {

	private Map<String, MonthlyCreditDetails> monthlyCreditDetails;
	private Long numberOfTx;
	private BigDecimal highestTx;
	private LocalDate oldestTx;
	private LocalDate newestTx;
	private BigDecimal totalRevenue;
	private BigDecimal totalExpenses;

}
