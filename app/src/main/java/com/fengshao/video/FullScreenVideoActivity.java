package com.fengshao.video;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;

/**
 * Created by qianzise on 2015/12/7.
 */
public class FullScreenVideoActivity extends AppCompatActivity {
    public static final String URL_PATH = "FullScreenVideoActivity.URL";
    private Uri uri = null;
    private MyVideoView videoView = null;
    private MediaController controller = null;
    private AudioManager audioManager = null;
    private int bri = 0;//亮度
    private int bri_mode = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;//亮度模式


    private float y;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreenvideo);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        if (getIntent() != null) {
            uri = Uri.parse(getIntent().getStringExtra(URL_PATH));
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
            }


            videoView = (MyVideoView) findViewById(R.id.videoView);
            controller = new MediaController(this);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            controller.setVisibility(View.VISIBLE);
            videoView.setMediaController(controller);
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            try {
                //获取亮度和亮度模式
                bri = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                bri_mode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }


            //扩充到成功layout
            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            videoView.setLayoutParams(layoutParams);

            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    controller.show(1000);
                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN: {
                            //按下,开始捕捉运动
                            y = event.getY();

                        }
                        break;

                        case MotionEvent.ACTION_UP: {
                            //松开,取消捕捉运动
                            y = 0;

                        }
                        break;

                        case MotionEvent.ACTION_MOVE: {
                            float temp_y = event.getY();

                            if (event.getX() > 0 && event.getX() < (videoView.getWidth() / 2)) {
                                //左屏幕滑动


                                if (y > temp_y && (y - temp_y > 10)) {
                                    //增加亮度
                                    bri += 10;


                                } else if (y < temp_y && (y - temp_y) < -10) {
                                    //减少亮度
                                    bri -= 10;
                                }
                                if (bri >= 255) {
                                    bri = 255;
                                } else if (bri <= 0) {
                                    bri = 0;
                                }
                                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, bri);
                                y = temp_y;


                            } else if (event.getX() < videoView.getWidth() && event.getX() > (videoView.getWidth() / 2)) {
                                //右屏幕滑动,音量控制
                                if (y > temp_y && (y - temp_y > 10)) {
                                    //减少音量
                                    audioManager.adjustStreamVolume(
                                            AudioManager.STREAM_MUSIC,
                                            AudioManager.ADJUST_RAISE,
                                            AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);


                                } else if (y < temp_y && (y - temp_y) < -10) {
                                    //增加音量
                                    audioManager.adjustStreamVolume(
                                            AudioManager.STREAM_MUSIC,
                                            AudioManager.ADJUST_LOWER,
                                            AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                                }
                                y = temp_y;

                            }
                        }

                        break;

                    }

                    return true;
                }
            });

            videoView.setVideoURI(uri);
            videoView.start();
            videoView.requestFocus();


        }


    }


    @Override
    protected void onStop() {
        super.onStop();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, bri_mode);
    }
}
