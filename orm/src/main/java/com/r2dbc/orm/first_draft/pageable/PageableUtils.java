package com.r2dbc.orm.first_draft.pageable;

import com.r2dbc.orm.a_second_draft.utils.StringUtils;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Slf4j
public class PageableUtils {

  public static String getSort(Pageable pageable, String prefix) {
    AtomicReference<String> sort = new AtomicReference<>("");

    // 입력된 sort 파라미터를 String 하나로 변경
    pageable.getSort()
            .forEach(order -> 
                sort.set(sort.get() + ", " + putPrefix(prefix) + StringUtils.camelToSnake(order.getProperty()) + " " + order.getDirection().name())
            );

    log.info("sort : " + pageable.getSort());
    
    // 맨 앞 쉼표 제거 및 order by 절 추가
    String order = sort.get().replaceFirst(",", "");
    if (StringUtils.isBlank(order)) return "";
    return " ORDER BY" + order;
  }
  private static String putPrefix(String prefix) {
    return (prefix != null && !prefix.isBlank()) ? (prefix + ".") : "";
  }

  public static String getLimit(Pageable pageable) {

    Integer size = pageable.getPageSize();
    Integer page = pageable.getPageNumber();
    int start = page * size;

    return " LIMIT " + size + " OFFSET " + start;
  }

  public static String getSortAndLimit(Pageable pageable, String prefix) {
    return getSort(pageable, prefix) + getLimit(pageable);
  }

  public static <T> Page<T> getPageImplPaged(List<T> data, Pageable pageable, long totalCount) {
    return new PageImpl<>(data, pageable, totalCount);
  }

  public static <T> PageImpl<T> getPageImpl(List<T> objects, Pageable pageable) {
    int pageNumber = pageable.getPageNumber();
    int size = pageable.getPageSize();
    int fromIndex = (pageNumber) * size;
    int toIndex = Math.min(fromIndex + size, objects.size());
    List<T> workspaceConnections = objects.subList(fromIndex, toIndex);
    return new PageImpl<>(workspaceConnections, pageable, objects.size());
  }
}
