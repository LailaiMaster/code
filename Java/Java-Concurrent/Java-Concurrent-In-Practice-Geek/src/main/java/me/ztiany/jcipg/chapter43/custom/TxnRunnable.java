package me.ztiany.jcipg.chapter43.custom;

@FunctionalInterface
public interface TxnRunnable {
  void run(Txn txn);
}