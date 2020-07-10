package me.ztiany.jcipg.chapter43;

import org.multiverse.api.StmUtils;
import org.multiverse.api.references.TxnLong;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.multiverse.api.StmUtils.atomic;

public class TransferMain {

    public static void main(String[] args) {
        testNoSafe();
    }

    private static void testNoSafe() {
        AccountNoSafe accountA = new AccountNoSafe(10000);
        AccountNoSafe accountB = new AccountNoSafe(20000);

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            executorService.execute(() -> {
                accountA.transfer(accountB, random.nextInt(100));
            });
        }

        System.out.println("result " + (accountA.balance + accountB.balance));
    }

    private static void testSafe() {
        Account accountA = new Account(10000);
        Account accountB = new Account(20000);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Random random = new Random();
        for (int i = 0; i < 5000; i++) {
            executorService.execute(() -> {
                accountA.transfer(accountB, random.nextInt(100));
            });
        }

        System.out.println("result " + (accountA.balance.atomicGet() + accountB.balance.atomicGet()));
    }

    static class AccountNoSafe {
        //余额
        private long balance;

        //构造函数
        public AccountNoSafe(long balance) {
            this.balance = balance;
        }

        //转账
        public void transfer(AccountNoSafe to, long amt) {
            if (this.balance > amt) {
                this.balance -= amt;
                to.balance += amt;
            }
        }
    }

    static class Account {
        //余额
        private TxnLong balance;

        //构造函数
        public Account(long balance) {
            this.balance = StmUtils.newTxnLong(balance);
        }

        //转账
        public void transfer(Account to, int amt) {
            //原子化操作
            atomic(() -> {
                if (this.balance.get() > amt) {
                    this.balance.decrement(amt);
                    to.balance.increment(amt);
                }
            });
        }
    }

}