package com.dongnao.player;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.WindowManager;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

/**
 * @author Ztiany
 * Email: ztiany3@gmail.com
 * Date : 2020-05-22 13:40
 */
public class PlayActivity extends AppCompatActivity {

    private static final String URL_KEY = "url_key";

    private DNPlayer mDNPlayer;

    public static Intent newIntent(Context context, String url) {
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra(URL_KEY, url);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_play);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);

        mDNPlayer = new DNPlayer();
        mDNPlayer.setSurfaceView(surfaceView);
        mDNPlayer.setDataSource(getIntent().getStringExtra(URL_KEY));

        mDNPlayer.setOnPrepareListener(() -> AndroidSchedulers.mainThread().scheduleDirect(() -> mDNPlayer.start()));
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_play);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        mDNPlayer.setSurfaceView(surfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDNPlayer.prepare();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDNPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDNPlayer.destroy();
    }

}
