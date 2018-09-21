package com.example.juyoung.waiting2.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.juyoung.waiting2.MyApplication;
import com.example.juyoung.waiting2.R;

public class LoginActivity extends AppCompatActivity {
    Button button_check;
    EditText nickname;
    EditText phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("기다림");

        button_check=(Button)findViewById(R.id.button);

        //확인 버튼 클릭시 닉네임과 전화번호는 파일로 저장되고 메인 액티비티로 넘어감
        button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickname=(EditText)findViewById(R.id.nickname_textView);
                phone=(EditText)findViewById(R.id.phone_textView);
                //닉네임과 전화번호 저장
                SharedPreferences pref=getSharedPreferences("loginInfo",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("nickname",nickname.getText().toString());
                editor.putString("phone",phone.getText().toString());
                MyApplication.user_nick=nickname.getText().toString();
                MyApplication.user_phoneNum=phone.getText().toString();
                editor.commit();
                if(nickname.getText().toString().equals("점주")){
                    Intent intent=new Intent(getApplicationContext(),MainActivityBoss.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

        });
    }
    @Override
    protected void onStart(){
        super.onStart();
    }
    @Override
    protected void onStop(){
        super.onStop();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    protected  void onResume(){
        super.onResume();
    }
    @Override
    protected void onRestart(){
        super.onRestart();
    }
}
