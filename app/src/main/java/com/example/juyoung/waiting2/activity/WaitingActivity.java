package com.example.juyoung.waiting2.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.juyoung.waiting2.R;

public class WaitingActivity extends AppCompatActivity {
    private ImageView first_img_view;
    private ImageView second_img_view;
    private TextView nowaiting_view;
    private TextView branchName_view;
    private LinearLayout waiting_linear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        SharedPreferences pref=getSharedPreferences("waiting_num", Context.MODE_PRIVATE);
        int num=pref.getInt("num",-1);
        if(num>0) {
            //대기표 없다는 표시 안보이게
            nowaiting_view = (TextView) findViewById(R.id.nowaiting_textView);
            nowaiting_view.setVisibility(TextView.INVISIBLE);
            //대기표 나타나게
            waiting_linear = (LinearLayout) findViewById(R.id.waiting_Linear);
            waiting_linear.setVisibility(LinearLayout.VISIBLE);
            //상호명 출력
            branchName_view = (TextView) findViewById(R.id.name_textView);
            branchName_view.setText("< " + pref.getString("name", null) + " >");
            //대기표 번호 이미지 변경
            first_img_view = (ImageView) findViewById(R.id.firstNum_view);
            second_img_view = (ImageView) findViewById(R.id.secondNum_view);
            first_img_view.setImageResource(numToImage(num / 10));
            second_img_view.setImageResource(numToImage(num % 10));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public int numToImage(int num){
        int inum=0;
        switch (num){
            case 0:
                inum=R.drawable.zero;
                break;
            case 1:
                inum=R.drawable.one;
                break;
            case 2:
                inum=R.drawable.two;
                break;
            case 3:
                inum=R.drawable.three;
                break;
            case 4:
                inum=R.drawable.four;
                break;
            case 5:
                inum=R.drawable.five;
                break;
            case 6:
                inum=R.drawable.six;
                break;
            case 7:
                inum=R.drawable.seven;
                break;
            case 8:
                inum=R.drawable.eight;
                break;
            case 9:
                inum=R.drawable.nine;
                break;
            default:

        }
        return inum;
    }
}
