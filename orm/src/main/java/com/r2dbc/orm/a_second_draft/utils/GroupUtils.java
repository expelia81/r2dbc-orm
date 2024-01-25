package com.r2dbc.orm.a_second_draft.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GroupUtils {

  /** 단일 리스트를 특정 기준으로 그루핑해 2중 리스트로 만들어주는 함수. 기준은 i -> i.getOrgId() / UserDomain::getId 같은 형태로 작성하면된다.
   * @param targetList : 그루핑 타겟. 이 리스트의 제네릭 값을 기준으로 그루핑 함수를 사용하게 되므로, 제네릭 타입을 가지는 리스트여야만한다.
   * @param groupingFunction : Target List에서 grouping 기준이 될 값의 타입. (보통 String이 될 것임) */
  public static <K,V> List<List<V>> groupByFunction(List<V> targetList, Function<V,K> groupingFunction) {
    Map<K, List<V>> map = getGroups(targetList, groupingFunction);
    return new ArrayList<>(map.values());
  }

  /** 2중리스트로 만들어진 그룹리스트의 그룹들을 다시 단일 리스트로 합치는 함수.(즉, 2차 배열 -> 1차 배열 )
   * @param targetList : 그루핑 타겟. 이 리스트의 제네릭 값을 기준으로 그루핑 함수를 사용하게 되므로, 제네릭 타입을 가지는 리스트여야만한다.
   * @param groupingFunction : Target List에서 grouping 기준이 될 값의 타입. (보통 String이 될 것임)
   * @param mergeFunction : 병합 기준이 될 함수를 의미한다. 예시) 그루핑이 끝난 후, 그루핑된 리스트들이 가지는 별개의 값이 존재할 경우 합치는 로직이다. */
  public static <K,V,R> List<R> groupAndMergeByFunction(List<V> targetList, Function<V,K> groupingFunction, Function<List<V>,R> mergeFunction) {
    Map<K, List<V>> map = getGroups(targetList, groupingFunction);

    List<R> result = new ArrayList<>();
    map.forEach((key, value) -> result.add(mergeFunction.apply(value)));

    return result;
  }

  private static <K, V> Map<K, List<V>> getGroups(List<V> targetList,
      Function<V, K> groupingFunction) {
    Map<K, List<V>> map = new LinkedHashMap<>();
    targetList.forEach(target -> {
      K key = groupingFunction.apply(target);
      if (!map.containsKey(key)) {
        List<V> list = new ArrayList<>();
        list.add(target);
        map.put(key, list);
      } else {
        map.get(key).add(target);
      }
    });
    return map;
  }

}
