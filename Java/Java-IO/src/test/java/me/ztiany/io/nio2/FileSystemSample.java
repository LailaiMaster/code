package me.ztiany.io.nio2;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/6/4 15:39
 */
public class FileSystemSample {

    @Test
    public void listAvailableFSP() {
        for (FileSystemProvider installedProvider : FileSystemProvider.installedProviders()) {
            System.out.println(installedProvider.getScheme());
        }
    }

    private void addFileToZip(File zipFile, File fileToAdd) throws IOException {
        File tempFile = File.createTempFile(zipFile.getName(), null);
        tempFile.delete();
        zipFile.renameTo(tempFile);

        try (ZipInputStream input = new ZipInputStream(new FileInputStream(tempFile));
             ZipOutputStream output = new ZipOutputStream(new FileOutputStream(zipFile))) {

            ZipEntry entry = input.getNextEntry();
            byte[] buf = new byte[8192];

            while (entry != null) {
                String name = entry.getName();
                if (!name.equals(fileToAdd.getName())) {
                    output.putNextEntry(new ZipEntry(name));
                    int len = 0;
                    while ((len = input.read(buf)) > 0) {
                        output.write(buf, 0, len);
                    }
                }
                entry = input.getNextEntry();
            }

            try (InputStream newFileInput = new FileInputStream(fileToAdd)) {
                output.putNextEntry(new ZipEntry(fileToAdd.getName()));
                int len = 0;
                while ((len = newFileInput.read(buf)) > 0) {
                    output.write(buf, 0, len);
                }
                output.closeEntry();
            }
        }

        tempFile.delete();
    }

    @Test
    public void testAddFileToZip2() throws IOException {
        addFileToZip2(new File("jars/retrofit-2.5.0.jar"), new File("jars/test01.txt"));
    }

    private void addFileToZip2(File zipFile, File fileToAdd) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        try (FileSystem fs = FileSystems.newFileSystem(URI.create("jar:" + zipFile.toURI()), env)) {
            Path pathToAddFile = fileToAdd.toPath();
            Path pathInZipfile = fs.getPath("/" + fileToAdd.getName());
            Files.copy(pathToAddFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Test
    public void testCopyJar() throws IOException {
        copyJar(Paths.get("jars/retrofit-2.5.0.jar"), Paths.get("jars/retrofit-2.5.0-copy.jar"));
    }

    private void copyJar(Path input, Path output) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        URI inputUri = URI.create("jar:" + input.toUri());
        URI outputUri = URI.create("jar:" + output.toUri());

        try (FileSystem inputSystem = FileSystems.newFileSystem(inputUri, env);
             FileSystem outSystem = FileSystems.newFileSystem(outputUri, env);) {
            Path inputRootPath = getOnlyElement(inputSystem.getRootDirectories());
            Path outputRootPath = getOnlyElement(outSystem.getRootDirectories());
            doCopy(inputRootPath, outputRootPath);
        }
    }

    private void doCopy(Path inputRootPath, Path outputRootPath) {
        System.out.println("inputRootPath = " + inputRootPath + ", outputRootPath = " + outputRootPath);

        try {
            Files.walkFileTree(inputRootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path outputPath = toOutputPath(outputRootPath, inputRootPath, dir);
                    Files.createDirectories(outputPath);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    System.out.println(file);
                    Path outputPath = toOutputPath(outputRootPath, inputRootPath, file);
                    Files.copy(file, outputPath);
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> T getOnlyElement(Iterable<T> iterable) {
        for (T t : iterable) {
            return t;
        }
        return null;
    }

    private static Path toOutputPath(Path outputRoot, Path inputRoot, Path inputPath) {
        Path relativize = inputRoot.relativize(inputPath);//相对化。
        return outputRoot.resolve(relativize);//把 relativize 添加到 outputRoot 后面。
    }

}
