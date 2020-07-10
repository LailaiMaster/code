package me.ztiany.io.nio2;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/6/4 10:59
 */
public class PathSample {

    @Test
    public void usePath() {
        Path path1 = Paths.get("folder1", "sub1");
        Path path2 = Paths.get("folder2", "sub2");
        System.out.println(path1.resolve(path2));//folder1\sub1\folder2\sub2
        System.out.println(path1.resolveSibling(path2));//folder1\folder2\sub2
        System.out.println(path1.relativize(path2));//..\..\folder2\sub2
        System.out.println(path1.subpath(0, 1));//folder1
        System.out.println(path1.startsWith(path2));//false
        System.out.println(path1.endsWith(path2));//false
        System.out.println(Paths.get("folder1/./../folder2/my.text").normalize());//folder2\my.text
    }

}
