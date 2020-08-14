package io.yts.client.demoapp.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.handler.ssl.SslContext;
import io.yts.client.demoapp.client.util.AccessToken;
import io.yts.client.demoapp.client.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.util.function.Function;

@Component
public class YtsWebClient {

	private final WebClient webClient;
	private final ClientHttpConnector connector;
	private final Cache<String, String> cache;
	private final String baseUrl;
	private static final Logger logger = LoggerFactory.getLogger(YtsWebClient.class);


	@Autowired
	public YtsWebClient(SslContext sslContext, @Value("${client.base.url}") String baseUrl) {

		this.baseUrl = baseUrl;

		HttpClient httpClient = HttpClient.create()
				.secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

		this.connector = new ReactorClientHttpConnector(httpClient);

		this.webClient = WebClient.builder()
				.filter(setAccessToken)
				.baseUrl(baseUrl)
				.clientConnector(connector)
				.build();

		this.cache = Caffeine.newBuilder().maximumSize(100)
				.expireAfterWrite(Duration.ofSeconds(600)).build();
	}

	public WebClient getWebClient() {
		return webClient;
	}

	private ExchangeFilterFunction setAccessToken = (clientRequest, nextFilter)  -> extractAccessToken(getAccessToken()).map(setBearerTokenInHeader(clientRequest)).flatMap(nextFilter::exchange);

	private Function<String, ClientRequest> setBearerTokenInHeader(ClientRequest request) {
		return token -> ClientRequest.from(request).header("Authorization", "Bearer " + token).build();
	}

	private Mono<String> extractAccessToken(Mono<String> accessTokenResponse) {
		ObjectMapper mapper = new ObjectMapper();
		return accessTokenResponse.map(s -> {
			try {
				return mapper.readValue(s, AccessToken.class).getAccessToken();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return "";
			}
		});
	}

	private Mono<String> getAccessToken()  {
		String cachedToken = cache.getIfPresent("token");
		if (cachedToken != null) {
			return Mono.just(cachedToken);
		} else {
			try {
				return getAccessTokenFromServer(TokenUtil.createToken());
			} catch (Exception e) {
				e.printStackTrace();
				return Mono.empty();
			}
		}
	}

	private Mono<String> getAccessTokenFromServer(String requestToken) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException, CertificateException {

		logger.info("Requesting Access token");
		WebClient anonymousWebClient = WebClient.builder()
				.baseUrl(baseUrl)
				.clientConnector(this.connector)
				.build();

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>(2);
		body.add("grant_type", "client_credentials");
		body.add("request_token", requestToken);

		return anonymousWebClient.post()
				.uri("/tokens/tokens")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData(body))
				.retrieve().bodyToMono(String.class).doOnNext(res -> this.cache.put("token", res));
	}
}