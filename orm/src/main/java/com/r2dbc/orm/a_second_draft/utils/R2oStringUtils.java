package com.r2dbc.orm.a_second_draft.utils;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class R2oStringUtils {

  public static String camelToSnake(String string) {
    String regex = "([a-z])([A-Z]+)";

    String replacement = "$1_$2";

    string = string.replaceAll(regex, replacement)
        .toLowerCase();

    return string;
  }

  public static Long stringToLong(String string) {
    return Long.parseLong(string);
  }

  public static String stringUUID() {
    return UUID.randomUUID().toString();
  }

  public static String getOrBlank(Map<String, String> map, String key) {
    Optional<String> optional = Optional.ofNullable(map.get(key));
    return optional.orElse("");
  }

  public static boolean isBlank(String str) {
    return str == null || str.isBlank();
  }
}
