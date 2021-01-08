package me.ztiany.compiler.jsr269;


import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import static me.ztiany.compiler.common.Utils.APP_SOURCE_DIR;
import static me.ztiany.compiler.common.Utils.TARGET_OPTION;
import static me.ztiany.compiler.common.Utils.doCompile;

/**
 * @author ztiany
 * Email: ztiany3@gmail.com
 */

public class JSR269Main {

    private static final String TARGET_DIR = "JavaCompiler/build/jsr269/";

    public static void main(String... args) {
        jsr269();
    }

    /*
     * JSR-269（Pluggable Annotation Processing API）引入了编译器注解处理：
     *      整个类文件被扫描，包括内部类以及全部方法、构造方法和字段。注解处理在填充符号表之后进行。
     *      ElementScanner 类扫描的 Element 其实就是符号 Symbol：public abstract class Symbol extends AnnoConstruct implements Element
     */
    private static void jsr269() {
        doCompile(Collections.singletonList(new File(APP_SOURCE_DIR)), Arrays.asList(TARGET_OPTION, TARGET_DIR), Collections.singletonList(new ElementVisitProcessor()));
    }

}
