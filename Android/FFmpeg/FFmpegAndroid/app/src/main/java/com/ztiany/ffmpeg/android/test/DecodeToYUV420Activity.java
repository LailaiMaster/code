package com.ztiany.ffmpeg.android.test;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ztiany.ffmpeg.android.FFmpeg;

import java.io.File;

/**
 * @author Ztiany
 *         Email: ztiany3@gmail.com
 *         Date : 2018-03-11 22:22
 */
public class DecodeToYUV420Activity extends AppCompatActivity {

    private boolean mIsWorking = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        Button child = new Button(this);
        frameLayout.addView(child, params);
        setContentView(frameLayout);
        child.setText("进行解码");
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDecode();
            }
        });
    }

    private void doDecode() {
        if (mIsWorking) {
            return;
        }
        mIsWorking = true;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                File input = new File(Environment.getExternalStorageDirectory(), "input.mp4");
                File out = new File(Environment.getExternalStorageDirectory(), "out.yuv");
                if (!input.exists()) {
                    Toast.makeText(DecodeToYUV420Activity.this, input.getAbsolutePath() + "不存在", Toast.LENGTH_SHORT).show();
                    return null;
                }
                new FFmpeg().decode2YUV420(input.getAbsolutePath(), out.getAbsolutePath());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mIsWorking = false;
                Toast.makeText(DecodeToYUV420Activity.this, "解码完成", Toast.LENGTH_SHORT).show();
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
