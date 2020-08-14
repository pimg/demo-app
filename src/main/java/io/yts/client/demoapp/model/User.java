package io.yts.client.demoapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	Long id;
	UUID clientUserId;
	LocalDateTime createdAt;
	String name;
}
