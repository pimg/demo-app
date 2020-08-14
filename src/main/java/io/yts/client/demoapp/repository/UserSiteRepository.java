package io.yts.client.demoapp.repository;

import io.yts.client.demoapp.model.UserSite;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface UserSiteRepository extends ReactiveCrudRepository<UserSite, UUID> {
}
