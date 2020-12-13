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
 * Date 2020/12/13 23:13
 */
public class SunSourceMain {

    private static final String TARGET_DIR = "JavaCompiler/build/sun-source/";

    /**
     * com.sun.source.* 包下暴露的 API 对语法树只能做只读操作，功能有限。
     */
    public static void main(String... args) {
        treeScanner();
    }

    /*
     * 语法树扫描：com.sun.source.* 提供了扫描器 TreeScanner。获取语法树是通过工具类 Trees 的 getTree 方法完成的。
     *  com.sun.source.* 包下暴露的 API 对语法树只能做只读操作，功能有限。
     */
    private static void treeScanner() {
        doCompile(Collections.singletonList(new File(APP_SOURCE_DIR)), Arrays.asList(TARGET_OPTION, TARGET_DIR), Collections.singletonList(new SunSourceAPIVisitTreeProcessor()));
    }

}
