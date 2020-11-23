package io.yts.client.demoapp.service;

import io.yts.client.demoapp.client.YtsWebClient;
import io.yts.client.messages.AccountDTO;
import io.yts.client.messages.TransactionsPageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Component
public class AccountService {

	@Autowired
	YtsWebClient ytsWebClient;

	public Flux<AccountDTO> getAccounts(UUID userId){
		return ytsWebClient.getWebClient().get()
			.uri(uriBuilder -> uriBuilder.path("/v1/users/{$userId}/accounts").build(userId))
			.header("Content-Type", "application/json")
			.retrieve().bodyToFlux(AccountDTO.class);
	}

	public Flux<TransactionsPageDTO> getTransactions(UUID userId, UUID accountId) {
		return ytsWebClient.getWebClient().get()
			.uri(uriBuilder -> uriBuilder.path("/v1/users/{$userId}/transactions")
//				.queryParam("accountIds", accountId)
				.build(userId))
			.header("Content-Type", "application/json")
			.retrieve().bodyToFlux(TransactionsPageDTO.class);

	}
}
