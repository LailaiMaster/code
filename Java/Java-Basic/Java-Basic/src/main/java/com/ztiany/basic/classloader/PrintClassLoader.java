package com.ztiany.basic.classloader;

import java.net.URL;
import java.net.URLClassLoader;

import sun.misc.Launcher;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 17.8.13 16:12
 */
class PrintClassLoader {

    //vm args: -XX:+TraceClassLoading
    public static void main(String... args) {
        //AppClassLoader 主要加载系统属性“java.class.path”配置下的类文件，也就是环境变量 CLASS_PATH 配置的路径。
        System.out.println("java.class.path=====================================================");
        System.out.println(System.getProperty("java.class.path"));
        System.out.println();
        System.out.println();
        //ExtClassLoader 加载系统属性“java.ext.dirs”配置下类文件
        System.out.println("java.ext.dirs=====================================================");
        System.out.println(System.getProperty("java.ext.dirs"));
        System.out.println();
        System.out.println();
        //BootstrapClassLoader 加载系统属性“sun.boot.class.path”配置下的类文件
        System.out.println("sun.boot.class.path=====================================================");
        System.out.println(System.getProperty("sun.boot.class.path"));
        System.out.println();
        System.out.println();

        //打印AppClassLoader
        ClassLoader appClassLoader = PrintClassLoader.class.getClassLoader();
        URL[] appUrLs = ((URLClassLoader) appClassLoader).getURLs();
        System.out.println("AppClassLoader=====================================================");
        System.out.println("AppClassLoader = " + appClassLoader);
        //ClassLoader.getSystemClassLoader方法无论何时均会返回 ApplicationClassLoader，其只加载classpath下的class文件。
        System.out.println("getSystemClassLoader = " + ClassLoader.getSystemClassLoader());
        for (URL appUrL : appUrLs) {
            System.out.println("url " + appUrL);
        }

        //打印ExtClassLoader
        System.out.println();
        System.out.println("ExtClassLoader=====================================================");
        ClassLoader extraClassLoader = appClassLoader.getParent();
        URL[] extraUrLs = ((URLClassLoader) extraClassLoader).getURLs();
        System.out.println("ExtraClassLoader = " + extraClassLoader);
        for (URL appUrL : extraUrLs) {
            System.out.println("url " + appUrL);
        }

        //打印 BootstrapClassLoader，BootstrapClassLoader 由 C/C++ 语言编写的，它本身属于虚拟机的一部分。因此我们无法在 Java 代码中直接获取它的引用。
        //BootstrapClassLoader 称为启动类加载器，是Java类加载层次中最顶级的加载，负责加载JDK中的核心类库。
        // 如：rt.jar、resource.jar、charsets.jar等，Bootstrap类加载器的加载目录由系统属性（sun.boot.class.path）指定
        System.out.println();
        System.out.println("BootstrapClassLoader=====================================================");
        for (URL url : Launcher.getBootstrapClassPath().getURLs()) {
            System.out.println("url " + url);
        }

    }

}