package com.lieying.stat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.tj.TJ;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btJump).setOnClickListener(this);

        TJ.init(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btJump:
                startActivity(new Intent(this,SecondActivity.class));
            break;
        }
    }
}
