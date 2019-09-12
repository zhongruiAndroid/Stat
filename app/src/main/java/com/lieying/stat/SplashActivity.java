package com.lieying.stat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.tj.TJ;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TJ.init(this);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent();
        setIntent(intent.putExtra(TJ.TJ_IGNORE_ADVERT_PAGE,TJ.TJ_IGNORE_ADVERT_PAGE));

        findViewById(R.id.btIntoMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
            }
        });


        App.get(this);
        App.getNavigationBarHeight(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        App.get(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        TJ.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
//        TJ.onPause(this);
    }
}
