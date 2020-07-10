package com.ztiany.basic.java8;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/*
 * Stream 的操作步骤：
 *
 * 1. 创建 Stream
 * 2. 中间操作
 * 3. 终止操作
 */
public class StreamAPI1 {

    private List<Employee> mEmployees = Arrays.asList(
            new Employee(102, "李四", 59, 6666.66),
            new Employee(101, "张三", 18, 9999.99),
            new Employee(103, "王五", 28, 3333.33),
            new Employee(104, "赵六", 8, 7777.77),
            new Employee(104, "赵六", 8, 7777.77),
            new Employee(104, "赵六", 8, 7777.77),
            new Employee(105, "田七", 38, 5555.55)
    );

    //2. 中间操作
    public void test1() {
        Stream<String> str = mEmployees.stream().map(Employee::getName);

        System.out.println("-------------------------------------------");

        List<String> strList = Arrays.asList("aaa", "bbb", "ccc", "ddd", "eee");

        Stream<String> stream = strList.stream().map(String::toUpperCase);

        stream.forEach(System.out::println);

        Stream<Stream<Character>> stream2 = strList.stream().map(StreamAPI1::filterCharacter);

        stream2.forEach((sm) -> sm.forEach(System.out::println));

        System.out.println("---------------------------------------------");

        Stream<Character> stream3 = strList.stream().flatMap(StreamAPI1::filterCharacter);

        stream3.forEach(System.out::println);
    }

    private static Stream<Character> filterCharacter(String str) {
        List<Character> list = new ArrayList<>();

        for (Character ch : str.toCharArray()) {
            list.add(ch);
        }

        return list.stream();
    }

    /*
        sorted()——自然排序
        sorted(Comparator com)——定制排序
     */
    public void test2() {
        mEmployees.stream()
                .map(Employee::getName)
                .sorted()
                .forEach(System.out::println);

        System.out.println("------------------------------------");

        mEmployees.stream()
                .sorted((x, y) -> {
                    if (x.getAge() == y.getAge()) {
                        return x.getName().compareTo(y.getName());
                    } else {
                        return Integer.compare(x.getAge(), y.getAge());
                    }
                }).forEach(System.out::println);
    }

}