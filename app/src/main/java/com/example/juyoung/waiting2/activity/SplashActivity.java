package com.example.juyoung.waiting2.activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.example.juyoung.waiting2.MyApplication;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.activity.MainActivity;
import com.example.juyoung.waiting2.activity.MainActivityBoss;
import com.facebook.stetho.Stetho;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Stetho.initializeWithDefaults(this);

        LottieAnimationView lottieAnimationView=(LottieAnimationView)findViewById(R.id.lottie);
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                Log.v("로티","시작");
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                //로그인 정보를 불러온다.
                SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
                MyApplication.user_nick = pref.getString("nickname", null);
                MyApplication.user_phoneNum=pref.getString("phone",null);
                MyApplication.user_Image=pref.getString("profile",null);
                // MainActivity.class 자리에 다음에 넘어갈 액티비티를 넣어주기
                if (MyApplication.user_nick !=null&&MyApplication.user_nick .equals("점주")) {
                    Intent intent = new Intent(SplashActivity.this, MainActivityBoss.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                Log.v("로티","반복중");
            }
        });





    }
}
