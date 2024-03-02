package com.r2dbc.orm.sample;

import com.r2dbc.orm.a_second_draft.interfaces.R2dbcOrmRepository;
import com.r2dbc.orm.sample.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class TestClass {
	private final DatabaseClient client;
	R2dbcOrmRepository<User, String> repo = R2dbcOrmRepository.simple(User.class, String.class);


	@PostConstruct
	public void test() {
		System.out.println(repo.getSelectQuery());
		repo.findAll(client)
						.doOnNext(user -> log.info("user: {}", user))
						.subscribe();
	}

}
