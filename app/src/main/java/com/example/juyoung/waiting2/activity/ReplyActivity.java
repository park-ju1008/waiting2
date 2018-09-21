package com.example.juyoung.waiting2.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.juyoung.waiting2.MyApplication;
import com.example.juyoung.waiting2.MyDataBase;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.Reply;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReplyActivity extends AppCompatActivity {
    EditText content;
    TextView ok_view;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_write);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        content=(EditText)findViewById(R.id.content_view);
        ok_view=(TextView)findViewById(R.id.ok_view);
        ok_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=getIntent();
                int id=intent.getIntExtra("id",0);
                //현재 날짜를 구하는 코드
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String getTime = sdf.format(date);

                Reply reply=new Reply(MyApplication.user_nick,content.getText().toString(),0,getTime);
                MyDataBase.getInstance(view.getContext()).saveReply(id,reply);
                Bundle bundle=new Bundle();
                bundle.putSerializable("reply",reply);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ok_view.setText("완료("+charSequence.length()+"/2000)");
                if(charSequence.length()>0){
                    ok_view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    ok_view.setClickable(true);
                }
                else{
                    ok_view.setBackgroundColor(Color.parseColor("#d0d0d0"));
                    ok_view.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
