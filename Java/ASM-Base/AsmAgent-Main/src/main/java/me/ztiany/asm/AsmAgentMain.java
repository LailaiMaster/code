package me.ztiany.asm;

/**
 * -javaagent:D:\code\github\Programming-Notes\Java\00-Code\ASM-Base\AsmAgent\build\libs\AsmAgent-1.0.jar
 *
 * @author ztiany
 * Email: ztiany3@gmail.com
 */
public class AsmAgentMain {

    public static void main(String... args) {
        System.out.println("AsmAgentMain running");
        User user = new User();
        user.setAge(12);
        user.setName("Ztiany");
        System.out.println(user);
    }

    public static void _main(String... args) throws Error {
      /*
      java6：
      VirtualMachine.attach()
        VirtualMachine.loadAgent("xxx.jar");
        VirtualMachine vm = VirtualMachine.attach();
        vm.loadAgent(jarFilePath, args);*/
    }

}
