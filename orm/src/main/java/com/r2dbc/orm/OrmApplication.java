package com.r2dbc.orm;

import com.r2dbc.orm.a_second_draft.interfaces.R2dbcOrmRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrmApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrmApplication.class, args);
    R2dbcOrmRepository<Integer, Object> repo = R2dbcOrmRepository.simple(Integer.class, Object.class);

    repo.findById(1, null)
            .subscribe();
  }

}
