package me.ztiany.opencv.idrecognizing;


import androidx.annotation.NonNull;

public interface SystemMediaSelectorResultListener {

    void onTakeSuccess(@NonNull String path);

    default void onTakeFail() {
    }

    default void onCancel() {
    }

}