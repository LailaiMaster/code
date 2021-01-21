package com.lin.lol.v1;

import com.lin.lol.v1.hero.Diana;
import com.lin.lol.v1.hero.Irelia;

import java.util.Scanner;

/**
 * 英雄联盟
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/16 0:09
 */
public class Main {

    public static void main(String... args) {
        //根据输入的英雄名称创建英雄
        String name = Main.getPlayInput();
        switch (name) {
            case "Diana":
                Diana diana = new Diana();
                diana.q();
                break;
            case "Irelia":
                Irelia irelia = new Irelia();
                irelia.q();
                break;
        }
    }

    /**
     * 获取输入的英雄名称
     */
    private static String getPlayInput() {
        System.out.println("Enter a Hero's name");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

}
