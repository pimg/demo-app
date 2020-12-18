package io.yts.client.demoapp.service.composites;

import io.yts.client.demoapp.model.creditreport.CreditReport;
import io.yts.client.demoapp.model.creditreport.MonthlyCreditDetails;
import io.yts.client.demoapp.service.AccountService;
import io.yts.client.messages.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CreditService {

	@Autowired
	public AccountService accountService;

	@Autowired
	public BalanceService balanceService;

	public Mono<CreditReport> calculateCreditReport(UUID userId, UUID accountId) {

		Mono<List<TransactionDTO>> tx = accountService.getAllTransactions(userId, accountId);

		return tx.map(transactionDTOList -> {
			CreditReport creditReport = CreditReport.builder()
					.numberOfTx(transactionDTOList.stream().count())
					.highestTx(calculateMax(transactionDTOList))
					.oldestTx(getOldestTx(transactionDTOList))
					.newestTx(getNewestTx(transactionDTOList))
					.totalRevenue(calculateRevenue(transactionDTOList))
					.totalExpenses(calculateExpenses(transactionDTOList))
					.monthlyCreditDetails(calculateMonthlyCreditDetails(transactionDTOList))
				.build();
			return creditReport;
		});
	}

	private Map<String, MonthlyCreditDetails> calculateMonthlyCreditDetails(List<TransactionDTO> transactionDTOList) {

		return groupTxPerMonth(transactionDTOList).entrySet().stream().collect(Collectors.toMap(stringListEntry -> stringListEntry.getKey(), stringListEntry -> {
			return MonthlyCreditDetails.builder()
					.numberofTx(Long.valueOf(stringListEntry.getValue().size()))
					.highestTx(calculateMax(stringListEntry.getValue()))
					.lowestTx(calculateMin(stringListEntry.getValue()))
					.revenue(calculateRevenue(stringListEntry.getValue()))
					.expenses(calculateExpenses(stringListEntry.getValue()))
					.balances(balanceService.calculateBalances(stringListEntry.getValue()))
				.build();
		}));
	}

	private Map<String, List<TransactionDTO>> groupTxPerMonth(List<TransactionDTO> transactionDTOList) {
		return transactionDTOList.stream()
			.collect(Collectors.groupingBy(transactionDTO -> transactionDTO.getDate().getMonth().toString() + "_" + String.valueOf(transactionDTO.getDate().getYear())));
	}

	private BigDecimal calculateMax(List<TransactionDTO> txList) {
		return txList.stream()
			.map(TransactionDTO::getAmount)
			.max(Comparator.comparingDouble(BigDecimal::doubleValue))
			.get();
	}
	private BigDecimal calculateMin(List<TransactionDTO> txList) {
		return txList.stream()
			.map(TransactionDTO::getAmount)
			.min(Comparator.comparingDouble(BigDecimal::doubleValue))
			.get();
	}

	private LocalDate getOldestTx(List<TransactionDTO> txList) {
		return txList.stream()
			.map(TransactionDTO::getDate)
			.min(LocalDate::compareTo)
			.get();
	}

	private LocalDate getNewestTx(List<TransactionDTO> txList) {
		return txList.stream()
			.map(TransactionDTO::getDate)
			.max(LocalDate::compareTo)
			.get();
	}

	private BigDecimal calculateRevenue(List<TransactionDTO> txList) {
		return txList.stream()
			.map(TransactionDTO::getAmount)
			.filter(bigDecimal -> bigDecimal.compareTo(BigDecimal.ZERO) > 0)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private BigDecimal calculateExpenses(List<TransactionDTO> txList) {
		return txList.stream()
			.map(TransactionDTO::getAmount)
			.filter(bigDecimal -> bigDecimal.compareTo(BigDecimal.ZERO) < 0)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
