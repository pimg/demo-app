package io.yts.client.demoapp.service.composites;

import io.yts.client.demoapp.model.creditreport.Balances;
import io.yts.client.messages.TransactionDTO;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BalanceService {

  public Balances calculateBalances(List<TransactionDTO> transactionDTOList) {
    Balances.BalancesBuilder balancesBuilder = Balances.builder();
    Map<LocalDateTime, BigDecimal> historicalBalances = calculateHistoricalBalances(transactionDTOList);
    Map<String, List<LocalDateTime>> txKeysPerMonth = getTxKeysPerMonth(historicalBalances);

    txKeysPerMonth.forEach((key, localDateTimes) -> {
      List<BigDecimal> txList = historicalBalances.keySet().stream()
        .filter(localDateTime -> (localDateTime.getMonth().toString() + "_" + String.valueOf(localDateTime.getYear())).equals(key))
        .map(historicalBalances::get)
        .collect(Collectors.toList());

      balancesBuilder
        .lowestBalance(Collections.min(txList, Comparator.naturalOrder()))
        .highestBalance(Collections.max(txList, Comparator.naturalOrder()));
    });
    return balancesBuilder.build();
  }

  private Map<LocalDateTime, BigDecimal> calculateHistoricalBalances(List<TransactionDTO> transactionDTOList) {
    Map<LocalDateTime, BigDecimal> historicalBalance = new TreeMap<>();

    //TODO replace with fetching the balance of the correct account
    BigDecimal balance = new BigDecimal("1019883.45");

    transactionDTOList.forEach(transactionDTO ->
      historicalBalance.put(transactionDTO.getTimestamp().toLocalDateTime(), getNewBalance(balance, transactionDTO.getAmount())
    ));
    return historicalBalance;
  }

  private BigDecimal getNewBalance(BigDecimal balance, BigDecimal amount) {
    balance = balance.subtract(amount);
    return balance;
  }

  private Map<String, List<LocalDateTime>> getTxKeysPerMonth(Map<LocalDateTime, BigDecimal> historicalBalances) {
    return historicalBalances.keySet().stream().collect(Collectors.groupingBy(localDateTime -> localDateTime.getMonth().toString() + "_" + String.valueOf(localDateTime.getYear())));
  }

}
