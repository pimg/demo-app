package io.yts.client.demoapp.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.yts.client.demoapp.client.YtsWebClient;
import io.yts.client.demoapp.model.UserSite;
import io.yts.client.demoapp.repository.UserSiteRepository;
import io.yts.client.messages.ClientSiteEntity;
import io.yts.client.messages.LoginFormResponse;
import io.yts.client.messages.LoginStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
public class SiteService {

  private Cache<UUID, Map<String, UUID>> cache;
  private String psuIpAddress;

  @Autowired
  YtsWebClient ytsWebClient;

  @Autowired
  UserSiteRepository userSiteRepository;

  @PostConstruct
  public void initialize() {
    this.cache = Caffeine.newBuilder().maximumSize(100)
      .expireAfterWrite(Duration.ofSeconds(600)).build();

    Socket socket = new Socket();
    try {
      socket.connect(new InetSocketAddress("google.com", 80));
    } catch (IOException e) {
      this.psuIpAddress = "ff39:6773:c03c:48e8:5b49:492a:d198:4b05";
    }
    this.psuIpAddress = socket.getInetAddress().getHostAddress();

  }

  public Mono<ClientSiteEntity[]> getSites() {
    return ytsWebClient.getWebClient().get()
      .uri("/site-management/v2/sites")
      .header("Content-Type", "application/json")
      .retrieve().bodyToMono(ClientSiteEntity[].class);

  }

  public Mono<LoginStep> connect(UUID siteId, UUID userId, String redirectUrlId) {

    return ytsWebClient.getWebClient().post()
      .uri(uriBuilder -> uriBuilder.path("/v1/users/{userId}/connect")
        .queryParam("site", siteId)
        .queryParam("redirectUrlId", redirectUrlId)
        .build(userId))
      .header("PSU-IP-Address", psuIpAddress)
      .contentType(MediaType.APPLICATION_JSON)
      .retrieve().bodyToMono(LoginStep.class).map(loginStep -> {
        MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(loginStep.getRedirect().getUrl()).build().getQueryParams();
        UUID state = UUID.fromString(Objects.requireNonNull(parameters.getFirst("state")));
        cache.put(state, Map.of("siteId", siteId, "userId", userId));
        return loginStep;
      });
  }

  public Mono<UserSite> createUserSite(UUID state, String redirectUrl) {

    Map<String, UUID> usersiteInfo = cache.getIfPresent(state);
    UUID siteId = usersiteInfo.get("siteId");
    UUID userId = usersiteInfo.get("userId");

    String requestBody = "{\"redirectUrl\": \"" + redirectUrl + "\", \"loginType\": \"URL\"}";

    Mono<LoginFormResponse> loginFormResponse = ytsWebClient.getWebClient().post()
      .uri(uriBuilder -> uriBuilder.path("/v1/users/{userId}/user-sites").build(userId))
      .header("PSU-IP-Address", psuIpAddress)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(requestBody).retrieve().bodyToMono(LoginFormResponse.class);

    Mono<UserSite> userSite = loginFormResponse.flatMap(loginFormResponse1 -> Mono.just(new UserSite(loginFormResponse1.getUserSiteId(), userId, siteId)));
    return userSiteRepository.saveAll(userSite).publishNext();
  }

  public Flux<UserSite> getConnections() {
    return userSiteRepository.findAll();
  }
}
