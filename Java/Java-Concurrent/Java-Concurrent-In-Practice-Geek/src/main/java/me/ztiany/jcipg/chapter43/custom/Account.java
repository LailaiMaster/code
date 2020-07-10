package me.ztiany.jcipg.chapter43.custom;

class Account {
    //余额
    private TxnRef<Integer> balance;

    //构造方法
    public Account(int balance) {
        this.balance = new TxnRef<>(balance);
    }

    //转账操作
    public void transfer(Account target, int amt) {
        STM.atomic((txn) -> {
            Integer from = balance.getValue(txn);
            balance.setValue(from - amt, txn);
            Integer to = target.balance.getValue(txn);
            target.balance.setValue(to + amt, txn);
        });
    }

}