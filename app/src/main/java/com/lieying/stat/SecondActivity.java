package com.lieying.stat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.tj.LG;
import com.github.tj.TJ;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    @Override
    protected void onResume() {
        TJ.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        TJ.onPause(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
