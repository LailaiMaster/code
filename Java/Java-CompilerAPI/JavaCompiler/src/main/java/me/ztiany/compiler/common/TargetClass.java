package me.ztiany.compiler.common;

/**
 * @author ztiany
 * Email: ztiany3@gmail.com
 */
public class TargetClass {

    public static void main(String... args) {
        System.out.println("app is running......");
        doWork(args);
        System.out.println("app is exit......");
    }

    @HelloTag
    private static void doWork(String[] args) {
        assert args != null;
        System.out.println("App.doWork");
    }

}
