package io.yts.client.demoapp.controller;

import io.yts.client.demoapp.model.User;
import io.yts.client.demoapp.model.UserSite;
import io.yts.client.demoapp.service.SiteService;
import io.yts.client.demoapp.service.UserService;
import io.yts.client.messages.ClientSiteEntity;
import io.yts.client.messages.LoginStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import java.net.URI;
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
}
