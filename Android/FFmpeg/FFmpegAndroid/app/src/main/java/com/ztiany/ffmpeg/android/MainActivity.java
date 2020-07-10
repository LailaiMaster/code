package com.ztiany.ffmpeg.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ztiany.ffmpeg.android.player.AsyncPlayerActivity;
import com.ztiany.ffmpeg.android.player.SyncPlayerActivity;
import com.ztiany.ffmpeg.android.push.PushActivity;
import com.ztiany.ffmpeg.android.test.DecodeToYUV420Activity;
import com.ztiany.ffmpeg.android.test.TestActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void testFFmpeg(View view) {
        startActivity(new Intent(this, TestActivity.class));
    }

    public void pushStream(View view) {
        startActivity(new Intent(this, PushActivity.class));
    }

    public void testDecode(View view) {
        startActivity(new Intent(this, DecodeToYUV420Activity.class));
    }

    public void asyncPlayer(View view) {
        startActivity(new Intent(this, AsyncPlayerActivity.class));
    }

    public void syncPlayer(View view) {
        startActivity(new Intent(this, SyncPlayerActivity.class));
    }

}