package me.ztiany.agent.test.jdk5;

/**
 * 运行参数： -javaagent:"D:\code\code\Java\ASM-Base\JDK5AgentLib\build\libs\JavaAgent.jar=World Hello"
 *
 * @author ztiany
 * Email: ztiany3@gmail.com
 */
public class JDK5AgentTest {

    public static void main(String... args) throws InterruptedException {
        while (true) {
            System.out.println(getGreeting());
            Thread.sleep(1000L);
        }
    }

    public static String getGreeting() {
        return "hello world";
    }

}
