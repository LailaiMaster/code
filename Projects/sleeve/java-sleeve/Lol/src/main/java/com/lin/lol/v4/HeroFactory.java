package com.lin.lol.v4;


/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/16 0:57
 */
public class HeroFactory {

    static ISkill newHero(String name) {
        ISkill iSkill = null;
        String classPath = "com.lin.lol.v4.hero.".concat(name);
        try {
            iSkill = (ISkill) Class.forName(classPath).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return iSkill;
    }

}
