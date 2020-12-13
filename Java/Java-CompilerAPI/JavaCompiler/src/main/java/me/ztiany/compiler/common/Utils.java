package me.ztiany.compiler.common;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2019/4/10 15:01
 */
public class Utils {

    public static final String APP_SOURCE_DIR = "JavaCompiler/src/main/java/me/ztiany/compiler/common/TargetClass.java";
    public static final String TARGET_OPTION = "-d";

    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void doCompile(List<File> source, List<String> args, List<Processor> processors) {
        //编译器
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

        //编译过程中诊断信息收集器
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        //标准 Java 文件管理器
        StandardJavaFileManager standardFileManager = javaCompiler.getStandardFileManager(diagnostics, null, null);

        //编译单元
        Iterable<? extends JavaFileObject> compilationUnits = standardFileManager.getJavaFileObjectsFromFiles(source);

        //构建编译任务
        JavaCompiler.CompilationTask task = javaCompiler.getTask(null, standardFileManager, diagnostics, args, null, compilationUnits);

        //设置编译处理器
        if (processors != null && processors.size() > 0) {
            task.setProcessors(processors);
        }

        //执行编译任务
        task.call();

        //打印编译过程中的诊断信息
        System.out.println();
        System.out.println("--------------------------------------------------------------------------------");
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.format("Error on line %d in %s\n%s\n", diagnostic.getLineNumber(), diagnostic.getSource(), diagnostic.getMessage(null));
        }

        //关闭资源
        close(standardFileManager);
    }

}
