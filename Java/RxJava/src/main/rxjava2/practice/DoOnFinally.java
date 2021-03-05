package practice;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import utils.RxLock;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/12/26 17:42
 */
public class DoOnFinally {

    public static void main(String... args) {

        Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> call() throws Exception {
                RxLock.sleep(1000);
                return Observable.just(1);
            }
        })
                .subscribeOn(Schedulers.io())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("doFinally 1 "+Thread.currentThread());
                    }
                })
                .observeOn(Schedulers.computation())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("doFinally 2 "+Thread.currentThread());
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer o) throws Exception {
                        System.out.println("accept "+Thread.currentThread());
                    }
                });

        RxLock.sleep(10000);
    }

}
