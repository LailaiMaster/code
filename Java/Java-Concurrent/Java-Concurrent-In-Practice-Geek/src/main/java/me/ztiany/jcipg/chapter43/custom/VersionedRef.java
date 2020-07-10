package me.ztiany.jcipg.chapter43.custom;

//带版本号的对象引用
public final class VersionedRef<T> {

    final T value;
    final long version;

    //构造方法
    public VersionedRef(T value, long version) {
        this.value = value;
        this.version = version;
    }

}