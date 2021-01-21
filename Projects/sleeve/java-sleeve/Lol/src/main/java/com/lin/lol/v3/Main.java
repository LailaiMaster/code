package com.lin.lol.v3;

import java.util.Scanner;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/16 0:16
 */
public class Main {

    public static void main(String... args) {
        //根据输入的英雄名称创建英雄
        String name = Main.getPlayInput();
        ISkill iSkill = HeroFactory.newHero(name);
        if (iSkill != null) {
            iSkill.run();
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
