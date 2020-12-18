package io.yts.client.demoapp.model.creditreport;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class MonthlyCreditDetails {

  private Long numberofTx;
  private BigDecimal highestTx;
  private BigDecimal lowestTx;
  private BigDecimal revenue;
  private BigDecimal expenses;
  private Balances balances;

}
