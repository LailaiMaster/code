package me.ztiany.compiler.sun;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import static me.ztiany.compiler.common.Utils.APP_SOURCE_DIR;
import static me.ztiany.compiler.common.Utils.TARGET_OPTION;
import static me.ztiany.compiler.common.Utils.doCompile;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/12/13 22:47
 */
public class SunToolsMain {

    private static final String TARGET_DIR = "JavaCompiler/build/sun-tools/";

    /**
     * com.sun.tools.javac.* 内部 API 和编译器的实现类库中，提供了编译器操作 AST 的功能，从而可以在源码编译器修改被编译的源码。
     */
    public static void main(String... args) {
        // allNodesScanner();
         assertToIfThrow();
        //clearLog();
    }

    /* 内部API，访问所有 AST 节点   */
    private static void allNodesScanner() {
        doCompile(Collections.singletonList(new File(APP_SOURCE_DIR)), Arrays.asList(TARGET_OPTION, TARGET_DIR), Collections.singletonList(new SunToolsAPIVisitNodeProcessor()));
    }

    /* 示例：断言修改为 if throw */
    private static void assertToIfThrow() {
        doCompile(Collections.singletonList(new File(APP_SOURCE_DIR)), Arrays.asList(TARGET_OPTION, TARGET_DIR), Collections.singletonList(new SunToolsAPIAssertToIfThrowProcessor()));
    }

    private static void clearLog() {
        doCompile(Collections.singletonList(new File(APP_SOURCE_DIR)), Arrays.asList(TARGET_OPTION, TARGET_DIR), Collections.singletonList(new SunToolsAPIClearLogProcessor()));
    }

}
