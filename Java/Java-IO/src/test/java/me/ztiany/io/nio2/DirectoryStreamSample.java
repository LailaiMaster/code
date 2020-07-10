package me.ztiany.io.nio2;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/6/4 11:04
 */
public class DirectoryStreamSample {

    @Test
    public void listFile() throws IOException {
        Path current = Paths.get(".");
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(current, "*.*")) {
            for (Path path : paths) {
                System.out.println(path);
            }
        }
    }

    @Test
    public void listAllFile() throws IOException {
        Path current = Paths.get(".");
        Files.walkFileTree(current, new SvnInfoCleanVisitor());
    }

    public static class SvnInfoCleanVisitor extends SimpleFileVisitor<Path> {

        private boolean cleanMark = false;

        private boolean isSvnFolder(Path dir) {
            return ".svn".equals(dir.getFileName().toString());
        }

        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (isSvnFolder(dir)) {
                cleanMark = true;
            }
            return FileVisitResult.CONTINUE;
        }

        public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
            if (e == null && cleanMark) {
                Files.delete(dir);
                if (isSvnFolder(dir)) {
                    cleanMark = false;
                }
            }
            return FileVisitResult.CONTINUE;
        }

        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (cleanMark) {
                Files.setAttribute(file, "dos:readonly", false);
                Files.delete(file);
            }
            return FileVisitResult.CONTINUE;
        }
    }

}
