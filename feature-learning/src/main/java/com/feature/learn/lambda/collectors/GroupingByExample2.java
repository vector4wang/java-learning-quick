package com.feature.learn.lambda.collectors;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroupingByExample2 {
    public static void main(String[] args) {
        Stream<String> s = Stream.of("apple", "banana", "orange");
        //cascaded group by
        Map<Integer, Long> map = s.collect(
                Collectors.groupingBy(String::length, Collectors.counting()));
        System.out.println(map);
    }
}