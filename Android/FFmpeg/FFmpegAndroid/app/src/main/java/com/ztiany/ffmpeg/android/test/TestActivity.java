package com.ztiany.ffmpeg.android.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.ztiany.ffmpeg.android.FFmpeg;

/**
 * @author Ztiany
 *         Email: ztiany3@gmail.com
 *         Date : 2018-03-11 01:06
 */
public class TestActivity extends AppCompatActivity {

    private AppCompatTextView mChild;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scrollView = new ScrollView(this);
        mChild = new AppCompatTextView(this);
        scrollView.addView(mChild, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(scrollView);
        showFFmpegInfo();
    }

    private void showFFmpegInfo() {
        try {
            String info = new FFmpeg().checkFFmepg();
            mChild.setText(info);
        } catch (Error error) {
            error.printStackTrace();
        }
    }


}
