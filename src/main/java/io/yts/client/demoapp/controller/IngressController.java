package io.yts.client.demoapp.controller;

import io.yts.client.demoapp.model.UserSite;
import io.yts.client.demoapp.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ingress")
public class IngressController {

	@Autowired
	SiteService siteService;

	@GetMapping("/redirect")
	public Mono<UserSite> captureRedirect(ServerHttpRequest request, @RequestParam Map<String, String> queryParams) {
		return siteService.createUserSite(UUID.fromString(queryParams.get("state")), request.getURI().toString());
	}

}
