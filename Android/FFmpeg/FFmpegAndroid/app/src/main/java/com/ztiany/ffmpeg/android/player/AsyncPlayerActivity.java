package com.ztiany.ffmpeg.android.player;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.ztiany.ffmpeg.android.FFmpeg;
import com.ztiany.ffmpeg.android.R;

import java.io.File;

/**
 * 实例代码
 */
public class AsyncPlayerActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payler);
        videoView = findViewById(R.id.video_view);
    }

    public void mPlay1(View btn) {
        File file = new File(Environment.getExternalStorageDirectory(), "input.mp4");
        if (!file.exists()) {
            Toast.makeText(this, file.getAbsolutePath() + " 不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        //Surface传入到Native函数中，用于绘制
        Surface surface = videoView.getHolder().getSurface();
        new FFmpeg().play(file.getAbsolutePath(), surface);
    }

    /*
    音频方法播放：
        1. AudioTrack(简单方便)
        2. OpenSl ES
     */
    public void mPlay2(View btn) {


    }

}
