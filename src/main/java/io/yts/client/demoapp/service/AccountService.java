package io.yts.client.demoapp.service;

import io.yts.client.demoapp.client.YtsWebClient;
import io.yts.client.messages.AccountDTO;
import io.yts.client.messages.TransactionDTO;
import io.yts.client.messages.TransactionsPageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
public class AccountService {

  @Autowired
  YtsWebClient ytsWebClient;

  public Flux<AccountDTO> getAccounts(UUID userId) {
    return ytsWebClient.getWebClient().get()
      .uri(uriBuilder -> uriBuilder.path("/v1/users/{$userId}/accounts").build(userId))
      .header("Content-Type", "application/json")
      .retrieve().bodyToFlux(AccountDTO.class);
  }

  public Mono<List<TransactionDTO>> getAllTransactions(UUID userId, UUID accountId) {
    return getTransactions(userId, accountId, null).expand(response -> {
      if (response.getNext() == null) {
        return Mono.empty();
      }
      return getTransactions(userId, accountId, response.getNext());
    }).flatMap(response -> Flux.fromIterable(response.getTransactions())).collectList();
  }

  public Mono<TransactionsPageDTO> getTransactions(UUID userId, UUID accountId, String next) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(1);

    if(next != null) {
      queryParams.add("next", next);
    }

    if(accountId != null) {
      queryParams.add("accountIds", accountId.toString());
    }

    Mono<TransactionsPageDTO> txResponse = ytsWebClient.getWebClient().get()
      .uri(uriBuilder -> uriBuilder.path("/v1/users/{$userId}/transactions")
        .queryParams(queryParams)
        .build(userId))
      .header("Content-Type", "application/json")
      .retrieve()
      .bodyToMono(TransactionsPageDTO.class);
    return txResponse;

  }

}
