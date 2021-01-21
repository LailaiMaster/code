package com.lin.lol.v3;


import com.lin.lol.v3.hero.Diana;
import com.lin.lol.v3.hero.Irelia;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/16 0:38
 */
public class HeroFactory {

    static ISkill newHero(String name) {
        ISkill iSkill = null;
        switch (name) {
            case "Diana":
                iSkill = new Diana();
                break;
            case "Irelia":
                iSkill = new Irelia();
                break;
        }
        return iSkill;
    }

}
