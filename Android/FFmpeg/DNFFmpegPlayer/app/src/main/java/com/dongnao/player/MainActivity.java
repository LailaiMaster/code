package com.dongnao.player;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SystemMediaSelector mSystemMediaSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSystemMediaSelector = new SystemMediaSelector(path -> startActivity(PlayActivity.newIntent(MainActivity.this, path)), this);
        findViewById(R.id.btnSelectFile).setOnClickListener(v -> mSystemMediaSelector.takeFile());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSystemMediaSelector.onActivityResult(requestCode, resultCode, data);
    }

}