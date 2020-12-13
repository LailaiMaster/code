package me.ztiany.compiler.jsr199;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import static me.ztiany.compiler.common.Utils.APP_SOURCE_DIR;
import static me.ztiany.compiler.common.Utils.TARGET_OPTION;
import static me.ztiany.compiler.common.Utils.doCompile;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/12/13 22:30
 */
public class JSR199Main {

    private static final String TARGET_DIR1 = "JavaCompiler/build/jsr199/1/";
    private static final String TARGET_DIR2 = "JavaCompiler/build/jsr199/2/";

    public static void main(String... args) {
        //jsr199_1();
        jsr199_2();
    }

    /* JSR-199（Java Compiler API） 引入了 Java 编译器 API，`javax.tools.JavaCompiler` 就是其中之一 */
    private static void jsr199_1() {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        int run = javaCompiler.run(null, null, null, APP_SOURCE_DIR, "-d", TARGET_DIR1);
        System.out.println(run);
    }

    private static void jsr199_2() {
        doCompile(Collections.singletonList(new File(APP_SOURCE_DIR)), Arrays.asList(TARGET_OPTION, TARGET_DIR2), null);
    }

}
