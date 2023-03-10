package com.yaar.shortvideoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import com.yaar.shortvideoapp.Main_Menu.MainMenuActivity;
import com.yaar.shortvideoapp.SimpleClasses.Variables;

public class Splash_A extends BaseActivity {


    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);


        Variables.sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);

        countDownTimer = new CountDownTimer(200, 100) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                if (LanguageUtils.isLanguageSelected(Splash_A.this)) {
                    Intent intent = new Intent(Splash_A.this, MainMenuActivity.class);

                    if (getIntent().getExtras() != null) {
                        intent.putExtras(getIntent().getExtras());
                        setIntent(null);
                    }
                    startActivity(intent);
                } else {
                    startActivity(new Intent(Splash_A.this, LanguageActivity.class));
                }


                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                finish();

            }
        }.start();


        final String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        SharedPreferences.Editor editor2 = Variables.sharedPreferences.edit();
        editor2.putString(Variables.device_id, android_id).commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

}
