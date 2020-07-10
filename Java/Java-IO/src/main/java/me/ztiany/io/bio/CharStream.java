package me.ztiany.io.bio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/6/3 15:16
 */
public class CharStream {

    public static void main(String... args) throws FileNotFoundException {
        InputStreamReader inputStreamReader = new InputStreamReader(
                new FileInputStream("a.txt"),
                StandardCharsets.UTF_8);
    }

}
