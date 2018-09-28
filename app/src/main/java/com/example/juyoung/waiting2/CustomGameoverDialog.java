package com.example.juyoung.waiting2;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.juyoung.waiting2.activity.GameActivity;

public class CustomGameoverDialog extends Dialog implements View.OnClickListener{
    int score;
    Context mContext;
    TextView scoreText;
    Button restart_button,out_button;


    public CustomGameoverDialog(@NonNull Context context,int score) {
        super(context);
        mContext=context;
        this.score=score;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_gameover_dialog);
        initView();
    }

    private void initView(){
        restart_button=(Button)findViewById(R.id.button_restart);
        out_button=(Button)findViewById(R.id.button_out);
        scoreText=(TextView)findViewById(R.id.score);
        scoreText.setText(""+score);
        restart_button.setOnClickListener(this);
        out_button.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.button_restart:
                ((GameActivity)mContext).gameRestart();
                dismiss();
                break;
            case R.id.button_out:
                ((GameActivity)mContext).finish();
                dismiss();
                break;
                default:
        }
    }
}
