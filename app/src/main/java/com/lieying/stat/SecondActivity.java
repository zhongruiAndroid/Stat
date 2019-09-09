package com.lieying.stat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.tj.LG;
import com.github.tj.TJ;
import com.github.tj.TJStatCore;

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
        LG.e("SecondActivity=====================onResume");
    }

    @Override
    protected void onPause() {
        TJ.onPause(this);
        super.onPause();
        LG.e("SecondActivity=====================onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LG.e("SecondActivity=====================onStop");
    }
}
