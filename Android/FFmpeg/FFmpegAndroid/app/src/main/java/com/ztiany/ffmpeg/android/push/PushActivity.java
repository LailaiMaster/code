package com.ztiany.ffmpeg.android.push;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ztiany.ffmpeg.android.FFmpeg;

import java.io.File;

/**
 * @author Ztiany
 *         Email: ztiany3@gmail.com
 *         Date : 2018-03-17 14:23
 */
public class PushActivity extends AppCompatActivity {

    private Button mStartBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);


        mStartBtn = new Button(this);
        mStartBtn.setText("开始");
        ll.addView(mStartBtn);


        setContentView(ll);

        setOnClick();
    }

    private void setOnClick() {
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPush();
            }
        });
    }

    private void doPush() {
        File file = new File(Environment.getExternalStorageDirectory(), "input.mp4");
        if (!file.exists()) {
            Toast.makeText(this, file.getAbsolutePath() + " 不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        new FFmpeg().push(file.getAbsolutePath(), "rtmp://39.108.56.76:1935/live/test");
    }


}
