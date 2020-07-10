package me.ztiany.jcipg.chapter42;

import akka.actor.*;

public class HelloMain {

    public static void main(String[] args) {
        System.out.println("--------------------------------------------------");
        //创建Actor系统
        ActorSystem system = ActorSystem.create("HelloSystem");
        //创建HelloActor
        ActorRef helloActor = system.actorOf(Props.create(HelloActor.class));
        //发送消息给HelloActor
        helloActor.tell("Actor", ActorRef.noSender());
        //关闭系统
        system.shutdown();
    }

    static class HelloActor extends UntypedActor {
        @Override
        public void onReceive(Object message) {
            System.out.println("Hello " + message);
        }
    }

}