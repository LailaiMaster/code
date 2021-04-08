package a.java.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/8/25 10:53
 */
public class HashMapTest {

    public static void main(String... args) {
        aspect1Hash();
        //aspect2GetFromHashMap();

        testHashCode();
    }

    private static class Person {

        String name;
        int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            if (age != person.age) return false;
            return Objects.equals(name, person.name);
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + age;
            return result;
        }
    }

    private static void testHashCode() {
        Map<Person, String> map = new HashMap<>();
        Person person = new Person("Zhan", 30);
        map.put(person, "TianYou");
        System.out.println(person.hashCode());
        System.out.println(map.get(person));//TianYou
        person.age++;
        System.out.println(person.hashCode());
        System.out.println(map.get(person));//null
    }

    private static class HashCollision {

        private final String value;

        private HashCollision(String value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return 96;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof HashCollision)) return false;
            HashCollision that = (HashCollision) o;
            return Objects.equals(value, that.value);
        }
    }

    private static void aspect2GetFromHashMap() {
        HashMap<String, String> iiMap = new HashMap<>();
        HashMap<HashCollision, String> isMap = new HashMap<>();

        for (int i = 0; i < 31; i++) {
            isMap.put(new HashCollision(String.valueOf(i)), String.valueOf(i));
        }

        System.out.println(isMap.get(new HashCollision("b")));
    }

    /*HashMap 的计算研究*/
    private static void aspect1Hash() {
        nonHighBitsMask();
        hashStepByStep();
    }

    /*高位不参与运算，则计算得到的索引分布将不够均匀*/
    private static void nonHighBitsMask() {
        System.out.println("===================== compute in dec ===========================");
        int h = 15, length = 16;
        System.out.println(h & (length - 1));
        h = 15 + 16;
        System.out.println(h & (length - 1));
        h = 15 + 16 + 16;
        System.out.println(h & (length - 1));
        h = 15 + 16 + 16 + 16;
        System.out.println(h & (length - 1));

        System.out.println("===================== compute in binary===========================");
        //0001111 = 15
        System.out.println(Integer.parseInt("0001111", 2) & Integer.parseInt("0001111", 2));
        System.out.println(Integer.parseInt("0011111", 2) & Integer.parseInt("0001111", 2));
        System.out.println(Integer.parseInt("0111111", 2) & Integer.parseInt("0001111", 2));
        System.out.println(Integer.parseInt("1111111", 2) & Integer.parseInt("0001111", 2));
    }

    private static void hashStepByStep() {
        index(hash("15"), 16);
        index(hash("31"), 16);
        index(hash("47"), 16);
        index(hash("63"), 16);
    }

    private static int hash(Object key) {
        if (key == null) {
            return 0;
        }

        int h = key.hashCode();
        int rh = h >>> 16;
        System.out.println("===================== hash(" + key + ") ===========================");
        System.out.println("key                  \t\t" + key);
        System.out.println("h=key.hashCode()     \t\t" + toBinary(h) + "\t\t" + h);
        System.out.println("rh=h>>>16            \t\t" + toBinary(rh) + "\t\t" + rh);

        return h ^ rh;
    }

    private static int index(int hash, int n) {
        System.out.println("===================== index(" + hash + ", " + n + ") ===========================");
        System.out.println("hash=h ^ rh          \t\t" + toBinary(hash) + "\t\t" + hash);
        System.out.println("n    =               \t\t" + toBinary(n) + "\t\t" + (n));
        System.out.println("n - 1=               \t\t" + toBinary(n - 1) + "\t\t" + (n - 1));
        int index = (n - 1) & hash;
        System.out.println("index=(n - 1) & hash \t\t" + toBinary(index) + "\t\t" + index);
        System.out.println("index=(hash % " + n + " )  \t\t" + toBinary(index) + "\t\t" + index);
        System.out.println();
        return index;
    }

    private static String toBinary(int h) {
        return Integer.toBinaryString(h);
    }

}
