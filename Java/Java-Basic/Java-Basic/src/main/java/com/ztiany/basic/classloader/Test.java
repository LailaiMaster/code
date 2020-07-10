package com.ztiany.basic.classloader;

public class Test {

    public static void main(String... args) {
        new Test().printCode();
    }

    private void printCode() {
        int superCode = super.hashCode();
        System.out.println("superCode: " + superCode);
        int selfCode = hashCode();
        System.out.println("selfCode: " + selfCode);
    }

    @Override
    public int hashCode() {
        return 100;
    }

}