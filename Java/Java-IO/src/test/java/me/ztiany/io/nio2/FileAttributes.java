package me.ztiany.io.nio2;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributeView;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/6/4 11:37
 */
public class FileAttributes {

    @Test
    public void readFileAttributes() throws IOException {
        DosFileAttributeView fileAttributeView = Files.getFileAttributeView(Paths.get("."), DosFileAttributeView.class);
        System.out.println(fileAttributeView.name());

        Object lastModifiedTime = Files.getAttribute(Paths.get("."), "lastModifiedTime");
        System.out.println(lastModifiedTime);

    }

}
