package a.java.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashMapTest {

    public static void main(String[] args) {
        //testOrder();
        testAccessOrder();
    }

    private static void testAccessOrder() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>(16,0.75F,true){

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                return this.size() > 3;
            }
        };

        map.put("d", 4);
        map.put("c", 3);
        map.put("b", 2);
        map.put("a", 1);

        map.forEach((key, value) -> System.out.println(key));
    }

    private static void testOrder() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("d", 4);
        map.put("c", 3);
        map.put("b", 2);
        map.put("a", 1);
        map.forEach((key, value) -> System.out.println(key));
    }

}
