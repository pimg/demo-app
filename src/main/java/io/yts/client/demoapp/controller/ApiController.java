package io.yts.client.demoapp.controller;

import io.yts.client.demoapp.model.User;
import io.yts.client.demoapp.model.UserSite;
import io.yts.client.demoapp.service.AccountService;
import io.yts.client.demoapp.service.SiteService;
import io.yts.client.demoapp.service.UserService;
import io.yts.client.messages.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ApiController {

	@Value("${redirect.url.id}")
	private String redirectUrlId;

	@Autowired
	private UserService userService;

	@Autowired
	private SiteService siteService;

	@Autowired
	private AccountService accountService;

	@GetMapping("/health")
	public Mono<String> getHealth() {
		return Mono.just("OK");
	}

	@GetMapping("/users")
	public Flux<User> getUsers() {
		return userService.findAllUsers();
	}

	@PostMapping("/users")
	public Mono<User> createUser() {
		return userService.createUser();
	}

	@GetMapping("/sites")
	public Mono<ClientSiteEntity[]> getSites() {
		return siteService.getSites();
	}

	@PostMapping("/sites/{siteId}/connect/{userId}")
	public Mono<LoginStep> connectBank(@PathVariable("siteId") UUID siteId, @PathVariable("userId") UUID userId){
		return siteService.connect(siteId, userId, redirectUrlId);
	}

	@GetMapping("/sites/connections")
	public Flux<UserSite> getBankConnection() {
		return siteService.getConnections();
	}

	@GetMapping("/accounts/{userId}")
	public Flux<AccountDTO> getAccounts(@PathVariable("userId") UUID userId) {
		return accountService.getAccounts(userId);
	}

	@GetMapping("/transactions/{userId}/{accountId}")
	public Flux<TransactionsPageDTO> getTransactions(@PathVariable("userId") UUID userId, @PathVariable("accountId") UUID accountId) {
		return accountService.getTransactions(userId, accountId);
	}
}
