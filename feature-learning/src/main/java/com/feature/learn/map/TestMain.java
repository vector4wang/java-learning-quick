package com.feature.learn.map;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author vector
 * @date: 2018/11/30 0030 9:25
 */
public class TestMain {
    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put("A", "A1");
        map.put("B", "A2");
        map.put("C", "A3");
        map.put("D", "A4");
        map.put("E", "A5");

        map.containsKey("A");
        map.get("B");
        map.remove("C");
        map.hashCode();
        map.keySet();

        String h = map.putIfAbsent("H", "1");
        System.out.println(h);
        System.out.println(map);


		ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();
		concurrentHashMap.put("a", "b");
		Hashtable<String, String> hashtable = new Hashtable<>();
		hashtable.put("a", "c");
    }
}
