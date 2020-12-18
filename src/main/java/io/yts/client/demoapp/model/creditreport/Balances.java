package io.yts.client.demoapp.model.creditreport;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class Balances {

  private BigDecimal highestBalance;
  private BigDecimal lowestBalance;
}
