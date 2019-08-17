package com.example.anaban;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import static com.example.anaban.MainActivity.TAG;

public class CoverActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initCoverView();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "run: start sleep");
                    Thread.sleep(1 * 1000);
                    Log.d(TAG, "run: sleep done");
                    startActivity(new Intent(CoverActivity.this, MainActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initCoverView() {
        FrameLayout frameLayout = new FrameLayout(this);
//        frameLayout.setBackgroundResource(R.drawable.anaban_cover);
        ImageView coverImageView = new ImageView(this);
        coverImageView.setImageResource(R.drawable.anaban_cover);
        coverImageView.setAdjustViewBounds(true);
        coverImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        FrameLayout.LayoutParams layoutParams= new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        frameLayout.addView(coverImageView, layoutParams);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        ActionBar actionBar = getActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
//        Window window = getWindow();
//        // 如果需要设置全屏
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            WindowManager.LayoutParams lp = window.getAttributes();
//            window.setAttributes(lp);
//        }

        setContentView(frameLayout);
    }
}
