package com.ztiany.basic.encoding;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * 字符编码相关知识
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/3/11 11:00
 */
public class JavaEncoding {

    /*
    相关链接：
                字符编码笔记：ASCII，Unicode 和 UTF-8：http://www.ruanyifeng.com/blog/2007/10/ascii_unicode_and_utf-8.html
                【字符编码】Java编码格式探秘：https://www.cnblogs.com/leesf456/p/5313408.html
                字符、编码和Java中的编码：https://www.jianshu.com/p/1b00ca07b003
                分享一下我所了解的字符编码知识：https://www.jianshu.com/p/2d4ad873b39f
                what-does-it-mean-to-say-java-modified-utf-8-encoding：https://stackoverflow.com/questions/7921016/what-does-it-mean-to-say-java-modified-utf-8-encoding

    Java运行时环境和外部环境使用的编码是不一样的：

        1. 外部环境的编码可以使用 Charset.defaultcharset() 获取。如果没有指定外部环境编码，就是操作系统的默认编码。
            jvm 操作 I/O流 时，如果不指定编码，也会使用这个编码，可以在启动 Java 时使用 -Dfile.encoding=xxx 设置。
            通过 System.setProperty("file.encoding","GBK") 能修改这个值，但由于 jvm 一旦启动就不能修改jvm默认字符集，所以修改这个值并没有什么作用。

        2. 编译时的编码转换：Java的源文件可以是任意的编码，但是在编译的时候，Javac编译器默认会使用操作系统平台的编码解析字符。
            如果 Java 源文件的编与默认编码不一致，就需要通过 -encoding 参数指定。

        3. Java 编译后的 class 文件，使用 modified UTF-8 编码存储字符常量。

        4. 运行时数据中的 UTF-16：JVM中运行时数据都是使用 UTF-16 进行编码的。

        5. modified UTF-8：后期为了支持更多编码，有了 modified UTF-8，modified UTF-8 是对 UTF-16 的再编码。
     */

    public static void main(String... args) throws UnsupportedEncodingException {
        /*下面是本 class 文件中的部分数据，里面包含“中”的 utf-8 编码：E4 B8 AD

                    ad20 696e 206a 766d 203d 2001 0003 e4b8
                    ad0c 0074 0075 0700 760c 0077 0078 0100
                    0be4 b8ad 2055 5446 3820 3d20 0100 0555
        */
        System.out.println("Charset.defaultCharset().name(): " + Charset.defaultCharset().name());
        System.out.println("file.encoding = " + System.getProperty("file.encoding", "un-know"));
        System.out.println("=========================================================");
        System.out.println("中 in jvm = " + Integer.toHexString("中".codePointAt(0)));
        System.out.println("=========================================================");
        System.out.println("中 default = " + getCode("中", ""));//跟随Charset.defaultCharset()编码
        System.out.println("中 UTF8 = " + getCode("中", "UTF-8"));
        System.out.println("中 UTF16 = " + getCode("中", "UTF-16"));
        System.out.println("中 UNICODE = " + getCode("中", "UNICODE"));
        System.out.println("中 GBK = " + getCode("中", "GBK"));
    }

    public static String getCode(String content, String format) throws UnsupportedEncodingException {
        byte[] bytes;
        if (format.isEmpty()) {
            bytes = content.getBytes();
        } else {
            bytes = content.getBytes(format);
        }
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toHexString(aByte & 0xff).toUpperCase()).append(" ");
        }
        return sb.toString();
    }

}