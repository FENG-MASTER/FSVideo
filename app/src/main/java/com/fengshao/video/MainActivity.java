package com.fengshao.video;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=new Intent(MainActivity.this,FullScreenVideoActivity.class);
        intent.putExtra(FullScreenVideoActivity.URL_PATH, Environment.getExternalStorageDirectory() + "/1.MP4");
        //  intent.putExtra(FullScreenVideoActivity.URL_PATH,"http://192.168.1.101:3000/1.MP4");
        startActivity(intent);
    }
}
