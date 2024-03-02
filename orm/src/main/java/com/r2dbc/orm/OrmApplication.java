package com.r2dbc.orm;

import com.r2dbc.orm.a_second_draft.interfaces.R2dbcOrmRepository;
import com.r2dbc.orm.sample.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrmApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrmApplication.class, args);
  }

}
