package me.ztiany.compiler.javac;

import com.sun.tools.javac.main.Main;

import java.io.File;

import static me.ztiany.compiler.common.Utils.APP_SOURCE_DIR;
import static me.ztiany.compiler.common.Utils.TARGET_OPTION;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/12/13 22:26
 */
public class JavaMain {

    private static final String TARGET_DIR = "JavaCompiler/build/javac-main/";

    public static void main(String... args) {
        javaToolsInternalApi();
    }

    /**
     * 在没有引入 JSR-199 前，只能使用 javac 源码提供内部 API，这部分API不是标准JAVA的一部分。
     * 官方的警告标注：这不是任何支持的API的一部分。如果您编写依赖于此的代码，则需要您自担风险。此代码及其内部接口如有更改或删除，恕不另行通知。
     */
    private static void javaToolsInternalApi() {
        Main compiler = new Main("javac");
        System.out.println(new File(".").getAbsolutePath());
        new File(TARGET_DIR).mkdirs();
        compiler.compile(new String[]{APP_SOURCE_DIR, TARGET_OPTION, TARGET_DIR});
    }

}
