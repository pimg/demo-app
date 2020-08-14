package io.yts.client.demoapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserSite {

	private UUID userSiteId;
	private UUID clientUserId;
	private UUID siteId;
}
