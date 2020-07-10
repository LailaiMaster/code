package com.dongnao.player;


import android.support.annotation.NonNull;

public interface SystemMediaSelectorResultListener {

    void onTakeSuccess(@NonNull String path);

    default void onTakeFail() {
    }

    default void onCancel() {
    }

}