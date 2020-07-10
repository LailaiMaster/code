package me.ztiany.jcipg.chapter42;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CounterMain {

    //累加器
    static class CounterActor extends UntypedActor {

        private int counter = 0;

        @Override
        public void onReceive(Object message) {
            //如果接收到的消息是数字类型，执行累加操作，
            //否则打印counter的值
            if (message instanceof Number) {
                counter += ((Number) message).intValue();
            } else {
                System.out.println(counter);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //创建Actor系统
        ActorSystem system = ActorSystem.create("HelloSystem");
        //4个线程生产消息
        ExecutorService es = Executors.newFixedThreadPool(4);
        //创建CounterActor
        ActorRef counterActor = system.actorOf(Props.create(CounterActor.class));
        //生产4*100000个消息
        for (int i = 0; i < 4; i++) {
            es.execute(() -> {
                for (int j = 0; j < 100000; j++) {
                    counterActor.tell(1, ActorRef.noSender());
                }
            });
        }
        //关闭线程池
        es.shutdown();
        //等待CounterActor处理完所有消息
        Thread.sleep(1000);
        //打印结果
        counterActor.tell("", ActorRef.noSender());
        //关闭Actor系统
        system.shutdown();
    }

}