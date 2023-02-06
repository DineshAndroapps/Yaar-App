package com.yaar.shortvideoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class BaseActivity extends AppCompatActivity {

    public BaseActivity() {
        LanguageUtils.updateConfig(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageUtils.init(this);
//        setContentView(R.layout.activity_base);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}