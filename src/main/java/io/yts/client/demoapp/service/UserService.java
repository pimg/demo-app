package io.yts.client.demoapp.service;

import io.yts.client.demoapp.client.YtsWebClient;
import io.yts.client.demoapp.model.User;
import io.yts.client.demoapp.repository.UserRepository;
import io.yts.client.messages.ClientUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UserService {

	@Autowired
	private YtsWebClient ytsWebClient;

	@Autowired
	private UserRepository userRepository;

	public Flux<User> findAllUsers() {
		return userRepository.findAll();
	}

	public Mono<User> findUserById(UUID id) {
		return userRepository.findById(id);
	}

	public Mono<User> createUser(){
		//Create user in YTS
		Mono<ClientUser> clientUser = ytsWebClient.getWebClient().post()
			.uri("/v1/users")
			.header("Content-Type", "application/json")
			.retrieve().bodyToMono(ClientUser.class);

		Mono<User> user = clientUser.map(clientUser1 -> new User(null, clientUser1.getId(), LocalDateTime.now(), clientUser1.getClientId() + "-" + clientUser1.getId()));

		return userRepository.saveAll(user).publishNext();
	}
}
