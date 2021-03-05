package com.ztiany.basic.generic.reflect;


abstract class AbsCallback<T> {

    public void onAfter(T t) {

    }

    public abstract T parseNetworkResponse(Object response) throws Exception;

}